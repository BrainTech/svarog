/* FilterType.java created 2010-09-12
 *
 */

package org.signalml.math.iirdesigner;

import org.springframework.context.MessageSourceResolvable;

/**
 * This enumeration type represents whether the filter is lowpass, highpass,
 * bandpass or bandstop.
 *
 * @author Piotr Szachewicz
 */
public enum FilterType implements MessageSourceResolvable {

	LOWPASS(false),
	HIGHPASS(false),
	BANDPASS(true),
	BANDSTOP(true),
	NOTCH(false),
	PEAK(false);

	private static final Object[] EMPTY_ARGUMENTS = new Object[0];

	private final boolean needsSecondFrequency;

	private FilterType(boolean needsSecondFrequency) {
		this.needsSecondFrequency = needsSecondFrequency;
	}

	public boolean isLowpass() {
		return (this == LOWPASS);
	}

	public boolean isHighpass() {
		return (this == HIGHPASS);
	}

	public boolean isBandpass() {
		return (this == BANDPASS);
	}

	public boolean isBandstop() {
		return (this == BANDSTOP);
	}

	public boolean isNotch() {
		return (this == NOTCH);
	}

	public boolean isPeak() {
		return (this == PEAK);
	}

	public boolean needsSecondFrequency() {
		return needsSecondFrequency;
	}

	@Override
	public Object[] getArguments() {
		return EMPTY_ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return new String[] { "iirdesigner.filterType." + this.toString()};
	}

	@Override
	public String getDefaultMessage() {
		return this.toString();
	}

}