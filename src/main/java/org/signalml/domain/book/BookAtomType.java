/* BookAtomType.java created 2008-03-20
 *
 */

package org.signalml.domain.book;

import org.springframework.context.MessageSourceResolvable;

/** BookAtomType
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum BookAtomType implements MessageSourceResolvable {

	DIRACDELTA,
	GAUSSFUNCTION,
	SINCOSWAVE,
	GABORWAVE

	;

	public static BookAtomType valueOf(int type) {
		switch (type) {

		case StandardBookAtom.DIRACDELTA_IDENTITY :
			return DIRACDELTA;

		case StandardBookAtom.GAUSSFUNCTION_IDENTITY :
			return GAUSSFUNCTION;

		case StandardBookAtom.SINCOSWAVE_IDENTITY :
			return SINCOSWAVE;

		case StandardBookAtom.GABORWAVE_IDENTITY :
		default :
			return GABORWAVE;

		}
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "bookAtomType." + name() };
	}

	@Override
	public String getDefaultMessage() {
		return name();
	}

}
