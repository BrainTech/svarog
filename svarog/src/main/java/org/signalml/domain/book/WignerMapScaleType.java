/* WignerMapScaleType.java created 2008-03-03
 *
 */

package org.signalml.domain.book;

import org.springframework.context.MessageSourceResolvable;

/** WignerMapScaleType
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum WignerMapScaleType implements MessageSourceResolvable {

	NORMAL,
	LOG,
	SQRT
	;

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "wignerMapScaleType." + name() };
	}

	@Override
	public String getDefaultMessage() {
		return name();
	}

}
