/* TagDifference.java created 2007-11-13
 *
 */

package org.signalml.domain.tag;

import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.signal.SignalSelectionType;

/**
 * This class represents the difference between two {@link Tag tags} from
 * different sets.
 * Difference is actually a selection representing that on this interval
 * in one set tag exists
 * (for the given {@link TagStyle style}, type and channel) and in
 * the other not.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagDifference extends SignalSelection implements Comparable<TagDifference> {

	private static final long serialVersionUID = 1L;

        /**
         * the {@link TagDifferenceType type} of the difference
         */
	private TagDifferenceType differenceType;

        /**
         * Constructor. Creates the difference.
         * @param type the {@link SignalSelectionType type} of a
         * {@link SignalSelection signal} selection which this difference
         * concerns
         * @param position the position when difference begins
         * @param length the length of the difference
         * @param channel the index of the channel
         * @param differenceType the {@link TagDifferenceType type} of
         * this difference
         */
	public TagDifference(SignalSelectionType type, float position, float length, int channel, TagDifferenceType differenceType) {
		super(type, position, length, channel);
		this.differenceType = differenceType;
	}

        /**
         * Constructor. Creates the difference.
         * @param type the {@link SignalSelectionType type} of a
         * {@link SignalSelection signal} selection which this difference
         * concerns
         * @param position the position when difference begins
         * @param length the length of the difference
         * @param differenceType the {@link TagDifferenceType type} of
         * this difference
         */
	public TagDifference(SignalSelectionType type, float position, float length, TagDifferenceType differenceType) {
		super(type, position, length);
		this.differenceType = differenceType;
	}

        /**
         * Returns the {@link SignalSelectionType type} of this difference.
         * @return the type of this difference.
         */
	public TagDifferenceType getDifferenceType() {
		return differenceType;
	}

        /**
         * Compares this difference to another difference.
         * Parameters used (in this order): starting position, length,
         * number of a channel, type of selection, type of a difference
         * @param t the difference to be compared to this reference
         * @return grater then 0 if this difference is larger,
         * less then 0 if this reference is smaller,
         * 0 if references are equal
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
         * Checks if this reference object is equal to given.
         * Uses {@link #compareTo}.
         * @param obj the object to be compared with this reference
         * @return true if the given object is equal to this reference,
         * false otherwise
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TagDifference)) {
			return false;
		}
		return (this.compareTo((TagDifference) obj) == 0);
	}

}
