/* TagDifferenceType.java created 2007-11-13
 *
 */

package org.signalml.domain.tag;

import java.awt.Color;

/**
 * This class represents the type of a difference between tags
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum TagDifferenceType {

        /**
         * Tags are the same. No difference
         */
	SAME(null),
        /**
         * Tag in the bottom on the part of the signal exists, tag on the top not
         */
	MISSING_IN_TOP(Color.GREEN),
        /**
         * Tag on the top on the part of the signal exists, tag in the bottom not
         */
	MISSING_IN_BOTTOM(Color.RED),
        /**
         * Unknown difference between tags
         */
	DIFFERENT(Color.ORANGE)

	;

        /**
         * Colour in which the difference will be painted
         */
	private Color color;

        /**
         * Constructor. Creates difference of a given colour
         * @param color the colour in which the difference will be painted
         */
	private TagDifferenceType(Color color) {
		this.color = color;
	}

        /**
         * Returns the colour of the difference
         * @return the colour of the difference
         */
	public Color getColor() {
		return color;
	}

}
