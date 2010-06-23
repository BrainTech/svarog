/* ApplicationTaskWorker.java created 2007-10-18
 * 
 */

package org.signalml.app.task;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.task.Task;
import org.signalml.task.TaskEvent;
import org.signalml.task.TaskEventListener;
import org.signalml.task.TaskResult;
import org.signalml.task.TaskStatus;

/** ApplicationTaskWorker
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ApplicationTaskWorker extends SwingWorker<TaskResult, TaskEvent> implements TaskEventListener {

	protected static final Logger logger = Logger.getLogger(ApplicationTaskWorker.class);
	
	private Task task;

	private EventListenerList listenerList = new EventListenerList();
	private PleaseWaitDialog pleaseWaitDialog = null;
		
	public ApplicationTaskWorker(Task task) {
		super();
		this.task = task;		
		task.addTaskEventListener(this);
	}		
			
	public Task getTask() {
		return task;
	}

	public void destroy() {
		task.removeTaskEventListener(this);
	}
	
	@Override
	protected void done() {
		if( pleaseWaitDialog != null ) {
			pleaseWaitDialog.releaseIfOwnedBy(this);
		}
	}

	@Override
	protected void process(List<TaskEvent> chunks) {
		for( TaskEvent ev : chunks ) {
			fireTaskEvent(ev);
		}
	}
	
	@Override
	protected TaskResult doInBackground() throws Exception {
		
		TaskStatus status;
					
		logger.debug("Entering do if possible");
		task.doIfPossible();
		logger.debug("Exited do if possible");

		if( isCancelled() ) {
			logger.debug("Worker is cancelled, exiting");
			return null;
		}
			
		synchronized(task) {

			status = task.getStatus();				
			logger.debug("Task status is [" + status + "]" );
			if( status.isResultAvailable() ) {
				logger.debug("Worker returning with result" );
				return task.getResult();
			}
			
		}
			
		logger.debug("Worker returning with null" );
		
		return null;
		
	}	
	
	@Override
	public void taskAborted(TaskEvent ev) {
		// ensure the worker relays the last possible event after it has stopped
		// (abort of a suspended task)
		if( isDone() && SwingUtilities.isEventDispatchThread() ) {
			fireTaskEvent(ev);
		}
		publish(ev);
	}

	@Override
	public void taskFinished(TaskEvent ev) {
		publish(ev);
	}

	@Override
	public void taskResumed(TaskEvent ev) {
		publish(ev);
	}

	@Override
	public void taskStarted(TaskEvent ev) {
		publish(ev);
	}

	@Override
	public void taskSuspended(TaskEvent ev) {
		publish(ev);
	}
	
	@Override
	public void taskRequestChanged(TaskEvent ev) {
		publish(ev);
	}

	@Override
	public void taskMessageSet(TaskEvent ev) {
		publish(ev);
	}
	
	@Override
	public void taskTickerUpdated(TaskEvent ev) {
		publish(ev);
	}

	public void addTaskEventListener(TaskEventListener listener) {
		listenerList.add(TaskEventListener.class, listener);
	}

	public void removeTaskEventListener(TaskEventListener listener) {
		listenerList.remove(TaskEventListener.class, listener);
	}
	
	protected void fireTaskEvent(TaskEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskEventListener.class) {
				 switch( ev.getType() ) {
				 
				 	case TASK_STARTED :
				 		((TaskEventListener)listeners[i+1]).taskStarted(ev);
				 		break;
				 	case TASK_SUSPENDED :
				 		((TaskEventListener)listeners[i+1]).taskSuspended(ev);
				 		break;
				 	case TASK_RESUMED :
				 		((TaskEventListener)listeners[i+1]).taskResumed(ev);
				 		break;
				 	case TASK_ABORTED :
				 		((TaskEventListener)listeners[i+1]).taskAborted(ev);
				 		break;
				 	case TASK_FINISHED :
				 		((TaskEventListener)listeners[i+1]).taskFinished(ev);
				 		break;
				 	case TASK_REQUEST_CHANGED :
				 		((TaskEventListener)listeners[i+1]).taskRequestChanged(ev);
				 		break;
				 	case TASK_MESSAGE_SET :
				 		((TaskEventListener)listeners[i+1]).taskMessageSet(ev);
				 		break;
				 	case TASK_TICKER_UPDATED :
				 		((TaskEventListener)listeners[i+1]).taskTickerUpdated(ev);
				 		break;
				 	default :
				 		logger.error( "Unsupported event type [" + ev.getType() + "]" );				 	
				 		break;
				 		
				 }
				 
			 }
		 }
		
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		return pleaseWaitDialog;
	}

	public void setPleaseWaitDialog(PleaseWaitDialog pleaseWaitDialog) {
		this.pleaseWaitDialog = pleaseWaitDialog;
	}
		
}
