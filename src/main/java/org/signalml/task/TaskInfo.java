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
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public Date getResumeTime() {
		return resumeTime;
	}
	
	public void setResumeTime(Date resumeTime) {
		this.resumeTime = resumeTime;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getSuspendTime() {
		return suspendTime;
	}
	
	public void setSuspendTime(Date suspendTime) {
		this.suspendTime = suspendTime;
	}
			
}
