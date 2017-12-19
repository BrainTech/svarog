package org.signalml.math.iirdesigner;

/**
 * This class represents a designer which is capable of designing
 * "notch" and "peak" filters.
 *
 * @author piotr.rozanski@braintech.pl
 */
class NotchIIRDesigner {

	/**
	 * Designs a filter satisfying given specifications.
	 *
	 * @param type type of the filter (lowpass/highpass/bandpass/bandstop)
	 * @param samplingFrequency sampling frequency [Hz]
	 * @param passb passband edge frequencies [Hz]
	 * @param stopb stopband edge frequencies [Hz]
	 * @param quality relative bandwidth (df/f0) for -3 dB attenuation
	 * @return coefficients of the filter which meets the specification
	 */
	public FilterCoefficients designDigitalFilter(double samplingFrequency, FilterType type, double[] passb, double[] stopb, double quality) {
		switch (type) {
			case NOTCH:
				return designNotchFilter(samplingFrequency, stopb[0], quality);
			case PEAK:
				return designPeakFilter(samplingFrequency, passb[0], quality);
			default:
				throw new IllegalArgumentException("only notch and peak filters are supported");
		}
	}

	/**
	 * Design a digital notch filter. Filter will be designed according to
	 * formulae from Sophocles J. Orfanidis "Introduction to signal processing"
	 * (ISBN 0-13-209172-0).
	 *
	 * @param samplingFrequency  sampling frequency [Hz]
	 * @param centerFrequency  center frequency [Hz]
	 * @param quality  relative bandwidth (df/f0) for -3 dB attenuation
	 * @return filter coefficients object
	 */
	protected FilterCoefficients designNotchFilter(double samplingFrequency, double centerFrequency, double quality) {
		double omega = 2.0 * Math.PI * centerFrequency / samplingFrequency;
		double delta_omega = omega / quality;
		double b = 1.0 / (1.0 + Math.tan(delta_omega / 2));
		double cos_omega = Math.cos(omega);

		double[] coeffs_b = { b, -2*b*cos_omega, b };
		double[] coeffs_a = { 1, -2*b*cos_omega, 2*b-1 };
		return new FilterCoefficients(coeffs_b, coeffs_a);
	}

	protected FilterCoefficients designPeakFilter(double samplingFrequency, double centerFrequency, double quality) {
		FilterCoefficients notch = designNotchFilter(samplingFrequency, centerFrequency, quality);
		double[] a = notch.getACoefficients();
		double[] b = notch.getBCoefficients();
		// we will modify the filter in-place
		for (int i=0; i<b.length; ++i) {
			b[i] = a[i] - b[i];
		}
		return notch;
	}

}
