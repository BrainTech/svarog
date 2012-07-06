package org.signalml.domain.signal.filter.iir;


/**
 * This class represents a single IIR filter implemented as
 * direct II transposed structure.
 *
 * Between calling the {@link IIRFilterEngine#filter(double[])} method
 * it remembers its state, so calling this method on whole signal
 * gives the same results as calling it first on one half of the
 * signal and then on the second. (See {@link IIRFilterEngineTest} for
 * reference}).
 *
 * @author Piotr Szachewicz
 */
public class IIRFilterEngine {

	/**
	 * feedforward coefficients of the filter
	 */
	private double[] bCoefficients;

	/**
	 * feedback coefficients of the filter
	 */
	private double[] aCoefficients;

	/**
	 * The state of the filter delays.
	 */
	private double[] initialConditions;

	/**
	 * Creates this filter.
	 *
	 * @param bCoefficients feedforward coefficients of the filter
	 * @param aCoefficients feedback coefficients of the filter
	 * @param initialConditions initial conditions of the filter delays
	 */
	public IIRFilterEngine(double[] bCoefficients, double[] aCoefficients, double[] initialConditions) {
		super();
		this.initialConditions = initialConditions;
		this.bCoefficients = bCoefficients;
		this.aCoefficients = aCoefficients;
	}

	/**
	 * Creates this filter.
	 *
	 * @param bCoefficients feedforward coefficients of the filter
	 * @param aCoefficients feedback coeffcients of the filter
	 */
	public IIRFilterEngine(double[] bCoefficients, double[] aCoefficients) {
		super();
		this.bCoefficients = bCoefficients;
		this.aCoefficients = aCoefficients;

		resetInitialConditions();
	}

	/**
	 * Resets the state of the filter delays to zeros.
	 */
	protected void resetInitialConditions() {
		int size = Math.max(bCoefficients.length, aCoefficients.length) - 1;
		initialConditions = new double[size];

		for (int i = 0; i < initialConditions.length; i++) {
			initialConditions[i] = 0;
		}
	}

	/**
	 * Filters the given signal.
	 * @param input the input signal to be filtered
	 * @return the input signal after filtering
	 */
	public double[] filter(double[] input) {
		/**
		 * The filter function is implemented as a direct II transposed structure.
		 * It is implemented as the lfilter function in the Scipy library.
		 * Compare with Scipy source code: scipy/signal/lfilter.c#@NAME@_filt
		 */

		int bi, ai, zi;
		double[] filtered = new double[input.length];

		for (int n = 0; n < input.length; n++) {
			bi = 0;
			ai = 0;
			zi = 0;

			if (bCoefficients.length > 1) {
				filtered[n] = initialConditions[zi] + bCoefficients[bi] / aCoefficients[0] * input[n];
				bi++;
				ai++;

				for (; zi < bCoefficients.length - 2; zi++) {
					initialConditions[zi] = initialConditions[zi + 1]
											+ input[n] * bCoefficients[bi] / aCoefficients[0]
											- filtered[n] * aCoefficients[ai] / aCoefficients[0];

					bi++;
					ai++;
				}
				initialConditions[zi] = input[n] * bCoefficients[bi] / aCoefficients[0]
										- filtered[n] * aCoefficients[ai] / aCoefficients[0];
			} else {
				filtered[n] = input[n] * bCoefficients[bi /* 0 */] / aCoefficients[0];
			}
		}

		return filtered;
	}

}
