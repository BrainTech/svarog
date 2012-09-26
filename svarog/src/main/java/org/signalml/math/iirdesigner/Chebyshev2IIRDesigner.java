/* Chebyshev2IIRDesigner.java created 2010-09-12
 *
 */

package org.signalml.math.iirdesigner;

import org.apache.commons.math.complex.Complex;
import java.util.ArrayList;

import org.signalml.math.iirdesigner.math.SpecialMath;

/**
 * This class represents a designer which is capable of designing a Chebyshev II.
 *
 * @author Piotr Szachewicz
 */
class Chebyshev2IIRDesigner extends ChebyshevIIRDesigner {

	/**
	 * Returns zeros, poles, and gain of an normalized prototype analog
	 * lowpass Chebyshev II filter which meets the given specification.
	 *
	 * @param filterOrder the order of the prototype
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return zeros, poles and gain of the filter prototype which meets the given specification
	 */
	@Override
	protected FilterZerosPolesGain calculatePrototype(int filterOrder, double gpass, double gstop) {
		return calculatePrototype(filterOrder, gstop);
	}

	/**
	 * Returns zeros, poles, and gain of an normalized prototype analog
	 * lowpass Chebyshev II filter which meets the specification.
	 *
	 * @param filterOrder the order of the prototype
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return zeros, poles and gain of the filter prototype which meets the given specification
	 */
	protected FilterZerosPolesGain calculatePrototype(int filterOrder, double gstop) {

		double de = 1.0 / Math.sqrt(Math.pow(10, 0.1 * gstop) - 1);
		double mu = SpecialMath.asinh(1.0 / de) / filterOrder;

		ArrayList<Complex> zerosList = new ArrayList<Complex>();
		ArrayList<Complex> polesList = new ArrayList<Complex>();

		//calculate poles and zeros
		for (int i = 1; i < 2*filterOrder; i += 2) {

			Complex pole = (new Complex(0, Math.PI * i / (2 * filterOrder) + Math.PI / 2.0)).exp();
			pole = new Complex(pole.getReal() * StrictMath.sinh(mu), pole.getImaginary() * StrictMath.cosh(mu));
			pole = new Complex(1.0, 0.0).divide(pole);
			polesList.add(pole);

			if (SpecialMath.isOdd(filterOrder))
				if (i >= filterOrder-1 && i < filterOrder+2)
					continue;

			Complex denominator = new Complex(Math.cos(i * Math.PI / (2 * filterOrder)), 0.0);
			zerosList.add((imaginaryUnit.divide(denominator)).conjugate());

		}

		//convert zeros&poles ArrayLists to arrays
		Complex[] zeros = new Complex[zerosList.size()];
		for (int i = 0; i < zerosList.size(); i++)
			zeros[i] = zerosList.get(i);

		Complex[] poles = new Complex[polesList.size()];
		for (int i = 0; i < polesList.size(); i++)
			poles[i] = polesList.get(i);

		//calculate gain
		Complex numerator = new Complex(1.0, 0.0);
		for (int i = 0; i < poles.length; i++)
			numerator = numerator.multiply(poles[i].multiply(-1.0));
		Complex denominator = new Complex(1.0, 0.0);
		for (int i = 0; i < zeros.length; i++)
			denominator = denominator.multiply(zeros[i].multiply(-1.0));
		double gain = (numerator.divide(denominator)).getReal();

		//return zeros, poles & gain
		FilterZerosPolesGain zpk = new FilterZerosPolesGain(zeros, poles, gain);
		return zpk;

	}

	/**
	 * Calculates the natural (critical) frequencies for a given specification (the 3dB frequency).
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

		double GSTOP = Math.pow(10.0, 0.1*Math.abs(gstop));
		double GPASS = Math.pow(10.0, 0.1*Math.abs(gpass));

		double newFrequency = Math.cosh(1.0 / (double)(filterOrder) * SpecialMath.acosh(Math.sqrt((GSTOP - 1.0) / (GPASS - 1.0))));
		newFrequency = 1.0 / newFrequency;

		double[] wn = new double[2];

		if (type.isLowpass())
			wn[0] = passb[0] / newFrequency;
		else if (type.isHighpass())
			wn[0] = passb[0] * newFrequency;
		else if (type.isBandstop()) {

			double[] result = optimizeBandstopFilterPassbandFrequencies(passb, stopb, gpass, gstop);
			passb[0] = result[0];
			passb[1] = result[1];

			wn[0] = (newFrequency / 2.0 * (passb[0]-passb[1])) +
					Math.sqrt(newFrequency * newFrequency * Math.pow(passb[1]-passb[0], 2) / 4.0 +
							  passb[1] * passb[0]);
			wn[1] = passb[1] * passb[0] / wn[0];

		}
		else if (type.isBandpass()) {

			wn[0] = 1.0 / (2.0*newFrequency) * (passb[0] - passb[1]) +
					Math.sqrt((passb[1]-passb[0])*(passb[1]-passb[0]) / (4.0*newFrequency*newFrequency) +
							  passb[1] * passb[0]);
			wn[1] = passb[0] * passb[1] / wn[0];

		}

		if (!analog)
			wn = unwarpFrequencies(wn);

		return wn;

	}

}