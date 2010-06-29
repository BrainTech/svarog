/* TaskResult.java created 2007-09-12
 *
 */
package org.signalml.task;

import java.io.Serializable;

import org.signalml.method.Method;

/** This is a simple wrapper class for the result of a task. This encompases basic task information
 *  as well as the result of the {@link Method#compute(Object, Task)} method or the exception
 *  it threw.
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String taskUID;

	private TaskStatus taskStatus;
	private TaskInfo taskInfo;

	private Object result;
	private Exception exception;

	/** Constructs a new result.
	 *
	 * @param taskUID the UID of the task
	 * @param taskInfo the task information
	 * @param taskStatus the current status of the task
	 * @param result the result or null if no result
	 * @param exception the exception or null if no exception
	 */
	public TaskResult(String taskUID, TaskInfo taskInfo, TaskStatus taskStatus, Object result, Exception exception) {
		this.taskUID = taskUID;
		this.taskInfo = taskInfo;
		this.taskStatus = taskStatus;
		this.result = result;
		this.exception = exception;
	}

	/** Returns the exception thrown by the {@link Method#compute(Object, Task)} method.
	 *
	 * @return the exception or null if no exception
	 */
	public Exception getException() {
		return exception;
	}

	/** Returns the task information.
	 *
	 * @return the information
	 */
	public TaskInfo getTaskInfo() {
		return taskInfo;
	}

	/** Returns the result returned by the {@link Method#compute(Object, Task)} method.
	 *
	 * @return the result or null if the compute method returned null.
	 */
	public Object getResult() {
		return result;
	}

	/** Returns task status (if the result could be obtained the status should be either FINISHED
	 *  or ERROR).
	 *
	 * @return the status
	 */
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	/** Returns the UID of the task.
	 *
	 * @return the uid
	 */
	public String getTaskUID() {
		return taskUID;
	}

}
