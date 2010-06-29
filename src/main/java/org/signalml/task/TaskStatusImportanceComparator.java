/* TaskStatusImportanceComparator.java created 2007-10-19
 *
 */

package org.signalml.task;

import java.util.Comparator;

/** This compares task statuses based on importance.
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskStatusImportanceComparator implements Comparator<TaskStatus> {

	@Override
	public int compare(TaskStatus o1, TaskStatus o2) {
		return o1.getImportance() - o2.getImportance();
	}

}
