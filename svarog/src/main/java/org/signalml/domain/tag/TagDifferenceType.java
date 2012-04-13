/* TagDifferenceType.java created 2007-11-13
 *
 */

package org.signalml.domain.tag;

import java.awt.Color;

import org.signalml.plugin.export.signal.Tag;

/**
 * This enumerator represents the type of a {@link TagDifference difference} between
 * {@link Tag tags}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum TagDifferenceType {

	/**
	 * {@link Tag Tags} are the same. No difference.
	 */
	SAME(null),
	/**
	 * {@link Tag Tag} in the bottom on the part of the signal exists,
	 * tag on the top not.
	 */
	MISSING_IN_TOP(Color.GREEN),
	/**
	 * {@link Tag Tag} on the top on the part of the signal exists,
	 * tag in the bottom not.
	 */
	MISSING_IN_BOTTOM(Color.RED),
	/**
	 * Unknown difference between {@link Tag tags}.
	 */
	DIFFERENT(Color.ORANGE)

	;

	/**
	 * Colour in which the {@link TagDifference difference} will be painted.
	 */
	private Color color;

	/**
	 * Constructor. Creates {@link TagDifference difference} of a given
	 * colour.
	 * @param color the colour in which the difference will be painted
	 */
	private TagDifferenceType(Color color) {
		this.color = color;
	}

	/**
	 * Returns the colour of the {@link TagDifference difference}.
	 * @return the colour of the difference
	 */
	public Color getColor() {
		return color;
	}

}
