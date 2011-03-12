/* OpenFileSignalMethod.java created 2011-03-12
 *
 */

package org.signalml.app.view.opensignal;

import org.springframework.context.MessageSourceResolvable;

/**
 *
 * @author Piotr Szachewicz
 */
public enum FileOpenSignalMethod implements MessageSourceResolvable {

	RAW,
	SIGNALML;

	public boolean isSignalML() {
		return (this == SIGNALML);
	}

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
