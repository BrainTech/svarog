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
import org.signalml.plugin.export.method.SvarogTask;
import org.signalml.task.TaskEvent.TaskEventType;
import org.springframework.core.task.TaskExecutor;

/**
 * LocalTask class enables managing of Tasks.
 * It also allows to controll method execution and receive progress feedback.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class LocalTask implements SvarogTask, MethodExecutionTracker, Runnable {

	/**
	 *  Logger for debug info.
	 */
	protected static final Logger logger = Logger.getLogger(LocalTask.class);

	private static final int STATISTICS_LENGTH = 20;

	private Method method;
	private String uid;
	private TaskInfo info;

	private volatile TaskStatus status;

	private final Object data;
	private volatile Object result;
	private volatile Exception exception;
	private volatile String message;

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

	private void debug(String message) {
		logger.debug("TASK [" + hashCode() + "]: " + message);
	}

        /**
         * Creates new instance of Task with specified Method and Data.
         * Sets Task's UID, creation time, status and prepares tickers.
         *
	 * Note that this Task will not collect tick statistics.
         *
         * @param method method to be computed by this Task
         * @param data arguments of the method
         * @throws NullPointerException when specified Method is null
         */
	public LocalTask(Method method, Object data) {
		this(method,data,false);
	}

        /**
         * Creates a new instance of Task with specified Method and Data.
         * Sets Task's UID, creation time, status and prepares tickers.
	 *
	 * When boolean collectTickStatistics is true and method is a {@link TrackableMethod}, this
	 * Task will collect tick statistics which enables reading progress status.
         *
         * @param method method to be computed by this Task
         * @param data arguments of the method
         * @param collectTickStatistics is true if Task should collect tick statistics
	 * @throws NullPointerException when specified Method is null
         */
	public LocalTask(Method method, Object data, boolean collectTickStatistics) {
		debug("Creating new task");
		if (method == null) {
			throw new NullPointerException();
		}
		this.collectTickStatistics = collectTickStatistics;
		this.method = method;
		this.data = data;
		uid = UUID.randomUUID().toString();
		info = new TaskInfo();
		info.setCreateTime(new Date());
		if (method instanceof TrackableMethod) {
			tickerLimits = new int[((TrackableMethod) method).getTickerCount()];
			tickers = new int[tickerLimits.length];
			if (collectTickStatistics) {
				lastTickMillis = new long[tickerLimits.length];
				tickStatisticTimes = new long[tickerLimits.length][STATISTICS_LENGTH];
				tickStatisticTicks = new int[tickerLimits.length][STATISTICS_LENGTH];
				statisticPointers = new int[tickerLimits.length];
				statisticLengths = new int[tickerLimits.length];
			}
		} else {
			this.collectTickStatistics = false;
		}
		if ((method instanceof SuspendableMethod) && ((SuspendableMethod) method).isDataSuspended(data)) {
			status = TaskStatus.SUSPENDED;
			debug("Created as suspended");
		} else {
			status = TaskStatus.NEW;
			debug("Created as new");
		}
	}

	/**
	 * Creates a new instance of Task with specified Method, Data and TaskStatus.  When boolean
	 * collectTickStatistics is true and method is a {@link TrackableMethod}, this Task will
	 * collect tick statistics which enables reading progress status.
	 *
	 * @param method method to be computed by this Task
	 * @param data arguments of the method
	 * @param collectTickStatistics true if Task should collect tick statistics
	 * @param status status of this Task
	 * @throws IllegalArgumentException when Status is not a valid Task status
	 * @throws NullPointerException when specified Method is null
	 */
	public LocalTask(Method method, Object data, boolean collectTickStatistics, TaskStatus status) {
		this(method,data,collectTickStatistics);
		switch (status) {

		case FINISHED :
			if (method instanceof TrackableMethod) {
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
			throw new IllegalArgumentException("Cannot create tasks in this status [" + status + "]");

		}

		this.status = status;
	}

	/**
	 * Returns the method that is being executed by this task.
	 * Note that when second thread calls getMethod() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @return the method
	 */
	@Override
	public Method getMethod() {
		synchronized (this) {
			return method;
		}
	}

	/**
	 *  Returns the result of the execution. Note that "result" here may also mean
	 *  an exception that resulted from the execution. Tasks that did not yet finish
	 *  (either normally or with exception), including those that were aborted and will
	 *  never finish do not have a result to return and will throw an exception. Use
	 *  the {@link TaskStatus#isResultAvailable()} method on the task's status to test
	 *  for result availability.
	 *
	 *  <p>Note that when second thread calls getResult() on the same object, it will have to wait for the first thread to complete the call to this method.
	 *
	 * @return the result descriptor
	 * @throws InvalidTaskStateException thrown when the task doesn't have a result (possibly yet).
	 */
	@Override
	public TaskResult getResult() throws InvalidTaskStateException {

		synchronized (this) {

			if (!status.isResultAvailable()) {
				throw new InvalidTaskStateException("error.taskStateMustBeFinishedOrError");
			}

			return new TaskResult(uid, info, status, result, exception);

		}

	}

	/**
	 * Returns the data object used as input data for the computation.
	 * @return the data object used as input data for the computation.
	 */
	@Override
	public Object getData() {
		return data;
	}

	/**
	 * Returns task status.
	 * Note that when second thread calls getStatus() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @return the status
	 */
	@Override
	public TaskStatus getStatus() {
		synchronized (this) {
			return status;
		}
	}

	/**
	 * Returns task information object.
	 * Note that when second thread calls getTaskInfo() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @return the task information object.
	 */
	@Override
	public TaskInfo getTaskInfo() {
		synchronized (this) {
			return info;
		}
	}

	/**
	 * Returns this task's unique identifier (must be unique for all tasks of all types
	 * AND accross VMs).
	 * Note that when second thread calls getUID() on the same object, it will have to wait for the first thread to complete the call to this method.
	 * @return the unique identifier.
	 */
	@Override
	public String getUID() {
		synchronized (this) {
			return uid;
		}
	}

        /**
	 * Prepares starting the execution of the task (the computation) on the new thread if specified TaskExecutor is null, otherwise by this TaskExecutor.
	 * Note that "starting" in this case menas only to post a request for starting by setting the status to ACTIVE_WAITING.
         * Note that when second thread calls start() on the same object, it will have to wait for the first thread to complete the call to this method.
         * @param t executor of Task
	 * @throws InvalidTaskStateException thrown when the task status doesn't allow starting
	 */
	public void start(TaskExecutor t) throws InvalidTaskStateException {
		synchronized (this) {
			if (!status.isStartable()) {
				throw new InvalidTaskStateException("error.taskStatusMustBeNew");
			}
			status = TaskStatus.ACTIVE_WAITING;
			if (t == null) {
				(new Thread(this)).start();
			} else {
				t.execute(this);
			}
			debug("Task started");
		}
	}

        /**
	 * Starts the execution of the task (the computation) on the <b> current thread</b> if the task is currently startable.
	 * The method returns only when the computation has finished (due to being completed, aborted, suspeneded or due to an exception).
         * Note that when second thread calls startAndDo() on the same object, it will have to wait for the first thread to complete the call to this method.
         * @throws InvalidTaskStateException thrown when the task status doesn't allow starting
         */
	public void startAndDo() throws InvalidTaskStateException {
		synchronized (this) {
			if (!status.isStartable()) {
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

	/**
	 *  Starts or resumes the execution of the task (the computation) on the <b>current thread</b>
	 *  if the task is currently startable or resumable. The method returns only when the computation
	 *  has finished (due to being completed, aborted, suspeneded or due to an exception).
	 *  Note that when second thread calls doIfPossible() on the same object, it will have to wait for the first thread to complete the call to this method.
	 */
	@Override
	public void doIfPossible() {
		synchronized (this) {
			if (!status.isStartable() && !status.isResumable()) {
				// not possible
				return;
			}
			if (status.isResumable()) {
				status = TaskStatus.ACTIVE;
				info.setResumeTime(new Date());
				fireTaskResumed();
				debug("Task resuming on current thread");
			}
			else if (status.isStartable()) {
				status = TaskStatus.ACTIVE;
				info.setStartTime(new Date());
				fireTaskStarted();
				debug("Task starting on current thread");
			}
			notifyAll();
		}
		run();
	}

	/**
	 *  Aborts the execution of this task. The task must be running or exceptions will be thrown. Note that
	 *  "aborting" in this case menas only to post a request for abortion by setting the status to
	 *  REQUESTING_ABORT. It is up to the method's compute implementation to check for this status and exit
	 *  when it is detected.
	 *
	 *  <p>This method may also wait for the task to abort, but this may lead to the calling thread being
	 *  permanently locked if the compute method never finishes.
	 *
	 *  <p>Note that when second thread calls abort() on the same object, it will have to wait for the first thread to complete the call to this method.
	 *
	 * @param wait whether to wait for the task to abort
	 * @throws InvalidTaskStateException thrown when the task status doesn't allow aborting
	 */
	@Override
	public void abort(boolean wait) throws InvalidTaskStateException {
		synchronized (this) {

			if (!status.isAbortable()) {
				throw new InvalidTaskStateException("error.taskNotAbortable");
			}
			if (status.isSuspended()) {
				onAbort(); // abort immediately
			} else {
				status = TaskStatus.REQUESTING_ABORT;
				fireTaskRequestChanged();
				notifyAll();
				debug("Task requesting abort, wait [" + wait + "]");
				if (wait) {
					do {
						try {
							wait();
						} catch (InterruptedException ex) {}
					} while (!status.isAborted());

					debug("Task has been aborted");
				}
			}
		}
	}

	/**
	 *  Suspends the execution of this task. The task must be running and the method it runs
	 *  must be suspendalbe or exceptions will be thrown. Note that "suspending" in this case menas
	 *  only to post a request for suspension by setting the status to REQUESTING_SUSPEND.
	 *  It is up to the method's compute implementation to check for this status and exit
	 *  when it is detected.
	 *
	 *  <p>This method may also wait for the task to suspend, but this may lead to the calling thread being
	 *  permanently locked if the compute method never finishes.
	 *
	 *  Note that when second thread calls suspend() on the same object, it will have to wait for the first thread to complete the call to this method.
	 *
	 * @param wait whether to wait for the task to suspend
	 * @throws InvalidTaskStateException thrown when the task status doesn't allow suspending or the method
	 * 		isn't suspendable
	 */
	@Override
	public void suspend(boolean wait) throws InvalidTaskStateException {
		synchronized (this) {

			if (!(method instanceof SuspendableMethod) || !status.isSuspendable()) {
				throw new InvalidTaskStateException("error.taskNotSuspendable");
			}
			status = TaskStatus.REQUESTING_SUSPEND;
			fireTaskRequestChanged();
			notifyAll();
			debug("Task requesting suspend, wait [" + wait + "]");
			if (wait) {
				do {
					try {
						wait();
					} catch (InterruptedException ex) {}
				} while (!status.isSuspended());

				debug("Task has been suspended");
			}

		}
	}

        /**
	 *  Resumes the execution of this task. The task must be suspended and the method it runs
         *  must be suspendable or exceptions will be thrown.
	 *
	 *  Note that "resuming" in this case menas only to post a request for resumption by setting the status to ACTIVE.
         *  It is up to the method's compute implementation to check for this status and resume when it is detected.
         *
         *  <p>This method may also wait for the task to resume, but this may lead to the calling thread being
         *  permanently locked if the compute method never finishes.
         *
         *  Note that when second thread calls resume() on the same object, it will have to wait for the first thread to complete the call to this method.
         *
         * @param t executor of this task
         * @throws InvalidTaskStateException thrown when the task status doesn't allow resuming or the method
         *              isn't suspendable
         */
	public void resume(TaskExecutor t) throws InvalidTaskStateException {
		synchronized (this) {

			if (!(method instanceof SuspendableMethod) || !status.isResumable()) {
				throw new InvalidTaskStateException("error.taskNotSuspendable");
			}
			status = TaskStatus.ACTIVE;
			info.setResumeTime(new Date());
			fireTaskResumed();
			notifyAll();
			if (t == null) {
				(new Thread(this)).start();
			} else {
				t.execute(this);
			}
			debug("Task resumed");
		}
	}

        /**
	 *  Resumes the execution of this task. The task must be suspended and the method it runs
         *  must be suspendable or exceptions will be thrown.
         *
         *  <p>Note that when second thread calls resumeAndDo() on the same object, it will have to wait for the first thread to complete the call to this method.
         *
         * @throws InvalidTaskStateException thrown when the task status doesn't allow resuming or the method
         *              isn't suspendable
         */
	public void resumeAndDo() throws InvalidTaskStateException {
		synchronized (this) {
			if (!(method instanceof SuspendableMethod) || !status.isResumable()) {
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


	/**
	 * Method called on aborting this Task. It sets status to ABORTED and time of end of execution.
	 */
	private void onAbort() {
		synchronized (this) {

			status = TaskStatus.ABORTED;
			info.setEndTime(new Date());
			fireTaskAborted();
			notifyAll();
			debug("Task aborted");

		}
	}

        /**
         * Method called on suspension this Task. It sets status to SUSPENDED and time of suspension of execution.
         */
	private void onSuspend() {
		synchronized (this) {

			status = TaskStatus.SUSPENDED;
			info.setSuspendTime(new Date());
			fireTaskSuspended();
			notifyAll();
			debug("Task suspended");

		}
	}

        /**
         * Method called on finish of this Task. It sets status to FINISHED if no error occured, otherwise ERROR.
	 * It also set a time of end of execution.
         */
	private void onFinish() {
		synchronized (this) {

			if (exception != null) {
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

        /**
         * Computes this Task result.
	 * If any exception occurs during computation process it sets this error as result.
	 * Possible errors:
	 * - InputDataException when the input Data is invalid
	 * - ComputationException when computation fails for reasons other than bad input data
	 *   or when no result is returned
         *
         */
	@Override
	public void run() {

		Object result = null;
		Exception exception = null;

		debug("Task entered run");

		synchronized (this) {
			if (status.isActiveAndWaiting()) {
				status = TaskStatus.ACTIVE;
				info.setStartTime(new Date());
				fireTaskStarted();
				notifyAll();
			}
		}

		synchronized (executionMonitorObject) {
			debug("Task begins to run");
			try {
				try {
					result = method.compute(data, this);
				} catch (InputDataException ex) {
					debug("Input data exception");
					exception = ex;
				} catch (ComputationException ex) {
					debug("Computation exception");
					exception = ex;
				}
			} catch (Throwable t) {
				debug("Other exception or error");
				exception = new ComputationException(t);
			}
			debug("Task finished running");
		}
		synchronized (this) {
			debug("Analyzing run result");
			if (exception != null) {
				this.result = null;
				this.exception = exception;
				debug("Exception detected");
				onFinish();
			} else if (result != null) {
				this.result = result;
				this.exception = null;
				debug("Result detected");
				onFinish();
			} else {
				if (status == TaskStatus.REQUESTING_ABORT) {
					this.result = null;
					this.exception = null;
					debug("Abort detected");
					onAbort();
				} else if (status == TaskStatus.REQUESTING_SUSPEND) {
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

	/**
	 * Adds a listener which will be notified of task events.
	 * Note that when second thread calls addTaskEventListener() on the same object, it will have to wait for the first thread to complete the call to this method.
	 *
	 * @param listener the listener to add
	 */
	@Override
	public void addTaskEventListener(TaskEventListener listener) {
		synchronized (this) {
			listenerList.add(TaskEventListener.class, listener);
		}
	}

	/**
	 * Removes a listener.
	 * Note that when second thread calls removeTaskEventListener() on the same object, it will have to wait for the first thread to complete the call to this method.
	 *
	 * @param listener the listener to remove
	 */
	@Override
	public void removeTaskEventListener(TaskEventListener listener) {
		synchronized (this) {
			listenerList.remove(TaskEventListener.class, listener);
		}
	}

        /**
	 * Checks if the controlling code requests the method to abort computations.
         *
         * @return true if an abortion request is posted
         */
	@Override
	public boolean isRequestingAbort() {
		return status.isRequestingAbort();
	}

        /**
	 * Checks if the controlling code requests the method to suspend computations.
         *
         * @return true if an suspention request is posted
         */
	@Override
	public boolean isRequestingSuspend() {
		return status.isRequestingSuspend();
	}

        /**
	 * Retrieves the last message set by the computation code with the
	 * {@link #setMessage(String)} method. Initially the Task has no
	 * message and null is returned.
         *
         * @return the message or null if no message has been posted
         */
	@Override
	public String getMessage() {
		return message;
	}

        /**
	 *  Posts a task message, which may be displayed by any controling application.
         *
         *  <p>Typically this method will be called from within the {@link Method#compute} method to
         *  indicate the current stage or status of the ongoing computation.
         *
	 *  <p>Note that when second thread calls setMessage() on the same object, it will have to
	 *  wait for the first thread to complete the call to this method.
         *
	 * @param message the new message
         */
	@Override
	public void setMessage(String message) {
		synchronized (this) {
			this.message = message;
			debug("Task message set to [" + message + "]");
			fireTaskMessageSet();
		}
	}

        /**
	 *  Returns the limits (maximum values) for the tickers associated with this task. For
         *  methods which aren't trackable an empty array should be returned. For trackable methods
         *  the length of the array should correspond to what is returned by
	 *  {@link TrackableMethod#getTickerCount()} for the executed method.
         *
	 *  <p>Note that when second thread calls getTickerLimits() on the same object, it will
	 *  have to wait for the first thread to complete the call to this method.
         *
	 * @return the ticker limits
         */
	@Override
	public int[] getTickerLimits() {
		synchronized (this) {
			return Arrays.copyOf(tickerLimits, tickerLimits.length);
		}
	}

        /**
	 *  Sets the limits (maximum values) for the tickers associated with this task. The method
         *  should generally throw IndexOutOfBoundsException if the method is not trackable or if
         *  the given array is longer than the ticker count for the method.
         *
	 *  <p>Note that when second thread calls setTickerLimits on the same object, it will
	 *  have to wait for the first thread to complete the call to this method.
         * @param initial the array of ticker limits
         */
	@Override
	public void setTickerLimits(int[] initial) {
		synchronized (this) {

			for (int i=0; i<initial.length; i++) {
				tickerLimits[i] = Math.max(0, initial[i]);
				tickers[i] = Math.min(tickers[i], initial[i]);
			}
			if (collectTickStatistics) {
				clearStatistics();
			}
			fireTaskTickerUpdated();

		}
	}

        /**
	 *  Sets a single ticker limit. See {@link #setTickerLimits(int[])}.
         *
	 *  <p>Note that when second thread calls setTickerLimit() on the same object, it will have to wait for the first thread to complete the call to this method.
         *
	 * @param index the index of the ticker
         * @param limit the new limit
         */
	@Override
	public void setTickerLimit(int index, int limit) {
		synchronized (this) {

			tickerLimits[index] = Math.max(0, limit);
			tickers[index] = Math.min(tickers[index], limit);
			if (collectTickStatistics) {
				clearStatistics(index);
			}
			fireTaskTickerUpdated();

		}
	}

        /**
	 *  Returns the current values for the tickers associated with this task. For methods which
         *  aren't trackable an empty array should be returned. For trackable methods the length of the array
         *  should correspond to what is returned by {@link TrackableMethod#getTickerCount()} for the executed
         *  method.
         *
	 *  <p>Note that when second thread calls getTickers() on the same object, it will have to wait for the first thread to complete the call to this method.
	 *
         * @return the ticker values
         */
	@Override
	public int[] getTickers() {
		synchronized (this) {
			return Arrays.copyOf(tickers, tickers.length);
		}
	}

        /**
	 *  Resets all ticker values to 0.
	 *
	 *  <p>Note that when second thread calls resetTickers() on the same object, it will have to wait for the first thread to complete the call to this method.
         */
	@Override
	public void resetTickers() {
		synchronized (this) {

			Arrays.fill(tickers, 0);
			if (collectTickStatistics) {
				clearStatistics();
			}
			fireTaskTickerUpdated();

		}
	}

        /**
	 *  Sets the current values for the tickers associated with this task. The method
         *  should generally throw IndexOutOfBoundsException if the method is not trackable or if
         *  the given array is longer than the ticker count for the method.
         *
	 *  <p>Note that when second thread calls setTickers() on the same object, it will have to
	 *  wait for the first thread to complete the call to this method.
	 *
         * @param tickers an array of ticker values
         */
	@Override
	public void setTickers(int[] tickers) {
		synchronized (this) {

			int oldVal;
			for (int i=0; i<tickers.length; i++) {
				oldVal = this.tickers[i];
				this.tickers[i] = Math.max(0, Math.min(tickerLimits[i], tickers[i]));
				if (collectTickStatistics) {
					addToStatistics(i, oldVal, tickers[i]);
				}
			}
			fireTaskTickerUpdated();

		}
	}

        /**
	 *  Sets a single ticker value. See {@link #setTickers(int[])}.
         *
	 * <p>Note that when second thread calls setTicker() on the same object, it will have to wait for the first thread to complete the call to this method.
         *
	 * @param index the index of the ticker
         * @param value the new value
         */
	@Override
	public void setTicker(int index, int value) {
		synchronized (this) {

			int oldVal = tickers[index];
			tickers[index] = Math.max(0, Math.min(tickerLimits[index], value));
			if (collectTickStatistics) {
				addToStatistics(index, oldVal, tickers[index]);
			}
			fireTaskTickerUpdated();

		}
	}

        /**
	 *  Advances the given ticker by one.
         *
	 *  <p>Note that when second thread calls tick() on the same object, it will have to wait for the first thread to complete the call to this method.
	 *
         * @param index the index of the ticker
         */
	@Override
	public void tick(int index) {
		synchronized (this) {

			int oldVal = tickers[index];
			if (tickers[index] < tickerLimits[index]) {
				tickers[index]++;
			}
			if (collectTickStatistics) {
				addToStatistics(index, oldVal, tickers[index]);
			}
			fireTaskTickerUpdated();

		}
	}

        /**
	 *  Advances the given ticker by the given value
         *
	 *  <p>Note that when second thread calls tick() on the same object, it will have to wait for the first thread to complete the call to this method.
	 *
         * @param index the index of the ticker
         * @param step the increase
         */
	@Override
	public void tick(int index, int step) {
		synchronized (this) {

			int oldVal = tickers[index];
			tickers[index] = Math.min(tickerLimits[index], tickers[index] + step);
			if (collectTickStatistics) {
				addToStatistics(index, oldVal, tickers[index]);
			}
			fireTaskTickerUpdated();

		}
	}

        /**
	 *  Should return the expected number of seconds until given ticker is complete. This
         *  should return <code>null</code> if the expected time is unknown or uncertain.
         *
	 *  <p>Note that when second thread calls getExpectedSecondsUntilComplete() on the same object, it will have to wait for the first thread to complete the call to this method.
	 *
         * @param index the index of the ticker
         * @return expected time in seconds or <code>null</code> when unknown.
         */
	@Override
	public Integer getExpectedSecondsUntilComplete(int index) {
		if (!collectTickStatistics) {
			return null;
		}
		synchronized (this) {
			if (statisticLengths[index] < 3) {
				return null;
			}
			int i;
			int pos = statisticPointers[index];
			long totalTime = 0;
			int totalTicks = 0;
			for (i=0; i<statisticLengths[index]; i++) {
				pos = (STATISTICS_LENGTH + pos - 1) % STATISTICS_LENGTH;
				totalTime += tickStatisticTimes[index][pos];
				totalTicks += tickStatisticTicks[index][pos];
			}
			float tps = ((float) totalTicks) / (((float) totalTime)/1000);

			int seconds = (int) Math.round(((float)(tickerLimits[index]-tickers[index])) / tps);

			// subtract elapsed
			seconds -= ((System.currentTimeMillis()-lastTickMillis[index])/1000);
			if (seconds < 0) {
				seconds = 0;
			}

			return new Integer(seconds);
		}
	}

	private void addToStatistics(int index, int oldVal, int newVal) {

		if (newVal <= oldVal) {
			/* no advance */
			return;
		}

		long millis = System.currentTimeMillis();

		if (lastTickMillis[index] == 0) {
			lastTickMillis[index] = millis;
			/* no sample this time */
			return;
		}

		long elapsed = millis - lastTickMillis[index];
		lastTickMillis[index] = millis;
		int gained = newVal - oldVal;

		tickStatisticTimes[index][statisticPointers[index]] = elapsed;
		tickStatisticTicks[index][statisticPointers[index]] = gained;

		statisticPointers[index] = (statisticPointers[index] + 1) % STATISTICS_LENGTH;
		if (statisticLengths[index] < STATISTICS_LENGTH) {
			statisticLengths[index]++;
		}

	}

	private void clearStatistics() {
		for (int i=0; i<tickerLimits.length; i++) {
			clearStatistics(i);
		}
	}

	private void clearStatistics(int index) {
		lastTickMillis[index] = 0;
		statisticLengths[index] = 0;
		statisticPointers[index] = 0;
		Arrays.fill(tickStatisticTimes[index], 0);
		Arrays.fill(tickStatisticTicks[index], 0);
	}

	/**
	 * Starts executing this Task for the first time.
	 */
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

        /**
         * Starts executing this Task after suspending it.
         */
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

        /**
         * Starts executing this Task after requesting aborting or suspense of it.
         */
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

        /**
         * Starts executing this Task after resuming it.
         */
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

        /**
         * Starts executing this Task after aborting of it
         */
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

        /**
         * Starts executing this Task after finishing it
         */
	protected void fireTaskFinished() {
		Object[] listeners = listenerList.getListenerList();
		TaskEvent taskEvent = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskEventListener.class) {
				if (taskEvent == null) {
					taskEvent = new TaskEvent(this, TaskEventType.TASK_FINISHED, getStatus(), getResult());
				}
				((TaskEventListener)listeners[i+1]).taskFinished(taskEvent);
			}
		}
	}

        /**
         * Starts executing this Task after setting message
         */
	protected void fireTaskMessageSet() {
		Object[] listeners = listenerList.getListenerList();
		TaskEvent taskEvent = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TaskEventListener.class) {
				if (taskEvent == null) {
					taskEvent = new TaskEvent(this, TaskEventType.TASK_MESSAGE_SET, getStatus(), getMessage());
				}
				((TaskEventListener)listeners[i+1]).taskMessageSet(taskEvent);
			}
		}
	}

        /**
         * Starts executing this Task after updating of ticker
         */
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
