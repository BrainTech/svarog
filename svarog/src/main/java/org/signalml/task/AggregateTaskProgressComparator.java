/* AggregateTaskProgressComparator.java created 2007-10-19
 *
 */

package org.signalml.task;

import java.util.Comparator;

/** A comparator comparing the overall progress of tasks.
 *
 * @see AggregateTaskProgressInfo
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AggregateTaskProgressComparator implements Comparator<AggregateTaskProgressInfo> {

	/**
	 * Compares two instances of classAggregateTaskProgressInfo according to their overall progress.
	 * @param o1 first AggregateTaskProgressInfo to compare
	 * @param o2 second AggregateTastProgressInfo to compare
	 * @return positive number if overall progress of first argument is greater than overall progress of second argument, negative if it is smaller and zero they are equal
	 */
	@Override
	public int compare(AggregateTaskProgressInfo o1, AggregateTaskProgressInfo o2) {
		return o1.compareTo(o2);
	}

}
