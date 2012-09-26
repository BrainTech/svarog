/* TaskInfo.java created 2007-09-12
 *
 */
package org.signalml.task;

import java.io.Serializable;
import java.util.Date;

/** This is a simple wrapper class for several simple attributes of a task.
 *
 * @see Task#getTaskInfo()
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date createTime;

	private Date startTime;
	private Date endTime;

	private Date suspendTime;
	private Date resumeTime;

	/**
	 * Returns Date of creation of this Task
	 * @return Date of creation of this Task
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * Sets time of creation of this Task
	 * @param createTime Date to set as creation time
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * Returns Date of end of this Task
	 * @return Date of end of this Task
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * Sets time of end of this Task
	 * @param endTime Date to set as end time
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * Returns Date of resumption of this Task
	 * @return Date of resumption of this Task
	 */
	public Date getResumeTime() {
		return resumeTime;
	}

	/**
	 * Sets time of resumption of this Task
	 * @param resumeTime Date to set as resumption time
	 */
	public void setResumeTime(Date resumeTime) {
		this.resumeTime = resumeTime;
	}

	/**
	 * Returns Date of start of this Task
	 * @return Date of start of this Task
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Sets time of start of this Task
	 * @param startTime Date to set as start time
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * Returns Date of suspension of this Task
	 * @return Date of suspension of this Task
	 */
	public Date getSuspendTime() {
		return suspendTime;
	}

	/**
	 * Sets time of suspension of this Task
	 * @param suspendTime Date to set as suspension time
	 */
	public void setSuspendTime(Date suspendTime) {
		this.suspendTime = suspendTime;
	}

}
