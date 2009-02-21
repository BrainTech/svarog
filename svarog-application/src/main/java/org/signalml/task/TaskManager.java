/* TaskManager.java created 2007-09-12
 * 
 */
package org.signalml.task;

import java.util.Iterator;

/** TaskManager
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TaskManager {

	int getTaskCount();
	Task getTaskAt(int index);
	int getIndexOfTask(Task task);
	
	Iterator<Task> iterator();
	Task getTaskByUID(String uid);
	
	void addTask(Task task);
	
	void removeTask(Task task);	
	void removeTaskAt(int index);		

	void addTaskManagerListener(TaskManagerListener listener);
	void removeTaskManagerListener(TaskManagerListener listener);
		
}
