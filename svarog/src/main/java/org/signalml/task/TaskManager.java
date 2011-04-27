/* TaskManager.java created 2007-09-12
 *
 */
package org.signalml.task;

import java.util.Iterator;

/** TaskManager
 * class enables managing of Tasks.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TaskManager {

        /**
         * Returns number of Tasks
         * @return number of Tasks
         */
	int getTaskCount();

        /**
         * Returns Task from specified index
         * @param index index of Task to return
         * @return Task from specified index
         */
	Task getTaskAt(int index);

        /**
         * Returns index of specified Task
         * @param task Task to return index of
         * @return index of specified Task
         */
	int getIndexOfTask(Task task);

        /**
         * Returns Iterator over the Tasks in this manager
         * @return Iterator over the Tasks in this manager
         */
	Iterator<Task> iterator();

        /**
         * Returns Task of specified UID
         * @return Task of specified UID
         */
	Task getTaskByUID(String uid);

        /**
         * Adds specified Task to this manager Tasks
         * @param task Task to add to this manager
         */
	void addTask(Task task);

        /**
         * Removes specified Task from this manager Tasks
         * @param task Task to remove from this manager
         */
	void removeTask(Task task);

        /**
         * Removes Task from specified index
         * @param index index of Task to remove
         */
	void removeTaskAt(int index);

        /**
         * Adds specified TaskManagerListener to this manager
         * @param listener TaskManagerListener to add
         */
	void addTaskManagerListener(TaskManagerListener listener);

        /**
         * Removes specified TaskManagerListener from this manager
         * @param listener TaskManagerListener to remove
         */
	void removeTaskManagerListener(TaskManagerListener listener);

}
