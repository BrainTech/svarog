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
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultTaskManager implements TaskManager {

	protected static final Logger logger = Logger.getLogger(DefaultTaskManager.class);
	
	private Vector<Task> tasks = new Vector<Task>(100,100);
	private Map<String,Task> tasksByUID = new HashMap<String,Task>(100);
	
	private EventListenerList listenerList = new EventListenerList();
	
	public DefaultTaskManager() {
		
	}
	
	public int getTaskCount() {
		synchronized( this ) {
			return tasks.size();
		}
	}
	
	public Task getTaskAt(int index) {
		synchronized( this ) {
			return tasks.elementAt(index);
		}
	}
	
	public int getIndexOfTask(Task task) {
		synchronized( this ) {
			return tasks.indexOf(task);
		}
	}
	
	public Iterator<Task> iterator() {
		synchronized( this ) {
			return tasks.iterator();
		}
	}
	
	public Task getTaskByUID(String uid) {
		synchronized( this ) {
			return tasksByUID.get(uid);
		}
	}
	
	public void addTask(Task task) {
		synchronized( this ) {
			if( tasks.contains(task) ) {
				return;
			}
			if( tasksByUID.containsKey(task.getUID()) ) {
				throw new RuntimeException("Sanity check failed, the same uid on different task");			
			}
			tasks.add(task);
			tasksByUID.put(task.getUID(),task);
			fireTaskAdded(task, tasks.indexOf(task));
		}
	}			
	
	public void removeTask(Task task) {
		synchronized( this ) {
			if( !tasks.contains(task) ) {
				return;
			}
			int index = tasks.indexOf(task);
			tasksByUID.remove(task.getUID());
			tasks.remove(task);
			fireTaskRemoved(task, index);
		}
	}
	
	public final void removeTaskAt(int index) {
		synchronized( this ) {
			Task task = tasks.elementAt(index);
			removeTask(task);
		}
	}

	
	protected void fireTaskAdded(Task task, int index) {
		Object[] listeners = listenerList.getListenerList();
		TaskManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskManagerListener.class) {
				 if( e == null ) { 
					 e = new TaskManagerEvent(this,task,index);
				 }
				 ((TaskManagerListener)listeners[i+1]).taskAdded(e);
			 }
		 }
	}
	
	protected void fireTaskRemoved(Task task, int index) {
		Object[] listeners = listenerList.getListenerList();
		TaskManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskManagerListener.class) {
				 if( e == null ) { 
					 e = new TaskManagerEvent(this,task,index);
				 }
				 ((TaskManagerListener)listeners[i+1]).taskRemoved(e);
			 }
		 }
	}
	
	public void addTaskManagerListener(TaskManagerListener listener) {
		synchronized( this ) {
			listenerList.add(TaskManagerListener.class, listener);
		}
	}

	public void removeTaskManagerListener(TaskManagerListener listener) {
		synchronized( this ) {
			listenerList.remove(TaskManagerListener.class, listener);
		}
	}
		
}
