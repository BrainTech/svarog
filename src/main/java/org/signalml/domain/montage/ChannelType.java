/* ChannelType.java created 2007-10-20
 *
 */

package org.signalml.domain.montage;

import org.springframework.context.MessageSourceResolvable;

/**
 * Enumerator with possible types of channels.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum ChannelType implements MessageSourceResolvable {

	/**
	 * "normal" channel with signal
	 */
	PRIMARY,
	/**
	 * channel used as reference
	 */
	REFERENCE,
	/**
	 * other known type
	 */
	OTHER,
	/**
	 * an empty (fake) channel
	 */
	EMPTY,

	/**
	 * unknown type
	 */
	UNKNOWN
	;

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "channelType." + this.toString() };
	}

	@Override
	public String getDefaultMessage() {
		return this.toString();
	}

}
