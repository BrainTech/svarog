/* LocalTask.java created 2007-09-12
 * 
 */
package org.signalml.task;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.signalml.method.ComputationException;
import org.signalml.method.InputDataException;
import org.signalml.method.Method;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.SuspendableMethod;
import org.signalml.method.TrackableMethod;
import org.signalml.task.TaskEvent.TaskEventType;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.task.TaskExecutor;

/** LocalTask
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class LocalTask implements Task, MethodExecutionTracker, Runnable {
	
	protected static final Logger logger = Logger.getLogger(LocalTask.class);
	
	private static final int STATISTICS_LENGTH = 20;
	
	private Method method;
	private String uid;
	private TaskInfo info;

	private volatile TaskStatus status;

	private Object data;
	private volatile Object result;
	private volatile Exception exception;
	private volatile MessageSourceResolvable message;
	
	private EventListenerList listenerList = new EventListenerList();
	
	private int[] tickerLimits = new int[0];
	private int[] tickers = new int[0];
	
	private boolean collectTickStatistics;
	private long[] lastTickMillis = null;
	private long[][] tickStatisticTimes = null;
	private int[][] tickStatisticTicks = null;
	private int[] statisticPointers;
	private int[] statisticLengths;
		
	private Object executionMonitorObject = new Object();
	
	private void debug( String message ) {
		logger.debug( "TASK [" + hashCode() + "]: " + message );
	}
	
	public LocalTask( Method method, Object data ) {
		this(method,data,false);
	}

	public LocalTask( Method method, Object data, boolean collectTickStatistics ) {
		debug("Creating new task");
		if( method == null ) {
			throw new NullPointerException();
		}
		this.collectTickStatistics = collectTickStatistics;
		this.method = method;		
		this.data = data;
		uid = UUID.randomUUID().toString();
		info = new TaskInfo();
		info.setCreateTime(new Date());
		if( method instanceof TrackableMethod ) {
			tickerLimits = new int[((TrackableMethod) method).getTickerCount()];
			tickers = new int[tickerLimits.length];
			if( collectTickStatistics ) {
				lastTickMillis = new long[tickerLimits.length];
				tickStatisticTimes = new long[tickerLimits.length][STATISTICS_LENGTH];
				tickStatisticTicks = new int[tickerLimits.length][STATISTICS_LENGTH];
				statisticPointers = new int[tickerLimits.length];
				statisticLengths = new int[tickerLimits.length];
			}
		} else {
			this.collectTickStatistics = false;
		}
		if( (method instanceof SuspendableMethod) && ((SuspendableMethod) method).isDataSuspended(data) ) {
			status = TaskStatus.SUSPENDED;
			debug("Created as suspended");
		} else {
			status = TaskStatus.NEW;
			debug("Created as new");
		}
	}
	
	public LocalTask(Method method, Object data, boolean collectTickStatistics, TaskStatus status) {
		this(method,data,collectTickStatistics);
		switch( status ) {
		
		case FINISHED :
			if( method instanceof TrackableMethod ) {
				TrackableMethod trackableMethod = (TrackableMethod) method;
				int tickerCount = trackableMethod.getTickerCount();
				tickerLimits = new int[tickerCount];
				Arrays.fill(tickerLimits, 1);
				tickers = new int[tickerCount];
				Arrays.fill(tickers, 1);
			}
			break;
			
		case SUSPENDED :
		case NEW :
			// do nothing
			break;
			
		default :
			// error for others
			throw new IllegalArgumentException("Cannot create tasks in this status [" + status + "]" );
		
		}
		
		this.status = status;		
	}

	@Override
	public Method getMethod() {
		synchronized( this ) {
			return method;
		}
	}

	@Override
	public TaskResult getResult() throws InvalidTaskStateException {
		
		synchronized( this ) {
		
			if( !status.isResultAvailable() ) {
				throw new InvalidTaskStateException("error.taskStateMustBeFinishedOrError");
			}
			
			return new TaskResult(uid, info, status, result, exception);
			
		}
		
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public TaskStatus getStatus() {
		synchronized( this ) {
			return status;
		}
	}

	@Override
	public TaskInfo getTaskInfo() {
		synchronized( this ) {
			return info;
		}
	}

	@Override
	public String getUID() {
		synchronized( this ) {
			return uid;
		}
	}

	public void start(TaskExecutor t) throws InvalidTaskStateException {
		synchronized( this ) {
			if( !status.isStartable() ) {
				throw new InvalidTaskStateException("error.taskStatusMustBeNew");
			}
			status = TaskStatus.ACTIVE_WAITING;
			if( t == null ) {
				(new Thread(this)).start();
			} else {
				t.execute(this);
			}
			debug("Task started");			
		}
	}

	public void startAndDo() throws InvalidTaskStateException {
		synchronized( this ) {
			if( !status.isStartable() ) {
				throw new InvalidTaskStateException("error.taskStatusMustBeNew");
			}
			status = TaskStatus.ACTIVE;
			info.setStartTime(new Date());
			fireTaskStarted();
			notifyAll();			
		}
		debug("Task starting on current thread");					
		run();
	}
		
	@Override
	public void doIfPossible() {
		synchronized( this ) {
			if( !status.isStartable() && !status.isResumable() ) {
				// not possible
				return;
			}
			if( status.isResumable() ) {
				status = TaskStatus.ACTIVE;		
				info.setResumeTime(new Date());
				fireTaskResumed();
				debug("Task resuming on current thread");
			}
			else if( status.isStartable() ) {
				status = TaskStatus.ACTIVE;
				info.setStartTime(new Date());
				fireTaskStarted();
				debug("Task starting on current thread");					
			}
			notifyAll();			
		}
		run();
	}

	@Override
	public void abort(boolean wait) throws InvalidTaskStateException {		
		synchronized( this ) {

			if( !status.isAbortable() ) {
				throw new InvalidTaskStateException("error.taskNotAbortable");
			}
			if( status.isSuspended() ) {
				onAbort(); // abort immediately
			}
			else {
				status = TaskStatus.REQUESTING_ABORT;
				fireTaskRequestChanged();
				notifyAll();				
				debug("Task requesting abort, wait [" + wait + "]" );			
				if( wait ) {
					do {
						try {
							wait();
						} catch( InterruptedException ex ) {}
					} while( !status.isAborted() );
					
					debug("Task has been aborted" );							
				}
			}		
		}
	}
	
	@Override
	public void suspend(boolean wait) throws InvalidTaskStateException {
		synchronized( this ) {
		
			if( !(method instanceof SuspendableMethod) || !status.isSuspendable() ) {
				throw new InvalidTaskStateException("error.taskNotSuspendable");			
			}
			status = TaskStatus.REQUESTING_SUSPEND;
			fireTaskRequestChanged();
			notifyAll();							
			debug("Task requesting suspend, wait [" + wait + "]" );			
			if( wait ) {
				do {
					try {
						wait();
					} catch( InterruptedException ex ) {}
				} while( !status.isSuspended() );
				
				debug("Task has been suspended" );							
			}
			
		}
	}
	
	public void resume(TaskExecutor t) throws InvalidTaskStateException {		
		synchronized( this ) {
			
			if( !(method instanceof SuspendableMethod) || !status.isResumable() ) {
				throw new InvalidTaskStateException("error.taskNotSuspendable");			
			}
			status = TaskStatus.ACTIVE;		
			info.setResumeTime(new Date());
			fireTaskResumed();
			notifyAll();			
			if( t == null ) {
				(new Thread(this)).start();
			} else {
				t.execute(this);
			}
			debug("Task resumed");						
		}
	}

	public void resumeAndDo() throws InvalidTaskStateException {		
		synchronized( this ) {
			if( !(method instanceof SuspendableMethod) || !status.isResumable() ) {
				throw new InvalidTaskStateException("error.taskNotSuspendable");			
			}
			status = TaskStatus.ACTIVE;		
			info.setResumeTime(new Date());
			fireTaskResumed();
			notifyAll();			
		}
		debug("Task resuming on current thread");
		run();
	}
	
	public void onAbort() {
		synchronized( this ) {

			status = TaskStatus.ABORTED;
			info.setEndTime(new Date());
			fireTaskAborted();
			notifyAll();
			debug("Task aborted");						
			
		}
	}
		
	public void onSuspend() {
		synchronized( this ) {

			status = TaskStatus.SUSPENDED;
			info.setSuspendTime(new Date());
			fireTaskSuspended();
			notifyAll();
			debug("Task suspended");						
			
		}
	}
	
	public void onFinish() {
		synchronized( this ) {
		
			if( exception != null ) {
				status = TaskStatus.ERROR;
				debug("Task finished with error");
				logger.debug("Task finished with error", exception);
			} else {
				status = TaskStatus.FINISHED;			
				debug("Task finished ok");
			}
			info.setEndTime(new Date());
			fireTaskFinished();
			notifyAll();							
			
		}
	}
	
	@Override
	public void run() {
		
		Object result = null;
		Exception exception = null;
		
		debug("Task entered run");
		
		synchronized( this ) {
			if( status.isActiveAndWaiting() ) {
				status = TaskStatus.ACTIVE;
				info.setStartTime(new Date());
				fireTaskStarted();
				notifyAll();
			}		
		}

		synchronized( executionMonitorObject ) {
			debug("Task begins to run");
			try {
				try {
					result = method.compute(data, this);
				} catch( InputDataException ex ) {
					debug("Input data exception");
					exception = ex;
				} catch( ComputationException ex ) {
					debug("Computation exception");
					exception = ex;
				}
			} catch( Throwable t ) {
				debug("Other exception or error");
				exception = new ComputationException(t);
			}
			debug("Task finished running");
		}
		synchronized( this ) {
			debug("Analyzing run result");
			if( exception != null ) {
				this.result = null;
				this.exception = exception;
				debug("Exception detected");				
				onFinish();
			} else if( result != null ) {
				this.result = result;
				this.exception = null;				
				debug("Result detected");				
				onFinish();
			} else {
				if( status == TaskStatus.REQUESTING_ABORT ) {
					this.result = null;
					this.exception = null;
					debug("Abort detected");				
					onAbort();
				} else if( status == TaskStatus.REQUESTING_SUSPEND ) {
					this.result = null;
					this.exception = null;
					debug("Suspend detected");				
					onSuspend();
				} else {
					this.result = null;
					this.exception = new ComputationException("error.noResult");
					debug("Finish with no result - throw exception");				
					onFinish();
				}
			}
		}
		
	}
	
	@Override
	public void addTaskEventListener(TaskEventListener listener) {
		synchronized( this ) {
			listenerList.add(TaskEventListener.class, listener);
		}
	}
	
	@Override
	public void removeTaskEventListener(TaskEventListener listener) {
		synchronized( this ) {
			listenerList.remove(TaskEventListener.class, listener);
		}
	}
				
	@Override
	public boolean isRequestingAbort() {
		return status.isRequestingAbort();
	}

	@Override
	public boolean isRequestingSuspend() {
		return status.isRequestingSuspend();
	}

	@Override
	public MessageSourceResolvable getMessage() {
		return message;
	}

	@Override
	public void setMessage(MessageSourceResolvable message) {
		synchronized( this ) {
			this.message = message;
			debug("Task message set to [" + message + "]");				
			fireTaskMessageSet();
		}
	}

	@Override
	public int[] getTickerLimits() {
		synchronized( this ) {
			return Arrays.copyOf(tickerLimits, tickerLimits.length);
		}
	}
	
	@Override
	public void setTickerLimits(int[] initial) {
		synchronized( this ) {
		
			for( int i=0; i<initial.length; i++ ) {
				tickerLimits[i] = Math.max( 0, initial[i] );
				tickers[i] = Math.min( tickers[i], initial[i] );
			}
			if( collectTickStatistics ) {
				clearStatistics();
			}			
			fireTaskTickerUpdated();

		}
	}
	
	@Override
	public void setTickerLimit(int index, int limit) {
		synchronized( this ) {

			tickerLimits[index] = Math.max( 0, limit );
			tickers[index] = Math.min( tickers[index], limit );
			if( collectTickStatistics ) {
				clearStatistics(index);
			}
			fireTaskTickerUpdated();
			
		}
	}
	
	@Override
	public int[] getTickers() {
		synchronized( this ) {
			return Arrays.copyOf(tickers, tickers.length);
		}
	}
	
	@Override
	public void resetTickers() {
		synchronized( this ) {

			Arrays.fill(tickers, 0);
			if( collectTickStatistics ) {
				clearStatistics();
			}
			fireTaskTickerUpdated();
			
		}
	}
	
	@Override
	public void setTickers(int[] tickers) {
		synchronized( this ) {

			int oldVal;
			for( int i=0; i<tickers.length; i++ ) {
				oldVal = this.tickers[i];
				this.tickers[i] = Math.max( 0, Math.min(tickerLimits[i], tickers[i]) );
				if( collectTickStatistics ) {
					addToStatistics(i, oldVal, tickers[i]);
				}
			}
			fireTaskTickerUpdated();
			
		}
	}
	
	@Override
	public void setTicker(int index, int value) {
		synchronized( this ) {

			int oldVal = tickers[index];
			tickers[index] = Math.max( 0, Math.min(tickerLimits[index], value) );
			if( collectTickStatistics ) {
				addToStatistics(index, oldVal, tickers[index]);
			}			
			fireTaskTickerUpdated();
			
		}
	}
	
	@Override
	public void tick(int index) {
		synchronized( this ) {

			int oldVal = tickers[index];
			if( tickers[index] < tickerLimits[index] ) {
				tickers[index]++;
			}
			if( collectTickStatistics ) {
				addToStatistics(index, oldVal, tickers[index]);
			}			
			fireTaskTickerUpdated();
			
		}
	}
	
	@Override
	public void tick(int index, int step) {
		synchronized( this ) {
		
			int oldVal = tickers[index];
			tickers[index] = Math.min( tickerLimits[index], tickers[index] + step );
			if( collectTickStatistics ) {
				addToStatistics(index, oldVal, tickers[index]);
			}			
			fireTaskTickerUpdated();
		
		}
	}
	
	@Override
	public Integer getExpectedSecondsUntilComplete(int index) {
		if( !collectTickStatistics ) {
			return null;
		}
		synchronized( this ) {
			if( statisticLengths[index] < 3 ) {
				return null;
			}
			int i;
			int pos = statisticPointers[index];
			long totalTime = 0;
			int totalTicks = 0;
			for( i=0; i<statisticLengths[index]; i++ ) {
				pos = ( STATISTICS_LENGTH + pos - 1 ) % STATISTICS_LENGTH; 
				totalTime += tickStatisticTimes[index][pos];
				totalTicks += tickStatisticTicks[index][pos];
			}
			float tps = ((float) totalTicks) / (((float) totalTime)/1000);
			
			int seconds = (int) Math.round( ((float) (tickerLimits[index]-tickers[index])) / tps );
			
			// subtract elapsed
			seconds -= ( (System.currentTimeMillis()-lastTickMillis[index])/1000 );
			if( seconds < 0 ) {
				seconds = 0;
			}
			
			return new Integer( seconds );
		}
	}
	
	private void addToStatistics(int index, int oldVal, int newVal) {
		
		if( newVal <= oldVal ) {
			/* no advance */
			return;
		}
		
		long millis = System.currentTimeMillis();
		
		if( lastTickMillis[index] == 0 ) {
			lastTickMillis[index] = millis;
			/* no sample this time */
			return;
		}
		
		long elapsed = millis - lastTickMillis[index];
		lastTickMillis[index] = millis;
		int gained = newVal - oldVal;
		
		tickStatisticTimes[index][statisticPointers[index]] = elapsed;
		tickStatisticTicks[index][statisticPointers[index]] = gained;
		
		statisticPointers[index] = ( statisticPointers[index] + 1 ) % STATISTICS_LENGTH;
		if( statisticLengths[index] < STATISTICS_LENGTH ) {
			statisticLengths[index]++;
		}
				
	}
	
	private void clearStatistics() {
		for( int i=0; i<tickerLimits.length; i++ ) {
			clearStatistics(i);
		}
	}
		
	private void clearStatistics(int index) {
		lastTickMillis[index] = 0;
		statisticLengths[index] = 0;
		statisticPointers[index] = 0;
		Arrays.fill( tickStatisticTimes[index], 0 );
		Arrays.fill( tickStatisticTicks[index], 0 );
	}

	protected void fireTaskStarted() {
		Object[] listeners = listenerList.getListenerList();
		TaskEvent taskEvent = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskEventListener.class) {
				 if (taskEvent == null) {
					 taskEvent = new TaskEvent(this, TaskEventType.TASK_STARTED, getStatus());
				 }
				 ((TaskEventListener)listeners[i+1]).taskStarted(taskEvent);
			 }
		 }
	}

	protected void fireTaskSuspended() {
		Object[] listeners = listenerList.getListenerList();
		TaskEvent taskEvent = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskEventListener.class) {
				 if (taskEvent == null) {
					 taskEvent = new TaskEvent(this, TaskEventType.TASK_SUSPENDED, getStatus());
				 }
				 ((TaskEventListener)listeners[i+1]).taskSuspended(taskEvent);
			 }
		 }
	}

	protected void fireTaskRequestChanged() {
		Object[] listeners = listenerList.getListenerList();
		TaskEvent taskEvent = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskEventListener.class) {
				 if (taskEvent == null) {
					 taskEvent = new TaskEvent(this, TaskEventType.TASK_REQUEST_CHANGED, getStatus());
				 }
				 ((TaskEventListener)listeners[i+1]).taskRequestChanged(taskEvent);
			 }
		 }
	}
	
	protected void fireTaskResumed() {
		Object[] listeners = listenerList.getListenerList();
		TaskEvent taskEvent = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskEventListener.class) {
				 if (taskEvent == null) {
					 taskEvent = new TaskEvent(this, TaskEventType.TASK_RESUMED, getStatus());
				 }
				 ((TaskEventListener)listeners[i+1]).taskResumed(taskEvent);
			 }
		 }
	}
	
	protected void fireTaskAborted() {
		Object[] listeners = listenerList.getListenerList();
		TaskEvent taskEvent = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskEventListener.class) {
				 if (taskEvent == null) {
					 taskEvent = new TaskEvent(this, TaskEventType.TASK_ABORTED, getStatus());
				 }
				 ((TaskEventListener)listeners[i+1]).taskAborted(taskEvent);
			 }
		 }
	}
	
	protected void fireTaskFinished() {
		Object[] listeners = listenerList.getListenerList();
		TaskEvent taskEvent = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskEventListener.class) {
				 if (taskEvent == null) {
					 taskEvent = new TaskEvent(this, TaskEventType.TASK_FINISHED, getStatus(), getResult() );
				 }
				 ((TaskEventListener)listeners[i+1]).taskFinished(taskEvent);
			 }
		 }
	}

	protected void fireTaskMessageSet() {
		Object[] listeners = listenerList.getListenerList();
		TaskEvent taskEvent = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskEventListener.class) {
				 if (taskEvent == null) {
					 taskEvent = new TaskEvent(this, TaskEventType.TASK_MESSAGE_SET, getStatus(), getMessage() );
				 }
				 ((TaskEventListener)listeners[i+1]).taskMessageSet(taskEvent);
			 }
		 }
	}
	
	protected void fireTaskTickerUpdated() {
		Object[] listeners = listenerList.getListenerList();
		TaskEvent taskEvent = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TaskEventListener.class) {
				 if (taskEvent == null) {
					 taskEvent = new TaskEvent(this, TaskEventType.TASK_TICKER_UPDATED, getStatus(), getTickerLimits(), getTickers());
				 }
				 ((TaskEventListener)listeners[i+1]).taskTickerUpdated(taskEvent);
			 }
		 }
	}
		
}
