/* MP5WritingModeType.java created 2007-10-03
 *
 */

package org.signalml.method.mp5;

import java.io.Serializable;
import org.springframework.context.MessageSourceResolvable;

/** MP5WritingModeType
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum MP5WritingModeType implements Serializable, MessageSourceResolvable {

	CREATE("CREATE"),
	APPEND("APPEND");

	private String name;

	private MP5WritingModeType(String name) {
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
		return new String[] { "mp5WritingModeType." + name };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}

}
