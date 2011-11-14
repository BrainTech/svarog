/* TimeDomainSampleFilterValidator.java created 2011-02-17
 *
 */

package org.signalml.domain.montage.filter;

import static org.signalml.app.SvarogApplication._;
import java.util.ArrayList;
import java.util.List;
import org.signalml.domain.montage.filter.iirdesigner.FilterType;

/**
 * This class respresents a validator which is capable of checking
 * if the frequencies describing the filter are correct.
 *
 * @author Piotr Szachewicz
 */
public final class TimeDomainSampleFilterValidator {

	/**
	 * The filter checked by this validator.
	 */
	private TimeDomainSampleFilter filter;

	/**
	 * True if the checked filter is valid, false otherwise.
	 */
	private boolean isValid;

	/**
	 * This errorMessages describes the problems if the filter is not valid.
	 */
	private List<String> errorMessages = new ArrayList<String>();

	/**
	 * Creates a new validator for the given filter.
	 * @param filter the filter to be validated
	 */
	public TimeDomainSampleFilterValidator( TimeDomainSampleFilter filter) {
		this.filter = filter;

		FilterType filterType = filter.getFilterType();

		switch(filterType) {
			case HIGHPASS: isValid = isHighpassValid(); break;
			case LOWPASS: isValid = isLowpassValid(); break;
			case BANDPASS: isValid = isBandpassValid(); break;
			case BANDSTOP: isValid = isBandstopValid(); break;
		}

		boolean isRippleValid = isRippleAndAttenuationValid();
		if (!isRippleValid)
			isValid = false;
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
	public List<String> getErrorMessages() {
		return errorMessages;
	}

	/**
	 * Adds a new error message to the list of error messages.
	 * @param message a message to be added
	 */
	protected void addErrorMessage(String message) {
		errorMessages.add(message);
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
			addErrorMessage(_("For high-pass filters the frequencies must fulfill: stopband frequency 1 < passband frequency 1."));
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
			addErrorMessage(_("For low-pass filters the frequencies must fulfill: passband frequency 1 < stopband frequency 1."));
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
			addErrorMessage(_("For band-pass filters the frequencies must fulfill: stopband frequency 1 < passband frequency 1 < passband frequency 2 < stopband frequency 2."));
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
			addErrorMessage(_("For band-stop filters the frequencies must fulfill: passband frequency 1 < stopband frequency 1 < stopband frequency 2 < passband frequency 2."));
			return false;
		}
	}

	/**
	 * Checks if the passband ripple and stopband attenuation values are
	 * correct.
	 * @return true if the values are correct, false otherwise
	 */
	private boolean isRippleAndAttenuationValid() {
		if (filter.getPassbandRipple() < filter.getStopbandAttenuation())
			return true;
		else {
			addErrorMessage(_("Passband ripple should be less than stopband attenuation."));
			return false;
		}
	}

}
