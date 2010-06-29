/* TagDifferenceType.java created 2007-11-13
 *
 */

package org.signalml.domain.tag;

import java.awt.Color;

/** TagDifferenceType
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum TagDifferenceType {

	SAME(null),
	MISSING_IN_TOP(Color.GREEN),
	MISSING_IN_BOTTOM(Color.RED),
	DIFFERENT(Color.ORANGE)

	;

	private Color color;

	private TagDifferenceType(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}
