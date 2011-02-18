/* TimeDomainSampleFilterValidator.java created 2011-02-17
 *
 */

package org.signalml.domain.montage.filter;

import org.signalml.domain.montage.filter.iirdesigner.FilterType;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This class respresents a validator which is capable of checking
 * if the frequencies describing the filter are correct.
 *
 * @author Piotr Szachewicz
 */
public final class TimeDomainSampleFilterValidator {

	/**
	 * The source of localized messages (labels).
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * The filter checked by this validator.
	 */
	private TimeDomainSampleFilter filter;

	/**
	 * True if the checked filter is valid, false otherwise.
	 */
	private boolean isValid;

	/**
	 * This message describes the problem if the filter is not valid.
	 */
	private String errorMessage = "";

	/**
	 * Creates a new validator for the given filter.
	 * @param messageSource the source of localized messages
	 * @param filter the filter to be validated
	 */
	public TimeDomainSampleFilterValidator(MessageSourceAccessor messageSource, TimeDomainSampleFilter filter) {
		this.messageSource = messageSource;
		this.filter = filter;

		FilterType filterType = filter.getFilterType();

		switch(filterType) {
			case HIGHPASS: isValid = isHighpassValid(); break;
			case LOWPASS: isValid = isLowpassValid(); break;
			case BANDPASS: isValid = isBandpassValid(); break;
			case BANDSTOP: isValid = isBandstopValid(); break;
		}
	}

	/**
	 * Returns true if the filter is valid, false otherwise.
	 * If this method returns false, you can check the message returned
	 * by the {@link TimeDomainSampleFilterValidator#getErrorMessage() }
	 * method.
	 * @return true if the filter is valid, false otherwise
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * If the filter is invalid, this method returns a String describing
	 * the problem. Otherwise, it returns an empty String.
	 * @return
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Checks if the filter fulfills the passband and stopband frequency
	 * requirements for a highpass filter. If the filter is not correct,
	 * this method sets an error message which describes the problem.
	 * @return true if the filter is correct, false otherwise
	 */
	private boolean isHighpassValid() {
		if(filter.getStopbandEdgeFrequencies()[0] < filter.getPassbandEdgeFrequencies()[0]) {
			return true;
		}
		else {
			errorMessage = messageSource.getMessage("timeDomainFilter.highpassFilterNotValidMessage");
			return false;
		}
	}

	/**
	 * Checks if the filter fulfills the passband and stopband frequency
	 * requirements for a lowpass filter. If the filter is not correct,
	 * this method sets an error message which describes the problem.
	 * @return true if the filter is correct, false otherwise
	 */
	private boolean isLowpassValid() {
		if(filter.getPassbandEdgeFrequencies()[0] < filter.getStopbandEdgeFrequencies()[0]) {
			return true;
		}
		else {
			errorMessage = messageSource.getMessage("timeDomainFilter.lowpassFilterNotValidMessage");
			return false;
		}
	}

	/**
	 * Checks if the filter fulfills the passband and stopband frequency
	 * requirements for a bandpass filter. If the filter is not correct,
	 * this method sets an error message which describes the problem.
	 * @return true if the filter is correct, false otherwise
	 */
	private boolean isBandpassValid() {
		if (filter.getStopbandEdgeFrequencies()[0] < filter.getPassbandEdgeFrequencies()[0] &&
		    filter.getPassbandEdgeFrequencies()[0] < filter.getPassbandEdgeFrequencies()[1] &&
		    filter.getPassbandEdgeFrequencies()[1] < filter.getStopbandEdgeFrequencies()[1]) {
			return true;
		}
		else {
			errorMessage = messageSource.getMessage("timeDomainFilter.bandpassFilterNotValidMessage");
			return false;
		}
	}

	/**
	 * Checks if the filter fulfills the passband and stopband frequency
	 * requirements for a bandstop filter. If the filter is not correct,
	 * this method sets an error message which describes the problem.
	 * @return true if the filter is correct, false otherwise
	 */
	private boolean isBandstopValid() {
		if(filter.getPassbandEdgeFrequencies()[0] < filter.getStopbandEdgeFrequencies()[0] &&
		   filter.getStopbandEdgeFrequencies()[0] < filter.getStopbandEdgeFrequencies()[1] &&
		   filter.getStopbandEdgeFrequencies()[1] < filter.getPassbandEdgeFrequencies()[1]) {
			return true;
		}
		else {
			errorMessage = messageSource.getMessage("timeDomainFilter.bandstopFilterNotValidMessage");
			return false;
		}
	}

}
