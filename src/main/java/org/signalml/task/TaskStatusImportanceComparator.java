/* TaskStatusImportanceComparator.java created 2007-10-19
 *
 */

package org.signalml.task;

import java.util.Comparator;

/** This Comparator compares task statuses according to their importance.
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskStatusImportanceComparator implements Comparator<TaskStatus> {

	/**
	 * Compares two instances of class TastStatus according to their importace.
	 * @param o1 first TaskStatus to compare
	 * @param o2 second TaskStatus to compare
	 * @return positive number if importance of first argument is greater than importance of second argument, negative if it is smaller and zero they are equal
	 */
	@Override
	public int compare(TaskStatus o1, TaskStatus o2) {
		return o1.getImportance() - o2.getImportance();
	}

}
