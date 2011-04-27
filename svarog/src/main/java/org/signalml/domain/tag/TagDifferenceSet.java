/* TagDifferenceSet.java created 2007-11-14
 *
 */

package org.signalml.domain.tag;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;

/**
 * This class represents a set of {@link TagDifference differences} between
 * {@link Tag tags}.
 * Allows to find which differences are located between two points in time.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagDifferenceSet {

        /**
         * the actual set containing tag {@link TagDifference differences}
         */
	private TreeSet<TagDifference> differences;

        /**
         * the length of the longest {@link TagDifference differences}
         */
	private double maxDifferenceLength = 0;

        /**
         * Constructor. Creates an empty TagDifferenceSet.
         */
	public TagDifferenceSet() {
		differences = new TreeSet<TagDifference>();
	}

        /**
         * Constructor. Creates a TagDifferenceSet with given
         * {@link TagDifference differences}.
         * @param differences the set of differences to be added
         */
	public TagDifferenceSet(TreeSet<TagDifference> differences) {
		this.differences = differences;
		calculateMaxTagLength();
	}

        /**
         * Returns the set containing {@link TagDifference tag differences}.
         * @return the set containing tag differences
         */
	public TreeSet<TagDifference> getDifferences() {
		return differences;
	}

        /**
         * Adds the given collection of {@link TagDifference tag differences}
         * to this set.
         * @param toAdd the collection of tag differences
         */
	public void addDifferences(Collection<TagDifference> toAdd) {
		differences.addAll(toAdd);
		calculateMaxTagLength();
	}

        /**
         * Returns the set of {@link TagDifference differences} for
         * {@link Tag tagged selections} that start between
         * <code>start-maxDifferenceLength</code> (inclusive)
         * and <code>end</code> (inclusive).
         * @param start the starting position of the interval
         * @param end the ending position of the interval
         * @return the set of differences for tagged selections
         * that start between <code>start-maxDifferenceLength</code> (inclusive)
         * and <code>end</code> (inclusive)
         */
        //TODO czy to na pewno ma zwracać to co napisałem, wydawało mi się, że mają to być różnice przecinające się z przedziałem, ale tu mogą się załapać także znajdujące się przed nim (i krótsze od maksymalnego)
	public SortedSet<TagDifference> getDifferencesBetween(double start, double end) {
		TagDifference startMarker = new TagDifference(SignalSelectionType.CHANNEL, start-maxDifferenceLength, 0, null);
		TagDifference endMarker = new TagDifference(SignalSelectionType.CHANNEL,end, Double.MAX_VALUE,null); // note that lengths matter, so that all tags starting at exactly end will be selected
		return differences.subSet(startMarker, true, endMarker, true);
	}

        /**
         * Calculates the maximal length of the difference in this set.
         */
	private void calculateMaxTagLength() {
		double maxDifferenceLength = 0;
		for (TagDifference difference : differences) {
			if (maxDifferenceLength < difference.getLength()) {
				maxDifferenceLength = difference.getLength();
			}
		}
		this.maxDifferenceLength = maxDifferenceLength;
	}


}
