/* TagDifferenceSet.java created 2007-11-14
 *
 */

package org.signalml.domain.tag;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.signalml.domain.signal.SignalSelectionType;

/** TagDifferenceSet
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagDifferenceSet {

	private TreeSet<TagDifference> differences;

	private float maxDifferenceLength = 0;

	public TagDifferenceSet() {
		differences = new TreeSet<TagDifference>();
	}

	public TagDifferenceSet(TreeSet<TagDifference> differences) {
		this.differences = differences;
		calculateMaxTagLength();
	}

	public TreeSet<TagDifference> getDifferences() {
		return differences;
	}

	public void addDifferences(Collection<TagDifference> toAdd) {
		differences.addAll(toAdd);
		calculateMaxTagLength();
	}

	public SortedSet<TagDifference> getDifferencesBetween(float start, float end) {
		TagDifference startMarker = new TagDifference(SignalSelectionType.CHANNEL, start-maxDifferenceLength, 0, null);
		TagDifference endMarker = new TagDifference(SignalSelectionType.CHANNEL,end,Float.MAX_VALUE,null); // note that lengths matter, so that all tags starting at exactly end will be selected
		return differences.subSet(startMarker, true, endMarker, true);
	}

	private void calculateMaxTagLength() {
		float maxDifferenceLength = 0;
		for (TagDifference difference : differences) {
			if (maxDifferenceLength < difference.getLength()) {
				maxDifferenceLength = difference.getLength();
			}
		}
		this.maxDifferenceLength = maxDifferenceLength;
	}


}
