/* DefaultTaskManager.java created 2007-09-12
 *
 */
package org.signalml.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

/** DefaultTaskManager
 * class provides default implementation of {@link TaskManager}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultTaskManager implements TaskManager {

	/**
	 * Logger named "DefaultTaskManager"
	 */
	protected static final Logger logger = Logger.getLogger(DefaultTaskManager.class);

	private Vector<Task> tasks = new Vector<Task>(100,100);
	private Map<String,Task> tasksByUID = new HashMap<String,Task>(100);

	private EventListenerList listenerList = new EventListenerList();

	/**
	 * Constructs an empty DefaultTaskManager.
	 */
	public DefaultTaskManager() {

	}

	/**
	 * Returns amount of Tasks.
	 * Note that when second thread calls getTaskAmount() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @return amount of Tasks
	 */
	public int getTaskCount() {
		synchronized (this) {
			return tasks.size();
		}
	}

	/**
	 * Returns Task from specified index.
	* Note that when second thread calls getTaskAt() on the same object, it will have to wait for the first thread to complete the call to this method.
	       * @return Task from specified index
	       */
	public Task getTaskAt(int index) {
		synchronized (this) {
			return tasks.elementAt(index);
		}
	}

	/**
	 * Returns index of specified Task.
	 * Note that when second thread calls getIndexOfTask() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @return index of specified Task
	 */
	public int getIndexOfTask(Task task) {
		synchronized (this) {
			return tasks.indexOf(task);
		}
	}

	/**
	 * Returns Iterator over DefaultTaskManager.
	 * Note that when second thread calls iterator() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @return Iterator over DefaultTastManager
	 */
	public Iterator<Task> iterator() {
		synchronized (this) {
			return tasks.iterator();
		}
	}

	/**
	 * Returns Task with specified UID.
	 * Note that when second thread calls getTaskByUID()  on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @return Task with specified UID
	 */
	public Task getTaskByUID(String uid) {
		synchronized (this) {
			return tasksByUID.get(uid);
		}
	}

	/**
	 * Adds specified Task to this DefaultTaskManager.
	 * Note that when second thread calls addTask() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @param task task to add to this DeafaultTaskManager
	* @throws RuntimeException when exist Task in this DefaultTaskManager with the same UID as Task to add
	       */
	public void addTask(Task task) {
		synchronized (this) {
			if (tasks.contains(task)) {
				return;
			}
			if (tasksByUID.containsKey(task.getUID())) {
				throw new RuntimeException("Sanity check failed, the same uid on different task");
			}
			tasks.add(task);
			tasksByUID.put(task.getUID(),task);
			fireTaskAdded(task, tasks.indexOf(task));
		}
	}

	/**
	 * Removes specified Task from this DefaultTaskManager and starts executing it. If it does not contain such Task, nothing happens.
	 * Note that when second thread calls removeTask() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @param task Task to remove
	 */
	public void removeTask(Task task) {
		synchronized (this) {
			if (!tasks.contains(task)) {
				return;
			}
			int index = tasks.indexOf(task);
			tasksByUID.remove(task.getUID());
			tasks.remove(task);
			fireTaskRemoved(task, index);
		}
	}

	/**
	 * Removes Task from specified index. If this DefaultTaskManager does not contain such Task, nothing happens.
	 * Note that when second thread calls removeTask() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @param index index of Task to remove
	 */
	public final void removeTaskAt(int index) {
		synchronized (this) {
			Task task = tasks.elementAt(index);
			removeTask(task);
		}
	}


	/**
	 * Starts executing added Task.
	 * @param task Task to start
	* @param index index of Task to start
	       */
	protected void fireTaskAdded(Task task, int index) {
		Object[] listeners = listenerList.getListenerList();
		TaskManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskManagerListener.class) {
				if (e == null) {
					e = new TaskManagerEvent(this,task,index);
				}
				((TaskManagerListener)listeners[i+1]).taskAdded(e);
			}
		}
	}

	/**
	 * Starts executing removed Task.
	 * @param task Task to start
	 * @param index index of Task to start
	 */
	protected void fireTaskRemoved(Task task, int index) {
		Object[] listeners = listenerList.getListenerList();
		TaskManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskManagerListener.class) {
				if (e == null) {
					e = new TaskManagerEvent(this,task,index);
				}
				((TaskManagerListener)listeners[i+1]).taskRemoved(e);
			}
		}
	}

	/**
	 * Adds specified TaskManagerListener to this DefaultTaskManager.
	 * Note that when second thread calls addTaskManagerListener() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @param listener listener to add to this DeafaultTaskManager
	 */
	public void addTaskManagerListener(TaskManagerListener listener) {
		synchronized (this) {
			listenerList.add(TaskManagerListener.class, listener);
		}
	}

	/**
	 * Removes specified TaskManagerListener from this DefaultTaskManager.
	 * Note that when second thread calls removeTaskManagerListener() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @param listener listener to remove from this DeafaultTaskManager
	 */
	public void removeTaskManagerListener(TaskManagerListener listener) {
		synchronized (this) {
			listenerList.remove(TaskManagerListener.class, listener);
		}
	}

}
