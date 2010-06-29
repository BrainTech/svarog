/* SignalSelectionType.java created 2007-09-28
 *
 */

package org.signalml.domain.signal;

import org.springframework.context.MessageSourceResolvable;

/** SignalSelectionType
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum SignalSelectionType implements MessageSourceResolvable {

	PAGE("page"),
	BLOCK("block"),
	CHANNEL("channel");

	private String name;

	private SignalSelectionType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isPage() {
		return (this == PAGE);
	}

	public boolean isBlock() {
		return (this == BLOCK);
	}

	public boolean isChannel() {
		return (this == CHANNEL);
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "signalSelectionType." + name };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}

}
