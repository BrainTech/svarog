/* ApplicationTaskDescriptor.java created 2008-02-15
 * 
 */

package org.signalml.app.task;

import java.io.Serializable;

import org.signalml.task.TaskStatus;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** ApplicationTaskDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("task")
public class ApplicationTaskDescriptor implements Serializable {

	private static final long serialVersionUID = 1L;

	private String methodUID;
	private TaskStatus status;
	private String serializationPath;
	
	public String getMethodUID() {
		return methodUID;
	}
	
	public void setMethodUID(String methodUID) {
		this.methodUID = methodUID;
	}
	
	public TaskStatus getStatus() {
		return status;
	}
	
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	
	public String getSerializationPath() {
		return serializationPath;
	}
	
	public void setSerializationPath(String serializationPath) {
		this.serializationPath = serializationPath;
	}	
	
}
