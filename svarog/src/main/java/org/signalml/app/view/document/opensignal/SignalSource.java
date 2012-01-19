/* SignalSource.java created 2011-03-06
 *
 */

package org.signalml.app.view.document.opensignal;

import org.springframework.context.MessageSourceResolvable;

/**
 * This enumeration represents possible source of signal:
 * a file, a running openBCI system, an amplifier.
 *
 * @author Piotr Szachewicz
 */
public enum SignalSource implements MessageSourceResolvable {

	FILE,
	OPENBCI,
	AMPLIFIER,
	;

	/**
	 * Returns if this signal source is a file.
	 * @return true if this signal source is a file, false otherwise
	 */
	public boolean isFile() {
		return (this == FILE);
	}

	/**
	 * Returns if this signal source is a runnig openBCI system.
	 * @return true if this signal source is openBCI, false otherwise
	 */
	public boolean isOpenBCI() {
		return (this == OPENBCI);
	}

	/**
	 * Returns if this signal source is an amplifier.
	 * @return true if this signal source is an amplifier, false otherwise
	 */
	public boolean isAmplifier() {
		return (this == AMPLIFIER);
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] {"opensignal.signalsource." + this.toString()};
	}

	@Override
	public String getDefaultMessage() {
		return this.toString();
	}

}
