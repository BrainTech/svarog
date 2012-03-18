/* OpenFileSignalMethod.java created 2011-03-12
 *
 */

package org.signalml.app.model.document.opensignal.elements;

import org.springframework.context.MessageSourceResolvable;

/**
 * This class represents a method which should be used to open a file signal
 * determining whether it is a raw signal or a SignalML codec should be used
 * to open it.
 *
 * @author Piotr Szachewicz
 */
public enum FileOpenSignalMethod implements MessageSourceResolvable {

	RAW,
	SIGNALML;

	/**
	 * Returns whether the signal is SignalML.
	 * @return true if the signal is a SignalML document.
	 */
	public boolean isSignalML() {
		return (this == SIGNALML);
	}

	/**
	 * Returns whether the signal is a raw document.
	 * @return true if the signal is a raw signal document, false otherwise.
	 */
	public boolean isRaw() {
		return (this == RAW);
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "openSignal.options.method." + this.toString()};
	}

	@Override
	public String getDefaultMessage() {
		return this.toString();
	}

};
