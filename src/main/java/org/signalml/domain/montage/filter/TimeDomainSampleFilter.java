/* TimeDomainSampleFilter.java created 2008-02-01
 *
 */

package org.signalml.domain.montage.filter;

import java.util.Arrays;
import org.signalml.util.ResolvableString;
import org.springframework.context.MessageSourceResolvable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class holds a time domain representation of a
 * {@link SampleFilterDefinition sample filter}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("timeDomainSampleFilter")
public class TimeDomainSampleFilter extends SampleFilterDefinition {

	private static final long serialVersionUID = 1L;

	protected Object[] arguments;
	protected String[] messageCodes;

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

		this.setDescription("Time Domain Filter");

	}

	public double[] getACoefficients() {
		return aCoefficients;
	}

	public double[] getBCoefficients() {
		return bCoefficients;
	}

	/**
	 * Returns the order of the filter.
	 * @return the order of the filter
	 */
	public int getFilterOrder() {
		return Math.max(aCoefficients.length, bCoefficients.length) - 1;
	}

	@Override
	public MessageSourceResolvable getEffectDescription() {
		return new ResolvableString(messageCodes, arguments, getDefaultEffectDescription());
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
	public SampleFilterDefinition duplicate() {

		TimeDomainSampleFilter duplicate = new TimeDomainSampleFilter();

		duplicate.aCoefficients = aCoefficients.clone();
		duplicate.bCoefficients = bCoefficients.clone();
		duplicate.messageCodes = messageCodes.clone();
		duplicate.arguments = arguments.clone();
		duplicate.setDescription(this.description);

		return duplicate;

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
		if ((Arrays.equals(aCoefficients, tdf.aCoefficients)) && Arrays.equals(bCoefficients, tdf.bCoefficients))
			return true;
		else
			return false;
	}

}
