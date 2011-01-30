/* AbstractIIRDesigner.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import flanagan.complex.Complex;
import flanagan.math.MinimisationFunction;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This is an abstract class representing a Designer capable of designing an IIR Filter.
 *
 * @author Piotr Szachewicz
 */
abstract class AbstractIIRDesigner {

	/**
	 * Logger to log history of designing a filter to it.
	 */
	protected static final Logger logger = Logger.getLogger(AbstractIIRDesigner.class);

	/*
	 * contains the value of imaginary unit i (which fulfills: sqrt(i) = -1)
	 */
	protected static final Complex imaginaryUnit = new Complex(0, 1); //imaginary unit

	/**
	 * Maximum filter order which can be designed using this designer.
	 * If a user wants to design a filter with a higher order,
	 * a {@link FilterOrderTooBigException} is thrown.
	 */
	protected static final int maximumFilterOrder = 8;

	/**
	 * Pre-warps the frequencies for digital filter design.
	 *
	 * @param frequencies frequencies to be pre-warped
	 * @return warped frequencies
	 */
	protected double[] prewarpFrequencies(double [] frequencies) {

		double[] warpedFrequencies = new double[frequencies.length];
		for (int i = 0; i < warpedFrequencies.length; i++)
			warpedFrequencies[i] = Math.tan(frequencies[i] * Math.PI / 2.0);

		return warpedFrequencies;

	}

	/**
	 * Undoes the warping that the {@link AbstractIIRDesigner#prewarpFrequencies(double[]) does}.
	 *
	 * @param frequencies frequencies to be unwarped
	 * @return unwarped frequencies
	 */
	protected double[] unwarpFrequencies(double[] frequencies) {

		double[] newFrequencies = new double[frequencies.length];
		for (int i = 0; i < newFrequencies.length; i++)
			newFrequencies[i] = (2.0 / Math.PI) * Math.atan(frequencies[i]);

		return newFrequencies;

	}

	/**
	 * Normalizes the frequencies from 0 to 1 (1 corresponds to PI radians per sample)
	 * for the given sampling frequency.
	 *
	 * @param frequencies frequencies to be normalized
	 * @param samplingFrequency sampling frequency
	 * @return normalized frequencies
	 */
	protected double[] normalizeFrequencies(double[] frequencies, double samplingFrequency) {

		double[] normalizedFrequencies = new double[frequencies.length];

		for (int i = 0; i < normalizedFrequencies.length; i++)
			normalizedFrequencies[i] = frequencies[i] * 2 / samplingFrequency;

		return normalizedFrequencies;

	}

	/**
	 * Designs an analog filter satisfying given specifications.
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param passb passband edge frequencies [rad/sec.]
	 * @param stopb stopband edge frequencies [rad/sec.]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return coefficients of the filter which meets the given specification
	 */
	protected FilterCoefficients designAnalogFilter(FilterType type, double[] passb, double[] stopb, double gpass, double gstop) throws BadFilterParametersException {
		return designFilter(type, passb, stopb, gpass, gstop, true);
	}

	/**
	 * Designs a digital filter satisfying given specifications.
	 *
	 * @param samplingFrequency the sampling frequency of the signal which will
	 * be processed with the filter
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param passb passband edge frequencies [Hz]
	 * @param stopb stopband edge frequencies [Hz]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return coefficients of the filter which meets the given specification
	 */
	protected FilterCoefficients designDigitalFilter(double samplingFrequency, FilterType type, double[] passb, double[] stopb, double gpass, double gstop) throws BadFilterParametersException {

		debug("==================================================");
		debug("Designing digital filter for parameters:");
		debug("sampling frequency: " + samplingFrequency + " Hz");
		debug("filter type: " + type);
		debug("passband frequency 1: " + passb[0] + " Hz");
		debug("stopband frequency 1: " + stopb[0] + " Hz");
		if (type.isBandpass() || type.isBandstop()) {
			debug("passband frequency 2: " + passb[1] + " Hz");
			debug("stopband frequency 2: " + stopb[1] + " Hz");
		}
		debug("passband ripple: " + gpass + " dB");
		debug("stopband attenuation: " + gstop + " dB");
		debug("");

		double[] normalizedPassband = normalizeFrequencies(passb, samplingFrequency);
		double[] normalizedStopband = normalizeFrequencies(stopb, samplingFrequency);

		debug("normalized passband frequency 1: " + normalizedPassband[0]);
		debug("normalized stopband frequency 1: " + normalizedStopband[0]);
		if (type.isBandpass() || type.isBandstop()) {
			debug("normalized passband frequency 2: " + normalizedPassband[1]);
			debug("normalized stopband frequency 2: " + normalizedStopband[1]);
		}

		return designDigitalFilter(type, normalizedPassband, normalizedStopband, gpass, gstop);

	}

	/**
	 * Designs a digital filter satisfying given specifications.
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param passb passband edge frequencies [PI*rad/sample]
	 * @param stopb stopband edge frequencies [PI*rad/sample]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return coefficients of the filter which meets the given specification
	 */
	protected FilterCoefficients designDigitalFilter(FilterType type, double[] passb, double[] stopb, double gpass, double gstop) throws BadFilterParametersException {
		return designFilter(type, passb, stopb, gpass, gstop, false);
	}

	/**
	 * Designs a filter satisfying given specifications.
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param passb passband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters]
	 * @param stopb stopband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @param analog true to design an analog filter, false to design a digital filter
	 * @return coefficients of the filter which meets the specification
	 */
	protected FilterCoefficients designFilter(FilterType type, double[] passb, double[] stopb, double gpass, double gstop, boolean analog) throws BadFilterParametersException {

		int filterOrder = calculateFilterOrder(type, passb, stopb, gpass, gstop, analog);
		debug("filter order: " + filterOrder);
		if (filterOrder > maximumFilterOrder)
			throw new FilterOrderTooBigException("The order of the filter is too big - the parameters are too strict.");

		double[] naturalFrequencies = calculateNaturalFrequency(type, filterOrder, passb, stopb, gpass, gstop, analog);

		debug("natural frequency 1: " + naturalFrequencies[0]);
		if (type.isBandpass() || type.isBandstop())
			debug("natural frequency 2: " + naturalFrequencies[1]);

		return designFilter(type, filterOrder, naturalFrequencies, gpass, gstop, analog);

	}

	/**
	 * Designs a filter satisfying given specifications.
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param filterOrder the order of the filter to be designed
	 * @param naturalFrequencies the critical (natural) frequencies of the filter
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @param analog true to design an analog filter, false to design a digital filter
	 * @return coefficients of the filter which meets the specification
	 */
	protected FilterCoefficients designFilter(FilterType type, int filterOrder, double[] naturalFrequencies, double gpass, double gstop, boolean analog) throws BadFilterParametersException {

		double samplingFrequency = 2.0;
		if (!analog) {
			for (int i = 0; i < naturalFrequencies.length; i++)
				naturalFrequencies[i] = 2 * samplingFrequency * Math.tan(Math.PI * naturalFrequencies[i] / samplingFrequency);
		}

		double bw = 0.0; //filter bandwidth
		double wo; //the center frequency
		if (type.isLowpass() || type.isHighpass())
			wo = naturalFrequencies[0];
		else {
			bw = naturalFrequencies[1] - naturalFrequencies[0];
			wo = Math.sqrt(naturalFrequencies[0] * naturalFrequencies[1]);
		}

		FilterZerosPolesGain zpk = calculatePrototype(filterOrder, gpass, gstop);
		debug("\n### Designing a lowpass prototype: ");
		debug(zpk.toString());

		FilterCoefficients coeffs = zpk.convertToBACoefficients();
		debug("### Transformed zpk prototype to b,a coefficients: ");
		debug(coeffs.toString());

		if (type.isLowpass())
			coeffs.transformLowpassToLowpass(wo);
		else if (type.isHighpass())
			coeffs.transformLowpassToHighpass(wo);
		else if (type.isBandstop())
			coeffs.transformFromLowpassToBandstop(wo, bw);
		else if (type.isBandpass())
			coeffs.transformLowpassToBandpass(wo, bw);

		debug("### Transformed lowpass prototype to " + type + " filter");
		debug(coeffs.toString());

		if (!analog)
			coeffs.bilinearTransform(samplingFrequency);

		debug("### Bilinear transform performed: ");
		debug(coeffs.toString());

		debug("### The filter was successfully designed.");

		return coeffs;

	}

	/**
	 * Returns zeros, poles, and gain representation of a normalized prototype analog
	 * lowpass filter which meets the given specification.
	 *
	 * @param filterOrder the order of the filter prototype
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return zeros, poles and gain of the filter prototype which meets the given specification
	 */
	protected abstract FilterZerosPolesGain calculatePrototype(int filterOrder, double gpass, double gstop) throws BadFilterParametersException;

	/**
	 * Calculates the frequency ratio needed to determine the filter order.
	 * [Thede L., Practical Analog and Digital Filter Design, page 56].
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param passb passband edge frequencies [rad/sec. for analog filters ]
	 * @param stopb stopband edge frequencies [rad/sec. for analog filters ]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return the frequency ratio
	 */
	protected double calculateFrequencyRatio(FilterType type, double[] passb, double[] stopb, double gpass, double gstop) {

		double frequencyRatio = 0;

		if (type.isLowpass())
			frequencyRatio = stopb[0]/passb[0];
		else if (type.isHighpass())
			frequencyRatio = passb[0]/stopb[0];
		else if (type.isBandstop()) {

			passb = optimizeBandstopFilterPassbandFrequencies(passb, stopb, gpass, gstop);

			double[] freqRatios = new double[2];
			freqRatios[0] = (stopb[0] * (passb[0] - passb[1])) / (Math.pow(stopb[0], 2) - passb[0] * passb[1]);
			freqRatios[1] = (stopb[1] * (passb[0] - passb[1])) / (Math.pow(stopb[1], 2) - passb[0] * passb[1]);
			frequencyRatio = Math.min(Math.abs(freqRatios[0]), Math.abs(freqRatios[1]));

		}
		else if (type.isBandpass()) {

			double[] possibilities = new double[2];
			possibilities[0] = (stopb[0] * stopb[0] - passb[0] * passb[1])/(stopb[0] * (passb[0] - passb[1]));
			possibilities[1] = (stopb[1] * stopb[1] - passb[0] * passb[1])/(stopb[1] * (passb[0] - passb[1]));
			frequencyRatio = Math.min(Math.abs(possibilities[0]), Math.abs(possibilities[1]));

		}

		return frequencyRatio;

	}

	/**
	 * Calculates the natural (critical) frequencies for a given specification (the 3dB frequency).
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param filterOrder the order of the filter to be designed
	 * @param passb passband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param stopb stopband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @param analog true to design an analog filter, false to design a digital filter
	 * @return an array of critical frequencies
	 */
	protected abstract double[] calculateNaturalFrequency(FilterType type, int filterOrder, double[] passb, double[] stopb, double gpass, double gstop, boolean analog) throws BadFilterParametersException;

	/**
	 * Calculates the natural (critical) frequency for a given specification (the 3dB frequency).
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param filterOrder the order of the filter to be designed
	 * @param passb passband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param stopb stopband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @param analog true to design an analog filter, false to design a digital filter
	 * @return the critical frequency
	 */
	protected double calculateNaturalFrequency(FilterType type, int filterOrder, double passb, double stopb, double gpass, double gstop, boolean analog) throws BadFilterParametersException {
		return calculateNaturalFrequency(type, filterOrder, new double[] {passb}, new double[] {stopb}, gpass, gstop, analog)[0];
	}

	/**
	 * Calculates the minimum filter order of the filter which would meets the given specifications.
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param passb passband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param stopb stopband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @param analog true to design an analog filter, false to design a digital filter
	 * @return the order of filter
	 */
	protected abstract int calculateFilterOrder(FilterType type, double[] passb, double[] stopb, double gpass, double gstop, boolean analog) throws BadFilterParametersException;

	/**
	 * Calculates the minimum filter order of the filter which would meets the given specifications.
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param passb passband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param stopb stopband edge frequencies [PI*rad/sample for digital filters or rad/sec. for analog filters ]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @param analog true to design an analog filter, false to design a digital filter
	 * @return the order of filter
	 */
	protected int calculateFilterOrder(FilterType type, double passb, double stopb, double gpass, double gstop, boolean analog) throws BadFilterParametersException {
		return calculateFilterOrder(type, new double[] {passb}, new double[] {stopb}, gpass, gstop, analog);
	}

	/**
	 * Finds optimal values for passband edge frequencies for an analog bandstop filter
	 * (the filter with optimal values has lower order).
	 *
	 * @param passb passband edge frequencies [rad/sec.]
	 * @param stopb stopband edge frequencies [rad/sec.]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return the optimized values of the passband edge frequencies
	 */
	protected double[] optimizeBandstopFilterPassbandFrequencies(double[] passb, double[] stopb, double gpass, double gstop) {

		double[] passbCopy = passb.clone();

		BandstopObjectiveFunction objectiveFunction = new BandstopObjectiveFunction(0, passbCopy, stopb, gpass, gstop);
		double passb0 = SpecialMath.minimizeFunctionConstrained(objectiveFunction, passbCopy[0], stopb[0]-1e-12);
		passbCopy[0] = passb0;

		objectiveFunction = new BandstopObjectiveFunction(1, passbCopy, stopb, gpass, gstop);
		double passb1 = SpecialMath.minimizeFunctionConstrained(objectiveFunction, stopb[1]+1e-12, passbCopy[1]);
		passbCopy[1] = passb1;

		return new double[] {passbCopy[0], passbCopy[1]};

	}

	/**
	 * Evaluates the bandstop objective function which is used to find
	 * optimal values for passband edge frequencies (if the filter is an analog
	 * bandstop filter) to minimize the order of the filter.
	 *
	 * @param passbandEdge the new passband edge frequency [rad/sec.]
	 * @param variablePassbandEdgeIndex specifies which passband edge to vary (0 or 1)
	 * @param passb passband edge frequencies [rad/sec.]
	 * @param stopb stopband edge frequencies [rad/sec.]
	 * @param gpass the maximum loss in the passband [dB]
	 * @param gstop the minimum attenuation in the stopband [dB]
	 * @return the filter order (possibly non-integer)
	 */
	protected abstract double calculateBandstopObjectiveFunctionValue(double passbandEdge, int variablePassbandEdgeIndex, double[] passb, double[] stopb, double gpass, double gstop);

	/**
	 * Calculates the bandstop objective function value which is used to find
	 * optimal values for passband edge frequencies (if the filter is an analog bandstop filter)
	 * to minimize the filter order. Uses the values that are stored
	 * in the given BandstopObjectiveFunction.
	 *
	 * @param passbandEdge the new passband edge frequency [rad/sec.]
	 * @param objectiveFunction the objective function which stores the values used for computation
	 * @return the filter order (possibly non-integer)
	 */
	protected double calculateBandstopObjectiveFunctionValue(double passbandEdge, BandstopObjectiveFunction objectiveFunction) {

		return calculateBandstopObjectiveFunctionValue(passbandEdge, objectiveFunction.getVariablePassbandEdgeIndex(), objectiveFunction.getPassb(), objectiveFunction.getStopb(), objectiveFunction.getPassbandRipple(), objectiveFunction.getStopbandAttenuation());

	}

	/**
	 * This class is used for bandstop filter order minimization.
	 */
	protected class BandstopObjectiveFunction implements MinimisationFunction {

		/**
		 * specyfies which passband edge to vary
		 */
		int variablePassbandEdgeIndex;

		/**
		 * the passband edge frequencies
		 */
		double[] passb = new double[2];

		/**
		 * the stopband edge frequencies
		 */
		double[] stopb = new double[2];

		/**
		 * the minimum stopband attenuation [dB]
		 */
		double gstop;

		/**
		 * the maximum ripple in the passband [dB]
		 */
		double gpass;

		/**
		 * Constructor.
		 *
		 * @param variablePassbandEdgeIndex specifies which passband edge to vary (0 or 1)
		 * @param passb passband edge frequencies [rad/sec.]
		 * @param stopb stopband edge frequencies [rad/sec.]
		 * @param gpass the maximum loss in the passband [dB]
		 * @param gstop the minimum attenuation in the stopband [dB]
		 */
		public BandstopObjectiveFunction(int variablePassbandEdgeIndex, double[] passb, double[] stopb, double gpass, double gstop) {

			this.variablePassbandEdgeIndex = variablePassbandEdgeIndex;
			this.passb = passb;
			this.stopb = stopb;
			this.gpass = gpass;
			this.gstop = gstop;

		}

		/**
		 * Calculates the value of the objective function for a given new passband edge frequency
		 * and values stored in this object. The value of this function should be
		 * minimized to get the lowest filter order.
		 *
		 * @param param param[0] - the new passband edge frequency [rad/sec.]
		 * @return the filter order (possibly non-integer)
		 */
		@Override
		public double function(double[] param) {
			double argument = param[0];
			return calculateBandstopObjectiveFunctionValue(argument, this);
		}

		/**
		 * Returs which passband edge this bandstop objective function will vary (0 or 1)
		 *
		 * @return the index of the passband edge to be varied
		 */
		public int getVariablePassbandEdgeIndex() {
			return variablePassbandEdgeIndex;
		}

		/**
		 * Returns the passband edge frequencies used to calculate the value of
		 * this objective function
		 *
		 * @return the passband edge frequencies
		 */
		public double[] getPassb() {
			return passb;
		}

		/**
		 * Returns the stopband edge frequencies used to calculate the value of
		 * this objective function
		 *
		 * @return the stopband edge frequencies
		 */
		public double[] getStopb() {
			return stopb;
		}

		/**
		 * Returns the maximum ripple in the passband  used to calculate the value of
		 * this objective function
		 *
		 * @return maximum ripple in the passband [dB]
		 */
		public double getPassbandRipple() {
			return gpass;
		}

		/**
		 * Returns the value of the minimum attenuation in the stopband used to calculate the value of
		 * this objective function
		 *
		 * @return minimum attenuation in the stopband [dB]
		 */
		public double getStopbandAttenuation() {
			return gstop;
		}

	}

	/**
	 * Writes the debug information contained in the given string using
	 * the program
	 * @param s a string to be logged.
	 */
	protected void debug(String s) {
		String split[];

		split = s.split("\\n");
		for(String str: split)
			logger.debug(str);
	}

	/**
	 * Enables or disables the logger that prints debug information
	 * about the process of designing a filter.
	 * @param enable true if the logger should be enabled, false otherwise.
	 */
	protected void enableDebugger(boolean enable) {
		if (enable != true)
			logger.setLevel(Level.OFF);
		else
			logger.setLevel(Level.ALL);
	}

}