/* SignalSource.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import org.springframework.context.MessageSourceResolvable;

/**
 *
 * @author Piotr Szachewicz
 */
public enum SignalSource implements MessageSourceResolvable {

	FILE,
	OPENBCI,
	AMPLIFIER,
	;

	public boolean isFile() {
		return (this == FILE);
	}

	public boolean isOpenBCI() {
		return (this == OPENBCI);
	}

	public boolean isAmplifier() {
		return (this == AMPLIFIER);
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "opensignal.signalsource." + this.toString()};
	}

	@Override
	public String getDefaultMessage() {
		return this.toString();
	}

}