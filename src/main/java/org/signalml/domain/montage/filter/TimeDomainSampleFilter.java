/* TimeDomainSampleFilter.java created 2008-02-01
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
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("timeDomainSampleFilter")
public class TimeDomainSampleFilter extends SampleFilterDefinition implements Preset {

	private static final long serialVersionUID = 1L;

	protected Object[] arguments;
	protected String[] messageCodes;
	private static final String[] CODES = new String[] { "timeDomainFilter" };
	private static final String[] EFFECT_CODES = new String[] { "timeDomainFilter.effect" };

	private String name;

	private FilterType filterType;
	private ApproximationFunctionType approximationFunctionType;
	private double[] passbandEdgeFrequencies = new double[2];
	private double[] stopbandEdgeFrequencies = new double[2];
	private double passbandRipple;
	private double stopbandAttenuation;
	private transient double samplingFrequency;

	private transient String effectString;

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

	/**
	 * an array of filter feedback coefficients
	 */
	protected double aCoefficients[];

	/**
	 * an array of filter feedforward coefficients
	 */
	protected double bCoefficients[];

	/**
	 * Constructor. Creates an empty instance of a TimeDomainSampleFilter.
	 * For internal use only.
	 */
	protected TimeDomainSampleFilter() {
	}

	public TimeDomainSampleFilter(TimeDomainSampleFilter filter) {
		this.copyFrom(filter);
	}

	/**
	* Constructor.
	* @param messsageCode the code for (@link MessageSourceResolvable MessageSourceResolvable)
	 * specyfying the type of the filter (e.g. "sampleFilter.td.lowPass")
	* @param passBand a String describing the passband of the filter (e.g. "0-20 Hz")
	* @param aCoefs array of A Coefficients (feedback filter coefficients)
	* @param bCoefs array of B Coefficients (feedforward filter coefficients)
	*/
	public TimeDomainSampleFilter(String messageCode, String passBand, double[] aCoefs, double[] bCoefs) {

		this.messageCodes = new String[] {messageCode};
		this.arguments = new Object[] {new String(passBand)};

		this.aCoefficients = aCoefs.clone();
		this.bCoefficients = bCoefs.clone();

		//this.setDescription("Time Domain Filter");

	}

	public TimeDomainSampleFilter(String messageCode, String passBand, double[] aCoefs, double[] bCoefs, FilterType filterType, ApproximationFunctionType approximationFunctionType, double[] passbandEdgeFrequencies, double[] stopbandEdgeFrequencies, double passbandRipple, double stopbandAttenuation) {

		this(messageCode, passBand, aCoefs, bCoefs);

		this.filterType = filterType;
		this.approximationFunctionType = approximationFunctionType;
		this.passbandEdgeFrequencies = passbandEdgeFrequencies.clone();
		this.stopbandEdgeFrequencies = stopbandEdgeFrequencies.clone();
		this.passbandRipple = passbandRipple;
		this.stopbandAttenuation = stopbandAttenuation;

	}

	public TimeDomainSampleFilter(FilterType filterType, ApproximationFunctionType approximationFunctionType, double[] passbandEdgeFrequencies, double[] stopbandEdgeFrequencies, double passbandRipple, double stopbandAttenuation) {

		this.filterType = filterType;
		this.approximationFunctionType = approximationFunctionType;
		this.passbandEdgeFrequencies = passbandEdgeFrequencies.clone();
		this.stopbandEdgeFrequencies = stopbandEdgeFrequencies.clone();
		this.passbandRipple = passbandRipple;
		this.stopbandAttenuation = stopbandAttenuation;

	}

	/*public double[] getACoefficients() {
		return aCoefficients;
	}

	public double[] getBCoefficients() {
		return bCoefficients;
	}

	*
	 * Returns the order of the filter.
	 * @return the order of the filter

	public int getFilterOrder() {
		return Math.max(aCoefficients.length, bCoefficients.length) - 1;
	}*/

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
		return new ResolvableString(EFFECT_CODES, new Object[] { filterType, getEffectString() }, getDefaultEffectDescription());
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
		return arguments;
	}

	@Override
	public String[] getCodes() {
		return messageCodes;
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
		if(tdf.filterType.equals(filterType) && tdf.approximationFunctionType.equals(approximationFunctionType) &&
			Arrays.equals(passbandEdgeFrequencies, tdf.passbandEdgeFrequencies) &&
			Arrays.equals(stopbandEdgeFrequencies, tdf.stopbandEdgeFrequencies) &&
			passbandRipple == tdf.passbandRipple && stopbandAttenuation == tdf.stopbandAttenuation)
				return true;
		return false;
	}


	/*public String getEffectString() {

		if (effectString == null) {

			StringBuilder sb = new StringBuilder("[");
			boolean first = true;

			Iterator<Range> it = ranges.iterator();
			Range range;
			while (it.hasNext()) {
				if (!first) {
					sb.append(", ");
				}
				range = it.next();
				sb.append('(').append(range.lowFrequency).append('-');
				if (range.highFrequency <= range.lowFrequency) {
					sb.append("Fn)");
				} else {
					sb.append(range.highFrequency).append(')');
				}
				sb.append('=').append(range.coefficient);
				first = false;
			}

			effectString = sb.append(']').toString();

		}

		return effectString;

	}

	@Override
	public String getDefaultEffectDescription() {
		return "FFT: " + getEffectString();
	}

	@Override
	public SampleFilterType getType() {
		return SampleFilterType.FFT;
	}

	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return CODES;
	}

	@Override
	public String getDefaultMessage() {
		return "FFT filter";
	}

	@Override
	public MessageSourceResolvable getEffectDescription() {
		return new ResolvableString(EFFECT_CODES, new Object[] { getEffectString() }, getDefaultEffectDescription());
	}*/

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
