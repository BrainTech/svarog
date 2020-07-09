/* Chebyshev1IIRDesigner.java created 2010-09-12
 *
 */

package org.signalml.math.iirdesigner;

import org.apache.commons.math.complex.Complex;
import org.signalml.math.iirdesigner.math.SpecialMath;

/**
 * This class represents a designer which is capable of designing a Chebyshev I filter.
 *
 * @author Piotr Szachewicz
 */
class Chebyshev1IIRDesigner extends ChebyshevIIRDesigner {

	/**
	 * Returns zeros, poles, and gain of an normalized prototype analog
	 * lowpass Chebyshev I filter which meets the specification.
	 *
	 * @param filterOrder the order of the prototype
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return zeros, poles and gain of the filter prototype which meets the given specification
	 */
	@Override
	protected FilterZerosPolesGain calculatePrototype(int filterOrder, double gpass, double gstop) {
		return calculatePrototype(filterOrder, gpass);
	}

	/**
	 * Returns zeros, poles, and gain of an normalized prototype analog
	 * lowpass Chebyshev I filter which meets the specification.
	 *
	 * @param filterOrder the order of the prototype
	 * @param gpass the maximum loss in the passband [dB]
	 * @return zeros, poles and gain of the filter prototype which meets the given specification
	 */
	protected FilterZerosPolesGain calculatePrototype(int filterOrder, double gpass) {

		double epsilon = Math.sqrt(Math.pow(10, 0.1 * gpass) - 1.0);
		double mu = 1.0 / filterOrder * Math.log((1.0 + Math.sqrt(1.0 + epsilon * epsilon)) / epsilon);

		Complex[] poles = new Complex[filterOrder];

		//calculate the poles
		double theta;
		for (int i = 1; i <= filterOrder; i++) {
			theta = Math.PI / 2.0 * (2 * i - 1.0) / filterOrder;
			poles[i-1] =  new Complex(-Math.sinh(mu) * Math.sin(theta), Math.cosh(mu) * Math.cos(theta));
		}

		//calculate gain
		Complex product = new Complex(1.0, 0.0);
		for (Complex pole : poles) {
			product = product.multiply(pole.negate());
		}
		double gain = product.getReal();

		if (SpecialMath.isEven(filterOrder))
			gain = gain / Math.sqrt(1 + epsilon * epsilon);

		//return zeros, poles & gain
		FilterZerosPolesGain zpk = new FilterZerosPolesGain(new Complex[0], poles, gain);
		return zpk;

	}

	/**
	 * Calculates the natural (critical) frequencies for a given specification (the 3dB frequency)
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param filterOrder the order of the filter to be designed (can be calculated using {@link AbstractIIRDesigner#calculateFilterOrder(org.signalml.math.iirdesigner.FilterType, double[], double[], double, double, boolean) })
	 * @param wp passband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param ws stopband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @param analog true to design an analog filter, false to design a digital filter
	 * @return the critical frequencies
	 */
	@Override
	protected double[] calculateNaturalFrequency(FilterType type, int filterOrder, double[] wp, double[] ws, double gpass, double gstop, boolean analog) {

		if (type.isBandstop()) {

			double[] passb;
			double[] stopb;
			if (!analog) {
				passb = prewarpFrequencies(wp);
				stopb = prewarpFrequencies(ws);
			}
			else {
				passb = wp.clone();
				stopb = ws.clone();
			}

			double[] result = optimizeBandstopFilterPassbandFrequencies(passb, stopb, gpass, gstop);
			passb[0] = result[0];
			passb[1] = result[1];

			if (!analog)
				passb = unwarpFrequencies(passb);
			return passb;

		}

		return wp;

	}

}