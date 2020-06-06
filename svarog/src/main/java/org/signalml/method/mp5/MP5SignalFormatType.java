/* MP5SignalFormatType.java created 2007-10-03
 *
 */

package org.signalml.method.mp5;

import java.io.Serializable;
import org.springframework.context.MessageSourceResolvable;

/** MP5SignalFormatType
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum MP5SignalFormatType implements Serializable, MessageSourceResolvable {

	ASCII("ASCII"),
	FLOAT("FLOAT"),
	SHORT("SHORT");

	private String name;

	private MP5SignalFormatType(String name) {
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
		return new String[] { "mp5SignalFormatType." + name };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}

}
