/* TimeDomainSampleFilter.java created 2010-09-29
 *
 */

package org.signalml.domain.montage.filter;

import java.util.Arrays;
import org.signalml.util.ResolvableString;
import org.springframework.context.MessageSourceResolvable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.config.preset.Preset;
import org.signalml.domain.montage.filter.iirdesigner.ApproximationFunctionType;
import org.signalml.domain.montage.filter.iirdesigner.FilterType;

/**
 * This class holds a time domain representation of a
 * {@link SampleFilterDefinition sample filter}.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("timeDomainSampleFilter")
public class TimeDomainSampleFilter extends SampleFilterDefinition implements Preset {

	private static final long serialVersionUID = 1L;

	protected Object[] arguments;
	private static final String[] EFFECT_CODES = new String[] {"timeDomainFilter.effect"};

	private String name;

	private FilterType filterType;
	private ApproximationFunctionType approximationFunctionType;
	private double[] passbandEdgeFrequencies = new double[2];
	private double[] stopbandEdgeFrequencies = new double[2];
	private double passbandRipple;
	private double stopbandAttenuation;
	private transient double samplingFrequency;

	private transient String effectString;


	/**
	 * Constructor. Creates an empty instance of a TimeDomainSampleFilter.
	 * For internal use only.
	 */
	protected TimeDomainSampleFilter() {
	}

	public TimeDomainSampleFilter(TimeDomainSampleFilter filter) {
		this.copyFrom(filter);
	}

	public TimeDomainSampleFilter(FilterType filterType, ApproximationFunctionType approximationFunctionType, double[] passbandEdgeFrequencies, double[] stopbandEdgeFrequencies, double passbandRipple, double stopbandAttenuation) {

		this.filterType = filterType;
		this.approximationFunctionType = approximationFunctionType;
		this.passbandEdgeFrequencies = passbandEdgeFrequencies.clone();
		this.stopbandEdgeFrequencies = stopbandEdgeFrequencies.clone();
		this.passbandRipple = passbandRipple;
		this.stopbandAttenuation = stopbandAttenuation;

	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	public ApproximationFunctionType getApproximationFunctionType() {
		return approximationFunctionType;
	}

	public void setApproximationFunctionType(ApproximationFunctionType approximationFunctionType) {
		this.approximationFunctionType = approximationFunctionType;
	}

	public double[] getPassbandEdgeFrequencies() {
		return passbandEdgeFrequencies;
	}

	public void setPassbandEdgeFrequencies(double[] passbandEdgeFrequencies) {
		this.passbandEdgeFrequencies = passbandEdgeFrequencies.clone();
	}

	public double[] getStopbandEdgeFrequencies() {
		return stopbandEdgeFrequencies;
	}

	public void setStopbandEdgeFrequencies(double[] stopbandEdgeFrequencies) {
		this.stopbandEdgeFrequencies = stopbandEdgeFrequencies.clone();
	}

	public double getPassbandRipple() {
		return passbandRipple;
	}

	public void setPassbandRipple(double passbandRipple) {
		this.passbandRipple = passbandRipple;
	}

	public double getStopbandAttenuation() {
		return stopbandAttenuation;
	}

	public void setStopbandAttenuation(double stopbandAttenuation) {
		this.stopbandAttenuation = stopbandAttenuation;
	}

	public double getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(double samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public String getEffectString() {

		effectString = "(";
		if (filterType.isLowpass())
			effectString += "0 - " + passbandEdgeFrequencies[0];
		else if (filterType.isHighpass())
			effectString += passbandEdgeFrequencies[0] + " - Fn";
		else if (filterType.isBandpass())
			effectString += passbandEdgeFrequencies[0] + " - " + passbandEdgeFrequencies[1];
		else if (filterType.isBandstop())
			effectString += stopbandEdgeFrequencies[0] + " - " + stopbandEdgeFrequencies[1];
		effectString += ")";

		return effectString;

	}

	@Override
	public MessageSourceResolvable getEffectDescription() {
		return new ResolvableString(EFFECT_CODES, getArguments(), getDefaultEffectDescription());
	}

	@Override
	public String getDefaultEffectDescription() {
		return new String("Time Domain Filter");
	}

	@Override
	public SampleFilterType getType() {
		return SampleFilterType.TIME_DOMAIN;
	}

	@Override
	public Object[] getArguments() {
		if (arguments == null)
			arguments = new Object[] {filterType, getEffectString()};
		return arguments;
	}

	@Override
	public String[] getCodes() {
		return EFFECT_CODES;
	}

	@Override
	public String getDefaultMessage() {
		return "Time domain filter " + getClass().getSimpleName();
	}

	/**
	 * Duplicates (@link TimeDomainSampleFilter the definition of the filter).
	 * @return the copy of the filter
	 */
	@Override
	public TimeDomainSampleFilter duplicate() {

		TimeDomainSampleFilter duplicate = new TimeDomainSampleFilter();

		duplicate.copyFrom(this);
		/*duplicate.aCoefficients = aCoefficients.clone();
		duplicate.bCoefficients = bCoefficients.clone();
		duplicate.messageCodes = messageCodes.clone();
		duplicate.arguments = arguments.clone();
		duplicate.setDescription(this.description);*/

		return duplicate;

	}

	public void copyFrom(TimeDomainSampleFilter filter) {

		filterType = filter.filterType;
		approximationFunctionType = filter.approximationFunctionType;
		passbandEdgeFrequencies = filter.passbandEdgeFrequencies.clone();
		stopbandEdgeFrequencies = filter.stopbandEdgeFrequencies.clone();
		passbandRipple = filter.passbandRipple;
		stopbandAttenuation = filter.stopbandAttenuation;
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
		                passbandRipple == tdf.passbandRipple && stopbandAttenuation == tdf.stopbandAttenuation)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return name;
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
