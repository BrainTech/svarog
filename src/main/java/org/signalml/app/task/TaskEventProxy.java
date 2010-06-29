/* TaskEventProxy.java created 2008-02-15
 *
 */

package org.signalml.app.task;

import javax.swing.event.EventListenerList;

import org.signalml.task.TaskEvent;
import org.signalml.task.TaskEventListener;

/** TaskEventProxy
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskEventProxy implements TaskEventListener {

	private EventListenerList listenerList = new EventListenerList();

	private ApplicationTaskWorker worker;

	public ApplicationTaskWorker getWorker() {
		return worker;
	}

	public void setWorker(ApplicationTaskWorker worker) {
		if (this.worker != worker) {
			if (this.worker != null) {
				this.worker.removeTaskEventListener(this);
				// release the old worker
				this.worker.destroy();
			}
			this.worker = worker;
			if (worker != null) {
				worker.addTaskEventListener(this);
			}
		}
	}

	public void addTaskEventListener(TaskEventListener listener) {
		listenerList.add(TaskEventListener.class, listener);
	}

	public void removeTaskEventListener(TaskEventListener listener) {
		listenerList.remove(TaskEventListener.class, listener);
	}

	@Override
	public void taskAborted(TaskEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskEventListener.class) {
				((TaskEventListener)listeners[i+1]).taskAborted(ev);
			}
		}
	}

	@Override
	public void taskFinished(TaskEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskEventListener.class) {
				((TaskEventListener)listeners[i+1]).taskFinished(ev);
			}
		}
	}

	@Override
	public void taskMessageSet(TaskEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskEventListener.class) {
				((TaskEventListener)listeners[i+1]).taskMessageSet(ev);
			}
		}
	}

	@Override
	public void taskRequestChanged(TaskEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskEventListener.class) {
				((TaskEventListener)listeners[i+1]).taskRequestChanged(ev);
			}
		}
	}

	@Override
	public void taskResumed(TaskEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskEventListener.class) {
				((TaskEventListener)listeners[i+1]).taskResumed(ev);
			}
		}
	}

	@Override
	public void taskStarted(TaskEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskEventListener.class) {
				((TaskEventListener)listeners[i+1]).taskStarted(ev);
			}
		}
	}

	@Override
	public void taskSuspended(TaskEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskEventListener.class) {
				((TaskEventListener)listeners[i+1]).taskSuspended(ev);
			}
		}
	}

	@Override
	public void taskTickerUpdated(TaskEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskEventListener.class) {
				((TaskEventListener)listeners[i+1]).taskTickerUpdated(ev);
			}
		}
	}

}
