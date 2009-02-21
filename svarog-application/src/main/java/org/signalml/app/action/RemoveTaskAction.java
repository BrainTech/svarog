/* RemoveTaskAction.java created 2007-10-19
 * 
 */
package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TaskFocusSelector;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.task.Task;
import org.signalml.task.TaskStatus;
import org.springframework.context.support.MessageSourceAccessor;

/** RemoveTaskAction
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RemoveTaskAction extends AbstractFocusableSignalMLAction<TaskFocusSelector> {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(RemoveTaskAction.class);
		
	private ApplicationTaskManager taskManager;
	
	public RemoveTaskAction(MessageSourceAccessor messageSource, TaskFocusSelector taskFocusSelector) {
		super(messageSource, taskFocusSelector);
		setText("action.removeTask");
		setIconPath("org/signalml/app/icon/removetask.png");
		setToolTip("action.removeTaskToolTip");
	}
			
	@Override
	public void actionPerformed(ActionEvent ev) {
		
		Task targetTask = getActionFocusSelector().getActiveTask();
		if( targetTask == null ) {
			return;
		}

		synchronized( targetTask ) {
			TaskStatus status = targetTask.getStatus();
			if( status.isAborted() || status.isError() || status.isFinished() ) {
				taskManager.removeTask(targetTask);
			}
		}
				
	}
	
	public void setEnabledAsNeeded() {
		boolean enabled = false;
		
		Task targetTask = getActionFocusSelector().getActiveTask();
		if( targetTask != null ) {
			TaskStatus status = targetTask.getStatus();
			if( status.isAborted() || status.isError() || status.isFinished() ) {
				enabled = true;
			}
		}
				
		setEnabled( enabled );
	}
	
	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(ApplicationTaskManager taskManager) {
		this.taskManager = taskManager;
	}
			
}
