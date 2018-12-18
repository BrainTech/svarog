package pl.edu.fuw.fid.signalanalysis.dtf;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.complex.ComplexField;
import org.apache.commons.math.linear.Array2DRowFieldMatrix;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.FieldLUDecompositionImpl;
import org.apache.commons.math.linear.FieldMatrix;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import static org.signalml.plugin.i18n.PluginI18n._;

/**
 * AR model class. Contains a factory method compute, calculating
 * AR model coefficients from Yule-Walker method (with whitening).
 *
 * @author ptr@mimuw.edu.pl
 */
public class ArModel {

	private final int C;
	private final double detV;
	private final RealMatrix V;
	private final RealMatrix[] A;
	private final double freqSampling;

	/**
	 * Create a new AR model instance from precomputed matrix of coefficients
	 * and error covariance matrix.
	 *
	 * @param C  number of channels
	 * @param A  coefficient matrix
	 * @param V  error covariance matrix
	 * @param freqSampling  sampling frequency (Hz)
	 */
	public ArModel(int C, RealMatrix[] A, RealMatrix V, double freqSampling) {
		for (RealMatrix M : A) {
			if (M.getRowDimension() != C || M.getColumnDimension() != C) {
				throw new RuntimeException(_("matrix dimension mismatch"));
			}
		}
		this.C = C;
		this.A = A;
		this.V = V;
		this.detV = new LUDecompositionImpl(V).getDeterminant();
		this.freqSampling = freqSampling;
	}

	/**
	 * Fit AR model to given multi-channel signal, using the Yule-Walker method.
	 * Signal will be whitened (mean subtracted and divided by standard variation)
	 * prior to calculations.
	 *
	 * @param X  multichannel signal data, rows=channels, columns=samples
	 * @param freqSampling  sampling frequency (Hz)
	 * @param order  order > 0 of the AR model to be fit
	 * @return  AR model instance with computed coefficients
	 */
	public static ArModel compute(RealMatrix X, double freqSampling, int order) {
		final int N = X.getColumnDimension();
		final int C = X.getRowDimension();

		// whitening matrix data
		for (int c=0; c<C; ++c) {
			double[] row = X.getRow(c);
			double sum = 0.0, sum2 = 0.0;
			for (double x : row) {
				sum += x;
				sum2 += x * x;
			}
			double EX = sum / N;
			double EX2 = sum2 / N;
			double D = Math.sqrt(EX2 - EX*EX);
			for (int i=0; i<N; ++i) {
				X.setEntry(c, i, (row[i] - EX) / D);
			}
		}

		// calculating lag correlations
		RealMatrix[] R = new RealMatrix[1+order];
		for (int s=0; s<=order; ++s) {
			R[s] = new Array2DRowRealMatrix(C, C);
			for (int i=0; i<C; ++i) {
				for (int j=0; j<C; ++j) {
					double sum = 0;
					for (int t=0; t<N-s; ++t) {
						// causality i -> j, so t < t+s
						sum += X.getEntry(i, t) * X.getEntry(j, t+s);
					}
					R[s].setEntry(i, j, sum / N);
				}
			}
		}

		// matrices for Yule-Walker equations
		RealMatrix bigMatrix = new Array2DRowRealMatrix(order*C, order*C);
		RealMatrix bigColumn = new Array2DRowRealMatrix(order*C, C);
		for (int i=0; i<order; ++i) for (int j=0; j<order; ++j) {
			int s = i - j;
			RealMatrix block = (s < 0) ? R[-s].transpose() : R[s];
			bigMatrix.setSubMatrix(block.getData(), i*C, j*C);
		}
		for (int i=0; i<order; ++i) {
			RealMatrix block = R[i+1];
			bigColumn.setSubMatrix(block.getData(), i*C, 0);
		}

		// solution of Yule-Walker equations
		RealMatrix bigMatrixInverse = new LUDecompositionImpl(bigMatrix).getSolver().getInverse();
		// TODO what if not invertible?
		RealMatrix bigSolution = bigMatrixInverse.multiply(bigColumn);

		RealMatrix[] A = new RealMatrix[1+order];
		A[0] = MatrixUtils.createRealIdentityMatrix(C).scalarMultiply(-1);
		for (int s=1; s<=order; ++s) {
			A[s] = bigSolution.getSubMatrix((s-1)*C, s*C-1, 0, C-1);
		}

		// computing residual error
		RealMatrix V = new Array2DRowRealMatrix(C, C);
		for (int s=0; s<=order; ++s) {
			V = V.subtract(A[s].transpose().multiply(R[s]));
		}
		return new ArModel(C, A, V, freqSampling);
	}

	public ArModelData[][] computeSpectralData(int spectrumSize, boolean normalized) {
		ArModelData[][] data = new ArModelData[C][C];
		for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
			data[i][j] = new ArModelData(spectrumSize);
		}
		final double nyquist = 0.5 * getSamplingFrequency();
		for (int f=0; f<spectrumSize; ++f) {
			double freq = f * nyquist / spectrumSize;
			RealMatrix H = computeTransferMatrix(freq, normalized);


			for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
				double value = H.getEntry(i, j);
				data[i][j].freqcs[f] = freq;
				data[i][j].values[f] = value;
			}
		}
		return data;
	}

	public RealMatrix computeTransferMatrix(double freq, boolean normalize) {
		FieldMatrix<Complex> S = new Array2DRowFieldMatrix<Complex>(ComplexField.getInstance(), C, C);
		for (int s=0; s<A.length; ++s) {
			Complex exp = new Complex(0, -2*Math.PI*s*freq/freqSampling).exp();
			for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
				double val = A[s].getEntry(i, j);
				S.addToEntry(i, j, exp.multiply(val));
			}
		}
		FieldMatrix<Complex> H = new FieldLUDecompositionImpl(S).getSolver().getInverse();

		RealMatrix DTF = new Array2DRowRealMatrix(C, C);
		for (int i=0; i<C; ++i) {
			for (int j=0; j<C; ++j) {
				Complex h = H.getEntry(i, j);
				double re = h.getReal();
				double im = h.getImaginary();
				DTF.setEntry(i, j, re*re + im*im);
			}
		}
		if (normalize) {
			// entry (i, j) represents causality i ->
			for (int j=0; j<C; ++j) {
				double norm = 0;
				for (int i=0; i<C; ++i) {
					norm += DTF.getEntry(i, j);
				}
				norm = 1.0 / norm;
				for (int i=0; i<C; ++i) {
					DTF.multiplyEntry(i, j, norm);
				}
			}
		}

		FieldMatrix<Complex> Hplus = new FieldLUDecompositionImpl(S).getSolver().getInverse();
		FieldMatrix<Complex> cV = new Array2DRowFieldMatrix<Complex>(ComplexField.getInstance(), C, C);
		for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
			cV.setEntry(i, j, new Complex(V.getEntry(i, j), 0));
			Hplus.setEntry(i, j, H.getEntry(j, i).conjugate());
		}
		FieldMatrix<Complex> spectrum = H.multiply(cV).multiply(Hplus);
		for (int i=0; i<C; ++i) {
			DTF.setEntry(i, i, spectrum.getEntry(i, i).abs());
		}
		return DTF;
	}

	private static String exportMatrix(RealMatrix M) {
		boolean comma = false;
		String result = "[";
		for (int r=0; r<M.getRowDimension(); ++r) {
			if (comma) result += ",";
			result += exportRow(M.getRow(r));
			comma = true;
		}
		result += "]";
		return result;
	}

	private static String exportRow(double[] row) {
		boolean comma = false;
		String result = "[";
		for (double v : row) {
			if (comma) result += ",";
			result += v;
			comma = true;
		}
		result += "]";
		return result;
	}

	public String exportCoefficients() {
		boolean comma = false;
		String result = "[";
		for (int i=1; i<A.length; ++i) {
			if (comma) result += ",";
			result += exportMatrix(A[i]);
			comma = true;
		}
		result += "]";
		return result;
	}

	public int getChannelCount() {
		return C;
	}

	public double getErrorDeterminant() {
		return detV;
	}

	public double getSamplingFrequency() {
		return freqSampling;
	}

}
