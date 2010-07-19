/* TagDifference.java created 2007-11-13
 *
 */

package org.signalml.domain.tag;

import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.signal.SignalSelectionType;

/**
 * This class represents the difference between tags.
 * Difference is actually a selection representing that on this interval one tag
 * (for the given style, type and channel) exists and other not.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagDifference extends SignalSelection implements Comparable<TagDifference> {

	private static final long serialVersionUID = 1L;

        /**
         * The {@link TagDifferenceType type} of the difference
         */
	private TagDifferenceType differenceType;

        /**
         * Constructor. Creates the difference
         * @param type the type of a signal selection which the difference
         * concerns
         * @param position the position when difference begins
         * @param length the length of the difference
         * @param channel the index of the channel
         * @param differenceType the type of the difference
         */
	public TagDifference(SignalSelectionType type, float position, float length, int channel, TagDifferenceType differenceType) {
		super(type, position, length, channel);
		this.differenceType = differenceType;
	}

        /**
         * Constructor. Creates the difference
         * @param type the type of a signal selection which the difference
         * concerns
         * @param position the position when difference begins
         * @param length the length of the difference
         * @param differenceType the type of the difference
         */
	public TagDifference(SignalSelectionType type, float position, float length, TagDifferenceType differenceType) {
		super(type, position, length);
		this.differenceType = differenceType;
	}

        /**
         * Returns the type of the difference
         * @return the type of the difference
         */
	public TagDifferenceType getDifferenceType() {
		return differenceType;
	}

        /**
         * Compares the difference to another difference.
         * Parameters used (in this order): starting position, length,
         * number of channel, type of selection, type of difference
         * @param t the difference to be compared to the current object
         * @return grater then 0 if the current object is larger,
         * less then 0 if the current object is smaller,
         * 0 if objects are equal
         */
	@Override
	public int compareTo(TagDifference t) {

		float test = position - t.position;
		if (test == 0) {
			test = length - t.length;
			if (test == 0) {
				test = channel - t.channel;
				if (((int) test) == 0) {
					int itest = type.ordinal() - t.type.ordinal();
					if (itest == 0) {
						return differenceType.ordinal() - t.differenceType.ordinal();
					}
				}
			}
		}
		return (int) Math.signum(test);

	}

        /**
         * Checks if the current object is equal to given.
         * Uses {@linkplain compareTo()}
         * @param obj the object to be compared with current
         * @return true if the given object is equal to current, false otherwise
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TagDifference)) {
			return false;
		}
		return (this.compareTo((TagDifference) obj) == 0);
	}

}
