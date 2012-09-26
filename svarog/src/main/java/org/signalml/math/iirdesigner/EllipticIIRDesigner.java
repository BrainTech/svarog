/* EllipticIIRDesigner.java created 2010-09-16
 *
 */

package org.signalml.math.iirdesigner;

import org.signalml.math.iirdesigner.math.FunctionOptimizer;
import org.signalml.math.iirdesigner.math.SpecialMath;
import org.apache.commons.math.complex.Complex;
import java.util.ArrayList;
import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * This class represents a designer which is capable of designing an Elliptic filter.
 *
 * @author Piotr Szachewicz
 */
class EllipticIIRDesigner extends AbstractIIRDesigner {

	private static final double EPSILON = 2e-16;

	protected static class VRatio implements UnivariateRealFunction {

		private double ineps;
		private double mp;

		VRatio() {
		}

		VRatio(double ineps, double mp) {

			this.ineps = ineps;
			this.mp = mp;

		}

		@Override
		public double value(double u) {
			double[] jacob = SpecialMath.calculateJacobianEllipticFunctionsValues(u, mp);
			return Math.abs(ineps - jacob[0] / jacob[1]);
		}

	}

	protected static class KRatio implements UnivariateRealFunction {

		private double kRatio;

		KRatio() {
		}

		KRatio(double kRatio) {
			this.kRatio = kRatio;
		}

		@Override
		public double value(double m) {
			double ratio;

			if (m < 0)
				m = 0.0;
			if (m > 1)
				m = 1.0;
			if (Math.abs(m) > EPSILON && (Math.abs(m) + EPSILON) < 1) {
				double k0 = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(m);
				double k1 = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(1 - m);
				ratio = k0 / k1 - kRatio;
			}
			else if (Math.abs(m) > EPSILON)
				ratio = -kRatio;
			else
				ratio = 1e20;

			return Math.abs(ratio);

		}

		public void setKRatio(double kRatio) {
			this.kRatio = kRatio;
		}

	}

	/**
	 * Returns zeros, poles, and gain of an normalized prototype analog
	 * lowpass Elliptic filter which meets the specification.
	 *
	 * @param filterOrder the order of the prototype
	 * @param gpass the maximum loss in the passband [dB]
	 * @return zeros, poles and gain of the filter prototype which meets the given specification
	 */
	@Override
	protected FilterZerosPolesGain calculatePrototype(int filterOrder, double gpass, double stopbandRipple) throws BadFilterParametersException {

		Complex[] zeros;
		Complex[] poles;
		double gain;

		if (filterOrder == 1) {
			poles = new Complex[] {new Complex(-Math.sqrt(1.0 / (Math.pow(10, 0.1 * gpass) - 1.0)), 0.0)};
			gain = -poles[0].getReal();
			zeros = new Complex[0];
			return new FilterZerosPolesGain(zeros, poles, gain);
		}

		double epsilon = Math.sqrt(Math.pow(10, 0.1 * gpass) - 1.0);
		double ck1 = epsilon / Math.sqrt(Math.pow(10, 0.1 * stopbandRipple) - 1.0);
		double ck1p = Math.sqrt(1 - ck1 * ck1);

		if (ck1 == 1)
			throw new BadFilterParametersException("Cannot design a filter with given rp and rs specifications.");

		double[] val = new double[2];
		val[0] = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(ck1 * ck1);
		val[1] = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(ck1p * ck1p);

		double krat;
		if (Math.abs(1 - ck1p * ck1p) < EPSILON)
			krat = 0.0;
		else
			krat = filterOrder * val[0] / val[1];

		KRatio kRatio = new KRatio(krat);
		double m = FunctionOptimizer.minimizeFunctionConstrained(kRatio, 0.0, 1.0, 250);

		double capk = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(m);

		//calculate poles & zeros
		VRatio vRatio = new VRatio(1.0 / epsilon, ck1p * ck1p);
		double startValue = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(m);
		double r = FunctionOptimizer.minimizeFunction(vRatio, startValue, 250);
		double v0 = capk * r / (filterOrder * val[0]);
		double[] ellipv = SpecialMath.calculateJacobianEllipticFunctionsValues(v0, 1 - m);

		ArrayList<Complex> zerosList = new ArrayList<Complex>();
		ArrayList<Complex> polesList = new ArrayList<Complex>();

		double[] ellip;
		for (int i = 1 - filterOrder % 2; i < filterOrder; i+=2) {
			ellip = SpecialMath.calculateJacobianEllipticFunctionsValues(i * capk / filterOrder, m);
			if (Math.abs(ellip[0]) > EPSILON) {
				Complex zero = new Complex(0, 1.0 / (Math.sqrt(m) * ellip[0]));
				zerosList.add(zero);
				zerosList.add(zero.conjugate());
			}

			Complex pole = new Complex(-ellip[1] * ellip[2] * ellipv[0] * ellipv[1], -ellip[0] * ellipv[2]);
			pole = pole.divide(new Complex(1 - Math.pow(ellip[2] * ellipv[0], 2), 0));

			if (SpecialMath.isOdd(filterOrder)) {
				polesList.add(pole);
				if (Math.abs(pole.getImaginary()) > EPSILON * Math.sqrt(pole.multiply(pole.conjugate()).getReal())) {
					polesList.add(pole.conjugate());
				}
			}
			else {
				polesList.add(pole);
				polesList.add(pole.conjugate());
			}

		}

		//convert zeros&poles ArrayLists to arrays
		zeros = new Complex[zerosList.size()];
		for (int i = 0; i < zerosList.size(); i++)
			zeros[i] = zerosList.get(i);

		poles = new Complex[polesList.size()];
		for (int i = 0; i < polesList.size(); i++)
			poles[i] = polesList.get(i);

		//calculating gain
		int i;
		Complex numerator = new Complex(1.0, 0.0);
		for (i = 0; i < poles.length; i++)
			numerator = numerator.multiply(poles[i].multiply(-1.0));
		Complex denominator = new Complex(1.0, 0.0);
		for (i = 0; i < zeros.length; i++)
			denominator = denominator.multiply(zeros[i].multiply(-1.0));

		gain = (numerator.divide(denominator)).getReal();

		if (SpecialMath.isEven(filterOrder))
			gain = gain/Math.sqrt((1 + epsilon * epsilon));

		//return zeros, poles & gain
		return new FilterZerosPolesGain(zeros, poles, gain);

	}

	/**
	 * Calculates the natural (critical) frequencies for a given specification (the 3dB frequency).
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param filterOrder the order of the filter to be designed (can be calculated using {@link AbstractIIRDesigner#calculateFilterOrder(org.signalml.math.iirdesigner.FilterType, double[], double[], double, double, boolean) })
	 * @param wp passband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters]
	 * @param ws stopband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @param analog true to design an analog filter, false to design a digital filter
	 * @return the critical frequencies
	 */
	@Override
	protected double[] calculateNaturalFrequency(FilterType type, int filterOrder, double[] wp, double[] ws, double gpass, double gstop, boolean analog) throws BadFilterParametersException {

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

		double GSTOP = Math.pow(10.0, 0.1 * Math.abs(gstop));
		double GPASS = Math.pow(10.0, 0.1 * Math.abs(gpass));
		double arg1 = Math.sqrt((GPASS - 1.0) / (GSTOP - 1.0));
		double arg0 = 1.0 / frequencyRatio;
		double[] d0 = new double[2];
		double[] d1 = new double[2];
		d0[0] = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(arg0 * arg0);
		d0[1] = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(1 - arg0 * arg0);
		d1[0] = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(arg1 * arg1);
		d1[1] = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(1 - arg1 * arg1);
		int filterOrder = (int)(Math.ceil(d0[0] * d1[1] / (d0[1] * d1[0])));

		if (filterOrder == 0)
			throw new BadFilterParametersException("Filter order is zero - check the input parameters!");

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

		double GSTOP = Math.pow(10, 0.1*Math.abs(gstop));
		double GPASS = Math.pow(10, 0.1*Math.abs(gpass));

		double arg1 = Math.sqrt((GPASS-1.0) / (GSTOP-1.0));
		double arg0 = 1.0 / nat;
		double[] d0 = new double[2];

		d0[0] = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(arg0 * arg0);
		d0[1] = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(1 - arg0 * arg0);

		double[] d1 = new double[2];
		d1[0] = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(arg1 * arg1);
		d1[1] = SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(1 - arg1 * arg1);

		double filterOrder = (d0[0] * d1[1] / (d0[1] * d1[0]));

		return filterOrder;

	}

}