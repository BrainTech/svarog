/* FilterType.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.springframework.context.MessageSourceResolvable;

/**
 * This enumeration type represents whether the filter is lowpass, highpass,
 * bandpass or bandstop.
 *
 * @author Piotr Szachewicz
 */
public enum FilterType implements MessageSourceResolvable {

	LOWPASS,
	HIGHPASS,
	BANDPASS,
	BANDSTOP
	;

/*	private String name;

	private FilterType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}*/

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

	@Override
	public Object[] getArguments() {
		return new Object[0];
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