package pl.edu.fuw.fid.signalanalysis.ica;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.EigenDecomposition;
import org.apache.commons.math.linear.EigenDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

/**
 * The core calculations of ICA (Independent Component Analysis) method.
 * This is an implementation of FastICA algorithm, according to:
 * Patil D., Das N., Routray A.: Implementation of Fast-ICA / A Performance
 * Based Comparison Between Floating Point and Fixed Point DSP Platform,
 * MEASUREMENT SCIENCE REVIEW, Volume 11, No. 4, 2011
 *
 * @author ptr@mimuw.edu.pl
 */
public class IcaMethodComputer {

	/**
	 * Performs the calculations.
	 *
	 * @param data  matrix of multichannel data (channels × samples)
	 * @return  matrix of coefficients corresponding to independent components,
	 * row 0 corresponds to first component, and so on,
	 * with row count (# components) = column count (# given channels)
	 *
	 * @throws IcaMethodException
	 */
	public RealMatrix compute(RealMatrix data) throws IcaMethodException {
		final int C = data.getRowDimension();
		if (C == 0) {
			throw new IcaMethodException("no channels selected");
		}
		final int N = data.getColumnDimension();
		if (N == 0) {
			throw new IcaMethodException("signal is empty");
		}

		// mean elimination
		for (int i=0; i<C; ++i) {
			double mean = 0.0;
			double[] channel = data.getRow(i);
			for (int n=0; n<N; ++n) {
				mean += channel[n];
			}
			mean /= N;
			for (int n=0; n<N; ++n) {
				data.addToEntry(i, n, -mean);
			}
		}

		// computation of covariance matrix
		RealMatrix V = data.multiply(data.transpose()).scalarMultiply(1.0/N);

		// computation of whitening matrix
		V = power(V, -0.5);
		for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
			double value = V.getEntry(i, j);
			if (Double.isInfinite(value) || Double.isNaN(value)) {
				throw new IcaMethodException("signal whitening failed");
			}
		}

		// whitening the signal
		RealMatrix v = V.multiply(data);

		// rows of matrix BT will be IC coefficients
		RealMatrix BT = new Array2DRowRealMatrix(C, C);

		for (int iteration=0; iteration<C; ++iteration) {

			double[] w0 = new double[C];
			// Take a random initial vector w...
			for (int i=0; i<C; ++i) {
				w0[i] = 2*Math.random() - 1;
			}
			// ... and normalize it to unity
			normalize(w0);

			while (true) {
				double[] w1 = new double[C];
				// Set ...
				for (int n=0; n<N; ++n) {
					double sum = 0.0;
					for (int i=0; i<C; ++i) {
						sum += w0[i] * v.getEntry(i, n);
					}
					sum *= sum * sum;
					for (int i=0; i<C; ++i) {
						w1[i] += v.getEntry(i, n) * sum;
					}
				}
				for (int i=0; i<C; ++i) {
					w1[i] = w1[i] / N - 3 * w0[i];
				}
				// orthogonalize to previous solutions
				for (int it=0; it<iteration; ++it) {
					double prod = product(C, BT.getRow(it), w1);
					for (int i=0; i<C; ++i) {
						w1[i] -= prod * BT.getEntry(it, i);
					}
				}
				normalize(w1);

				double w0w1 = Math.abs(product(C, w0, w1));
				w0 = w1;
				if (Math.abs(w0w1 - 1.0) < 1.0e-6) {
					break;
				}
			}
			// another solution
			BT.setRow(iteration, w0);
		}

		// final solution
		RealMatrix ICA = BT.multiply(V);

		// normalizing each component
		for (int i=0; i<C; ++i) {
			RealVector u = ICA.getRowVector(i).unitVector();
			ICA.setRowVector(i, u);
		}
		return ICA;
	}

	/**
	 * Normalize a real vector.
	 * After operation, ||w||² = w · w = 1
	 *
	 * @param w  vector (given as array) to normalize
	 */
	public static void normalize(double[] w) {
		double length = Math.sqrt(product(w.length, w, w));
		for (int i=0; i<w.length; ++i) {
			w[i] /= length;
		}
	}

	/**
	 * Raise given real symmetric matrix to any power.
	 *
	 * @param matrix  symmetric matrix
	 * @param p  real exponent
	 * @return  matrix raised to the power of p
	 */
	public static RealMatrix power(final RealMatrix matrix, final double p) {
		final EigenDecomposition eigen = new EigenDecompositionImpl(matrix, 0.0);
		final double[] rEigenValues = eigen.getRealEigenvalues();
		final int n = rEigenValues.length;
		final double[][] d = new double[n][n];
		for (int i = n - 1; i >= 0; --i) {
			d[i][i] = Math.pow(rEigenValues[i], p);
		}
		final RealMatrix res = eigen.getV().multiply((new Array2DRowRealMatrix(d)).multiply(eigen.getVT()));
		return res;
	}

	/**
	 * Compute scalar product of two real vectors.
	 * This operation is symmetric.
	 *
	 * @param N  length of both vectors
	 * @param x  first vector given as array
	 * @param y  second vector given as array
	 * @return  scalar product of two vectors
	 */
	public static double product(int N, double[] x, double[] y) {
		double sum = 0.0;
		for (int i=0; i<N; ++i) {
			sum += x[i] * y[i];
		}
		return sum;
	}

}
