/* ButterworthIIRDesigner.java created 2010-09-12
 *
 */

package org.signalml.math.iirdesigner;

import org.apache.commons.math.complex.Complex;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * This class represents a designer which is capable of designing a Butterworth filter.
 *
 * @author Piotr Szachewicz
 */
class ButterworthIIRDesigner extends AbstractIIRDesigner {

	/**
	 * Returns zeros, poles, and gain of a normalized prototype analog
	 * lowpass Butterworth filter which meets the given specification.
	 *
	 * @param filterOrder the order of the prototype
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return zeros, poles and gain of the filter prototype which meets the given specification
	 */
	@Override
	protected FilterZerosPolesGain calculatePrototype(int filterOrder, double gpass, double gstop) {
		return calculatePrototype(filterOrder);
	}

	/**
	 * Returns zeros, poles, and gain of a normalized prototype analog
	 * lowpass filter which meets the specification.
	 *
	 * @param filterOrder the order of the prototype
	 * @return zeros, poles and gain of the filter prototype which meets the given specification
	 */
	protected FilterZerosPolesGain calculatePrototype(int filterOrder) {

		Complex[] poles = new Complex[filterOrder];

		for (int i = 0; i < filterOrder; i++)
			poles[i] = (new Complex(0, Math.PI * (2 * i + 1) / (2 * filterOrder))).exp().multiply(imaginaryUnit);

		FilterZerosPolesGain zpk = new FilterZerosPolesGain(new Complex[0], poles, 1.0);
		return zpk;

	}

	/**
	 * Calculates the natural (critical) frequencies for a given specification (the 3dB frequency)
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param filterOrder the order of the filter to be designed
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

		double frequencyRatio = calculateFrequencyRatio(type, passb, stopb, gpass, gstop);

		double W0 = frequencyRatio / Math.pow((Math.pow(10.0, 0.1 * Math.abs(gstop)) - 1), (1.0 / (2.0 * filterOrder)));
		double[] WN = new double[2];

		if (type.isLowpass())
			WN[0] = W0 * passb[0];
		else if (type.isHighpass())
			WN[0] = passb[0] / W0;
		else if (type.isBandstop()) {

			double[] result = optimizeBandstopFilterPassbandFrequencies(passb, stopb, gpass, gstop);
			passb[0] = result[0];
			passb[1] = result[1];

			WN[0] = ((passb[1] - passb[0]) + Math.sqrt(Math.pow((passb[1] - passb[0]), 2) +
					 4 * Math.pow(W0, 2) * passb[0] * passb[1])) / (2 * W0);
			WN[1] = ((passb[1] - passb[0]) - Math.sqrt(Math.pow((passb[1] - passb[0]), 2) +
					 4 * Math.pow(W0, 2) * passb[0] * passb[1])) / (2 * W0);
			WN[0] = Math.abs(WN[0]);
			WN[1] = Math.abs(WN[1]);

			if (WN[0]>WN[1]) {
				double temp = WN[0];
				WN[0] = WN[1];
				WN[1] = temp;
			}

		}
		else if (type.isBandpass()) {

			WN[0] = W0 * (passb[1] - passb[0]) / 2.0 + Math.sqrt(Math.pow(-W0, 2) / 4.0 *
					Math.pow((passb[1]-passb[0]), 2) + passb[0] * passb[1]);
			WN[1] = -W0 * (passb[1]-passb[0]) / 2.0 + Math.sqrt(Math.pow(W0, 2) / 4.0 *
					Math.pow((passb[1] - passb[0]), 2) + passb[0] * passb[1]);
			WN[0] = Math.abs(WN[0]);
			WN[1] = Math.abs(WN[1]);

			if (WN[0] > WN[1]) {
				double temp = WN[0];
				WN[0] = WN[1];
				WN[1] = temp;
			}

		}

		if (!analog)
			WN = unwarpFrequencies(WN);

		return WN;

	}

	/**
	 * Calculates the minimum filter order of the filter which would meet the given specifications.
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param wp passband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param ws stopband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @param analog true to design an analog filter, false to design a digital filter
	 * @return the order of filter
	 */
	@Override
	protected int calculateFilterOrder(FilterType type, double[] wp, double[] ws, double gpass, double gstop, boolean analog) throws BadFilterParametersException {

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

		double frequencyRatio = calculateFrequencyRatio(type, passb, stopb, gpass, gstop);

		double GSTOP = Math.pow(10.0, 0.1 * Math.abs(gstop));
		double GPASS = Math.pow(10.0, 0.1 * Math.abs(gpass));

		int filterOrder = (int)(Math.ceil(Math.log10((GSTOP - 1.0) / (GPASS - 1.0)) / (2 * Math.log10(frequencyRatio))));

		if (filterOrder == 0)
			throw new BadFilterParametersException(_("Filter order is zero - check the input parameters!"));

		return filterOrder;

	}

	/**
	 * Calculates the bandstop objective function value which is used to find
	 * optimal values for passband edge frequencies (if the filter is an analog bandstop filter)
	 * to minimize the filter order.
	 *
	 * @param passbandEdge the new passband edge frequency [rad/sec.]
	 * @param variablePassbandEdgeIndex specifies which passband edge to vary (0 or 1)
	 * @param passb passband edge frequencies [rad/sec.]
	 * @param stopb stopband edge frequencies [rad/sec.]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return the filter order (possibly non-integer)
	 */
	@Override
	protected double calculateBandstopObjectiveFunctionValue(double passbandEdge, int variablePassbandEdgeIndex, double[] passb, double[] stopb, double gpass, double gstop) {

		double[] passbCopy = passb.clone();
		passbCopy[variablePassbandEdgeIndex] = passbandEdge;
		double[] possibilities = new double[2];

		possibilities[0] = stopb[0] * (passbCopy[0] - passbCopy[1]) / (Math.pow(stopb[0], 2) - passbCopy[0] * passbCopy[1]);
		possibilities[1] = stopb[1] * (passbCopy[0] - passbCopy[1]) / (Math.pow(stopb[1], 2) - passbCopy[0] * passbCopy[1]);
		double nat = Math.min(Math.abs(possibilities[0]), Math.abs(possibilities[1]));

		double GSTOP = Math.pow(10, 0.1 * Math.abs(gstop));
		double GPASS = Math.pow(10, 0.1 * Math.abs(gpass));
		double filterOrder;

		filterOrder = (Math.log10((GSTOP - 1.0) / (GPASS - 1.0)) / (2 * Math.log10(nat)));

		return filterOrder;

	}

}