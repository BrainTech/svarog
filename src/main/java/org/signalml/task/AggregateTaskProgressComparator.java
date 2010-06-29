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

	@Override
	public int compare(AggregateTaskProgressInfo o1, AggregateTaskProgressInfo o2) {
		return o1.compareTo(o2);
	}

}
