/* TimeDomainSampleFilter.java created 2010-09-29
 *
 */

package org.signalml.domain.montage.filter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.text.DecimalFormat;
import java.util.Arrays;
import org.signalml.app.config.preset.Preset;
import org.signalml.math.iirdesigner.ApproximationFunctionType;
import org.signalml.math.iirdesigner.FilterType;

/**
 * This class holds a time domain representation of a
 * {@link SampleFilterDefinition sample filter}.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("timeDomainSampleFilter")
public class TimeDomainSampleFilter extends SampleFilterDefinition implements Preset {

	private static final long serialVersionUID = 2L;

	/**
	 * the name of the filter for the use of the {@link Preset} interface
	 */
	private String name;

	/**
	 * the type of the filter (LOWPASS/HIGHPASS/BANDPASS/BANDSTOP/NOTCH/PEAK)
	 */
	private FilterType filterType;

	/**
	 * the type of approximation function which will be used to fulfill
	 * the requirements specified by the edge freuencies, passband ripple,
	 * stopband attenuation etc.
	 * This parameter is ignored for "notch" and "peak" filters.
	 */
	private ApproximationFunctionType approximationFunctionType;

	/**
	 * The edge frequencies at which the passband begins and/or ends.
	 * If the filter is a low-pass or a high-pass filter, then only one
	 * passband edge frequency is needed. In that case, the value of the
	 * passbandEdgeFrequencies[1] is ignored.
	 */
	private double[] passbandEdgeFrequencies = new double[2];

	/**
	 * The edge frequencies at which the stopband begins and/or ends.
	 * If the filter is a low-pass or a high-pass filter, then only one
	 * stopband edge frequency is needed. In that case, the value of the
	 * stopbandEdgeFrequencies[1] is ignored.
	 */
	private double[] stopbandEdgeFrequencies = new double[2];

	/**
	 * The maximum value (in decibels) of variations in the frequency
	 * magnitude response within the passband of a filter.
	 * This parameter is ignored for "notch" and "peak" filters.
	 */
	private double passbandRipple;

	/**
	 * The minimum value (in decibels) of attenuation for the stopband
	 * within the filter's frequency response.
	 * This parameter is ignored for "notch" and "peak" filters.
	 */
	private double stopbandAttenuation;

	/**
	 * Quality factor. Dimensionless parameter that characterizes notch
	 * filter -3 dB bandwidth relative to its center frequency, Q = w0/bw.
	 * This parameter is needed for "notch" and "peak" filters only.
	 */
	private double quality;

	/**
	 * The sampling frequency for which the filter's coefficients
	 * will be calculated.
	 */
	private transient double samplingFrequency;

	/**
	 * Constructor. Creates an empty instance of a TimeDomainSampleFilter.
	 * For internal use only.
	 */
	protected TimeDomainSampleFilter() {
	}

	/**
	 * Creates a new filter which is a copy of the filter given as an
	 * argument.
	 * @param filter filter to be copied
	 */
	public TimeDomainSampleFilter(TimeDomainSampleFilter filter) {
		this.copyFrom(filter);
	}

	/**
	 * Creates a new filter characterized by the given parameters.
	 * @param filterType the type of the filter (low-pass/high-pass etc.)
	 * @param approximationFunctionType the type of approximation function
	 * which will be used to calculate the filter coefficients (Butterworth,
	 * ChebyshevI, etc.)
	 * @param passbandEdgeFrequencies an array containing two (or one - if
	 * the filter is low-pass or high-pass) edge frequencies at which the
	 * passband should begin and/or end. If the filter is a low-pass or a
	 * high-pass filter, then only one passbandband edge frequency
	 * is needed. In that case, the value of the passbandEdgeFrequencies[1]
	 * is never read.
	 * @param stopbandEdgeFrequencies an array containing two (or one - if
	 * the filter is low-pass or high-pass) edge frequencies at which the
	 * stopband should begin and/or end. If the filter is a low-pass or a
	 * high-pass filter, then only one stopbandband edge frequency
	 * is needed. In that case, the value of the stopbandEdgeFrequencies[1]
	 * is never read.
	 * @param passbandRipple the maximum value (in decibels) of variations
	 * in the frequency magnitude response within the passband of a filter.
	 * @param stopbandAttenuation the minimum value (in decibels) of
	 * attenuation for the stopband within the filter's frequency response.
	 * @param quality quality factor, for notch and peak filters only
	 */
	public TimeDomainSampleFilter(FilterType filterType, ApproximationFunctionType approximationFunctionType, double[] passbandEdgeFrequencies, double[] stopbandEdgeFrequencies, double passbandRipple, double stopbandAttenuation, double quality) {
		this.filterType = filterType;
		this.approximationFunctionType = approximationFunctionType;
		this.passbandEdgeFrequencies = passbandEdgeFrequencies.clone();
		this.stopbandEdgeFrequencies = stopbandEdgeFrequencies.clone();
		this.passbandRipple = passbandRipple;
		this.stopbandAttenuation = stopbandAttenuation;
		this.quality = quality;
	}

	/**
	 * Creates a new filter characterized by the given parameters.
	 * @param filterType the type of the filter (low-pass/high-pass etc.)
	 * @param approximationFunctionType the type of approximation function
	 * which will be used to calculate the filter coefficients (Butterworth,
	 * ChebyshevI, etc.)
	 * @param passbandEdgeFrequencies an array containing two (or one - if
	 * the filter is low-pass or high-pass) edge frequencies at which the
	 * passband should begin and/or end. If the filter is a low-pass or a
	 * high-pass filter, then only one passbandband edge frequency
	 * is needed. In that case, the value of the passbandEdgeFrequencies[1]
	 * is never read.
	 * @param stopbandEdgeFrequencies an array containing two (or one - if
	 * the filter is low-pass or high-pass) edge frequencies at which the
	 * stopband should begin and/or end. If the filter is a low-pass or a
	 * high-pass filter, then only one stopbandband edge frequency
	 * is needed. In that case, the value of the stopbandEdgeFrequencies[1]
	 * is never read.
	 * @param passbandRipple the maximum value (in decibels) of variations
	 * in the frequency magnitude response within the passband of a filter.
	 * @param stopbandAttenuation the minimum value (in decibels) of
	 * attenuation for the stopband within the filter's frequency response.
	 */
	public TimeDomainSampleFilter(FilterType filterType, ApproximationFunctionType approximationFunctionType, double[] passbandEdgeFrequencies, double[] stopbandEdgeFrequencies, double passbandRipple, double stopbandAttenuation) {
		this(filterType, approximationFunctionType,
			passbandEdgeFrequencies, stopbandEdgeFrequencies,
			passbandRipple, stopbandAttenuation,
			Double.NaN); // quality is meaningless for most filters
	}

	/**
	 * Returns the type of the filter (low-pass/high-pass/band-pass/band-stop).
	 * @return the type of the filter
	 */
	public FilterType getFilterType() {
		return filterType;
	}

	/**
	 * Sets type of the filter (low-pass/high-pass/band-pass/band-stop).
	 * @param filterType the new type of the filter.
	 */
	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	/**
	 * Returns the type of approximation function which will be used to
	 * calculate filter coeffients which should fulfill the requirements
	 * specified by this filter's parameters (passband/stopband edge frequencies,
	 * passband ripple etc.).
	 * @return the type of approximation function which will be used
	 * (Butterworth, Chebyshev, Elliptic)
	 */
	public ApproximationFunctionType getApproximationFunctionType() {
		return approximationFunctionType;
	}

	/**
	 * Sets the type of approximation function which will be used to
	 * calculate filter coeffients which should fulfill the requirements
	 * specified by this filter's parameters (passband/stopband edge frequencies,
	 * passband ripple etc.).
	 * @param approximationFunctionType the type of approximation function
	 * which will be used (Butterworth, Chebyshev, Elliptic)
	 */
	public void setApproximationFunctionType(ApproximationFunctionType approximationFunctionType) {
		this.approximationFunctionType = approximationFunctionType;
	}

	/**
	 * Returns an array containing two edge frequencies at which the
	 * passband should begin and/or end. If the filter is a low-pass or a
	 * high-pass filter, then only the first passbandband edge frequency
	 * is valid.
	 * @return an array containing passband edge frequencies (in Hz)
	 */
	public double[] getPassbandEdgeFrequencies() {
		return passbandEdgeFrequencies;
	}

	/**
	 * Sets the values of edge frequencies at which the
	 * passband should begin and/or end. If the filter is a low-pass or a
	 * high-pass filter, then only the first passbandband edge frequency
	 * is valid.
	 * @param passbandEdgeFrequencies an array containing passband edge
	 * frequencies (in Hz)
	 */
	public void setPassbandEdgeFrequencies(double[] passbandEdgeFrequencies) {
		this.passbandEdgeFrequencies = passbandEdgeFrequencies.clone();
	}

	/**
	 * Returns an array containing two edge frequencies at which the
	 * stopband should begin and/or end. If the filter is a low-pass or a
	 * high-pass filter, then only one stopbandband edge frequency
	 * is valid.
	 * @return an array containing stopband edge frequencies
	 */
	public double[] getStopbandEdgeFrequencies() {
		return stopbandEdgeFrequencies;
	}

	/**
	 * Sets the values of edge frequencies at which the
	 * stopband should begin and/or end. If the filter is a low-pass or a
	 * high-pass filter, then only one stopbandband edge frequency
	 * is valid.
	 * @param stopbandEdgeFrequencies an array containing new values
	 * of stopband edge frequencies
	 */
	public void setStopbandEdgeFrequencies(double[] stopbandEdgeFrequencies) {
		this.stopbandEdgeFrequencies = stopbandEdgeFrequencies.clone();
	}

	/**
	 * Returns the maximum value (in decibels) of variations
	 * in the frequency magnitude response within the passband of a filter.
	 * @return the maximum ripple in the passband for this filter
	 */
	public double getPassbandRipple() {
		return passbandRipple;
	}

	/**
	 * Sets the maximum value (in decibels) of variations which can occur
	 * in the frequency magnitude response within the passband of a filter.
	 * @param passbandRipple the maximum ripple which can occur in the
	 * passband for this filter
	 */
	public void setPassbandRipple(double passbandRipple) {
		this.passbandRipple = passbandRipple;
	}

	/**
	 * Returns the minimum value (in decibels) of attenuation
	 * for the stopband within the filter's frequency response
	 * @return the minimum stopband attenuation for this filter
	 */
	public double getStopbandAttenuation() {
		return stopbandAttenuation;
	}

	/**
	 * Sets the minimum value (in decibels) of attenuation
	 * for the stopband within the filter's frequency response
	 * @param stopbandAttenuation the minimum stopband attenuation
	 * this filter must ensure.
	 */
	public void setStopbandAttenuation(double stopbandAttenuation) {
		this.stopbandAttenuation = stopbandAttenuation;
	}

	/**
	 * Reeturn required quality of the filter, e.g -3 dB bandwidth
	 * relative to its center frequency, Q = w0/bw.
	 * This parameter is meaningful for "notch" and "peak" filters only.
	 * @return quality parameter
	 */
	public double getQualityParameter() {
		return quality;
	}

	/**
	 * Set required quality of the filter, e.g -3 dB bandwidth
	 * relative to its center frequency, Q = w0/bw.
	 * This parameter is meaningful for "notch" and "peak" filters only.
	 * @param quality quality parameter
	 */
	public void setQualityParameter(double quality) {
		this.quality = quality;
	}

	/**
	 * Returns the sampling frequency for which the coefficients of this filter
	 * will be calculated.
	 * @return the sampling frequency for which this filter will operate
	 */
	public double getSamplingFrequency() {
		return samplingFrequency;
	}

	/**
	 * Sets the sampling frequency for which the coefficients of this filter
	 * will be calculated.
	 * @param samplingFrequency the sampling frequency for which this filter
	 * will operate
	 */
	public void setSamplingFrequency(double samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	/**
	 * Returns a string specifying the filter's passband frequencies.
	 * @return a string describing the filter's passband frequencies
	 */
	public String getEffect() {

		String passbandEdgeFrequency0 = convertDoubleToString(passbandEdgeFrequencies[0]);
		String passbandEdgeFrequency1 = convertDoubleToString(passbandEdgeFrequencies[1]);
		String stopbandEdgeFrequency0 = convertDoubleToString(stopbandEdgeFrequencies[0]);
		String stopbandEdgeFrequency1 = convertDoubleToString(stopbandEdgeFrequencies[1]);

		String effectString = "";

		effectString += filterType + " (";

		if (filterType.isLowpass())
			effectString += "0 - " + passbandEdgeFrequency0;
		else if (filterType.isHighpass())
			effectString += passbandEdgeFrequency0 + " - inf";
		else if (filterType.isBandpass())
			effectString += passbandEdgeFrequency0 + " - " + passbandEdgeFrequency1;
		else if (filterType.isBandstop())
			effectString += stopbandEdgeFrequency0 + " - " + stopbandEdgeFrequency1;
		else if (filterType.isNotch())
			effectString += stopbandEdgeFrequency0;
		else if (filterType.isPeak())
			effectString += passbandEdgeFrequency0;
		effectString += " Hz";

		effectString += ")";

		return effectString;

	}

	/**
	 * Converts a given double value to a string.
	 * @param value a double value to be converted
	 * @return the result of the conversion
	 */
	protected String convertDoubleToString(double value) {
		DecimalFormat decimalFormat = new DecimalFormat("######.##");
		return decimalFormat.format(value);
	}

	@Override
	public SampleFilterType getType() {
		return SampleFilterType.TIME_DOMAIN;
	}

	/**
	 * Duplicates (@link TimeDomainSampleFilter the definition of the filter).
	 * @return the copy of the filter
	 */
	@Override
	public TimeDomainSampleFilter duplicate() {

		TimeDomainSampleFilter duplicate = new TimeDomainSampleFilter();
		duplicate.copyFrom(this);

		return duplicate;

	}

	/**
	     * Sets all parameters of this filter to mathch the values of the
	     * parameters of the given filter.
	     * @param filter a filter which parameters are to be copied
	     * to this filter
	     */
	public final void copyFrom(TimeDomainSampleFilter filter) {

		filterType = filter.filterType;
		approximationFunctionType = filter.approximationFunctionType;
		passbandEdgeFrequencies = filter.passbandEdgeFrequencies.clone();
		stopbandEdgeFrequencies = filter.stopbandEdgeFrequencies.clone();
		passbandRipple = filter.passbandRipple;
		stopbandAttenuation = filter.stopbandAttenuation;
		quality = filter.quality;
		samplingFrequency = filter.samplingFrequency;

		description = filter.description;

	}

	/**
	 * Checks if the filter is equal to another filter o.
	 * (to be equal o must be an instance of (@link TimeDomainSampleFilter TimeDomainSampleFilter)
	 * and feedback and feedforward coefficients of each filters must be equal.
	 * (Strings describing the filters are not taken into account while comparing).
	 * @param o an Object to be compared with the filter
	 * @return true if the the filter is equal to the Object o, otherwise - false
	 */
	@Override
	public boolean equals(Object o) {

		if (!(o instanceof TimeDomainSampleFilter))
			return false;

		TimeDomainSampleFilter tdf = (TimeDomainSampleFilter)o;
		if (tdf.filterType.equals(filterType) && tdf.approximationFunctionType.equals(approximationFunctionType) &&
				Arrays.equals(passbandEdgeFrequencies, tdf.passbandEdgeFrequencies) &&
				Arrays.equals(stopbandEdgeFrequencies, tdf.stopbandEdgeFrequencies) &&
				name == tdf.name &&
				passbandRipple == tdf.passbandRipple && stopbandAttenuation == tdf.stopbandAttenuation)
			return true;
		return false;

	}

	@Override
	public String toString() {
		return name != null ? name : getEffect();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
