package pl.edu.fuw.fid.signalanalysis.dtf;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.complex.ComplexField;
import org.apache.commons.math.linear.Array2DRowFieldMatrix;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.FieldLUDecompositionImpl;
import org.apache.commons.math.linear.FieldMatrix;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import pl.edu.fuw.fid.signalanalysis.MultiSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ArModel {

	private final int C;
	private final RealMatrix[] A;
	private final double freqSampling;

	public ArModel(int C, RealMatrix[] A, double freqSampling) {
		for (RealMatrix M : A) {
			if (M.getRowDimension() != C || M.getColumnDimension() != C) {
				throw new RuntimeException("matrix dimension mismatch");
			}
		}
		this.C = C;
		this.A = A;
		this.freqSampling = freqSampling;
	}

	public static ArModel compute(MultiSignal signal, int order) {
		final int N = signal.getSampleCount();
		final int C = signal.getChannelCount();

		// calculating mean values
		double[] avg = new double[C];
		for (int c=0; c<C; ++c) {
			for (double x : signal.getData(c)) {
				avg[c] += x;
			}
			avg[c] /= N;
		}

		// calculating variances
		double[] std = new double[C];
		for (int c=0; c<C; ++c) {
			for (double x : signal.getData(c)) {
				double diff = x - avg[c];
				std[c] += diff * diff;
			}
			std[c] = Math.sqrt(std[c] / N);
		}

		// calculating lag correlations
		RealMatrix[] R = new RealMatrix[1+order];
		for (int s=0; s<=order; ++s) {
			R[s] = new Array2DRowRealMatrix(C, C);
			for (int i=0; i<C; ++i) {
				final double[] iData = signal.getData(i);
				for (int j=0; j<C; ++j) {
					final double[] jData = signal.getData(j);
					double sum = 0;
					for (int t=0; t<N-s; ++t) {
						// causality i -> j, so t < t+s
						double iValue = (iData[t] - avg[i]) / std[i];
						double jValue = (jData[t+s] - avg[j]) / std[j];
						sum += iValue * jValue;
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
		RealMatrix bigMatrixInverse =  new LUDecompositionImpl(bigMatrix).getSolver().getInverse();
		// TODO what if not invertible?
		RealMatrix bigSolution = bigMatrixInverse.multiply(bigColumn);

		RealMatrix[] A = new RealMatrix[order];
		for (int s=0; s<order; ++s) {
			A[s] = bigSolution.getSubMatrix(s*C, (s+1)*C-1, 0, C-1);
		}
		return new ArModel(C, A, signal.getSamplingFrequency());
	}

	public RealMatrix computeTransferMatrix(double freq, boolean normalize) {
		FieldMatrix<Complex> S = new Array2DRowFieldMatrix<Complex>(ComplexField.getInstance(), C, C);
		for (int s=0; s<=A.length; ++s) {
			Complex exp = new Complex(0, -2*Math.PI*s*freq/freqSampling).exp();
			for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
				double val = (s > 0) ? A[s-1].getEntry(i, j) : (i==j ? -1.0 : 0.0);
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
		return DTF;
	}

	public int getChannelCount() {
		return C;
	}

	public double getSamplingFrequency() {
		return freqSampling;
	}

}
