/* MP5DictionaryReinitType.java created 2007-10-03
 *
 */

package org.signalml.method.mp5;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/** MP5DictionaryReinitType
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum MP5DictionaryReinitType implements Serializable, MessageSourceResolvable {

	NO_REINIT_AT_ALL("NO_REINIT_AT_ALL"),
	REINIT_IN_CHANNEL_DOMAIN("REINIT_IN_CHANNEL_DOMAIN"),
	REINIT_IN_OFFSET_DOMAIN("REINIT_IN_OFFSET_DOMAIN"),
	REINIT_AT_ALL("REINIT_AT_ALL");

	private String name;

	private MP5DictionaryReinitType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "mp5Method.dictionaryReinitType." + name };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}

}
