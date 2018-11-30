/* ChebyshevIIRDesigner.java created 2010-09-14
 *
 */

package org.signalml.math.iirdesigner;

import org.signalml.math.iirdesigner.math.SpecialMath;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * This abstract class represents a designer which is capable of designing a Chebyshev filter.
 *
 * @author Piotr Szachewicz
 */
abstract class ChebyshevIIRDesigner extends AbstractIIRDesigner {

	/**
	 * Calculates the minimum filter order of the filter which would meets the given specifications.
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

		double GSTOP = Math.pow(10.0, 0.1*Math.abs(gstop));
		double GPASS = Math.pow(10.0, 0.1*Math.abs(gpass));

		int filterOrder = (int)(Math.ceil(SpecialMath.acosh(Math.sqrt((GSTOP - 1.0) / (GPASS - 1.0))) / SpecialMath.acosh(frequencyRatio)));

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

		double filterOrder = SpecialMath.acosh(Math.sqrt((GSTOP - 1.0) / (GPASS - 1.0))) / SpecialMath.acosh(nat);

		return filterOrder;

	}

}