/* Task.java created 2007-09-12
 *
 */
package org.signalml.task;

import org.signalml.method.Method;
import org.signalml.method.MethodExecutionTracker;

/** This interface must be implemented by classes used to control method execution.
 *
 * @see Method#compute(Object, Task)
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface Task extends MethodExecutionTracker {

	/** Returns this task's unique identifier (must be unique for all tasks of all types
	 *  AND accross VMs).
	 *
	 * @return the unique identifier.
	 */
	String getUID();

	/** Returns task information object.
	 *
	 * @return the task information object.
	 */
	TaskInfo getTaskInfo();

	/** Returns the method that is being executed by this task.
	 *
	 * @return the method
	 */
	Method getMethod();

	/** Returns task status.
	 *
	 * @return the status
	 */
	TaskStatus getStatus();

	/** Returns the result of the execution. Note that "result" here may also mean
	 *  an exception that resulted from the execution. Tasks that did not yet finish
	 *  (either normally or with exception), including those that were aborted and will
	 *  never finish do not have a result to return and will throw an exception. Use
	 *  the {@link TaskStatus#isResultAvailable()} method on the task's status to test
	 *  for result availability.
	 *
	 * @return the result descriptor
	 * @throws InvalidTaskStateException thrown when the task doesn't have a result (possibly yet).
	 */
	TaskResult getResult() throws InvalidTaskStateException;

	/** Returns the data object used as input data for the computation.
	 *
	 * @return
	 */
	Object getData();

	/** Starts or resumes the execution of the task (the computation) on the <b>current thread</b>
	 *  if the task is currently startable or resumable. The method returns only when the computation
	 *  has finished (due to being completed, aborted, suspenede or due to an exception).
	 */
	void doIfPossible();

	/** Aborts the execution of this task. The task must be running or exceptions will be thrown. Note that
	 *  "aborting" in this case menas only to post a request for abortion by setting the status to
	 *  REQUESTING_ABORT. It is up to the method's compute implementation to check for this status and exit
	 *  when it is detected.
	 *
	 *  <p>This method may also wait for the task to abort, but this may lead to the calling thread being
	 *  permanently locked if the compute method never finishes.
	 *
	 * @param wait whether to wait for the task to abort
	 * @throws InvalidTaskStateException thrown when the task status doesn't allow aborting
	 */
	void abort(boolean wait)  throws InvalidTaskStateException;

	/** Suspends the execution of this task. The task must be running and the method it runs
	 *  must be suspendalbe or exceptions will be thrown. Note that "suspending" in this case menas
	 *  only to post a request for suspension by setting the status to REQUESTING_SUSPEND.
	 *  It is up to the method's compute implementation to check for this status and exit
	 *  when it is detected.
	 *
	 *  <p>This method may also wait for the task to suspend, but this may lead to the calling thread being
	 *  permanently locked if the compute method never finishes.
	 *
	 * @param wait whether to wait for the task to suspend
	 * @throws InvalidTaskStateException thrown when the task status doesn't allow suspending or the method
	 * 		isn't suspendable
	 */
	void suspend(boolean wait) throws InvalidTaskStateException;

	/** Adds a listener which will be notified of task events.
	 *
	 * @param listener the listener to add
	 */
	void addTaskEventListener(TaskEventListener listener);

	/** Removes a listener.
	 *
	 * @param listener the listener to remove
	 */
	void removeTaskEventListener(TaskEventListener listener);

}
