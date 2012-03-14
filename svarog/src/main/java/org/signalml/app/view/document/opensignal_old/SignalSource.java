/* SignalSource.java created 2011-03-06
 *
 */

package org.signalml.app.view.document.opensignal_old;

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
