/* SuspendTaskAction.java created 2007-10-19
 * 
 */
package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TaskFocusSelector;
import org.signalml.task.Task;
import org.springframework.context.support.MessageSourceAccessor;

/** SuspendTaskAction
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SuspendTaskAction extends AbstractFocusableSignalMLAction<TaskFocusSelector> {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(SuspendTaskAction.class);
			
	public SuspendTaskAction(MessageSourceAccessor messageSource, TaskFocusSelector taskFocusSelector) {
		super(messageSource, taskFocusSelector);
		setText("action.suspendTask");
		setIconPath("org/signalml/app/icon/suspend.png");
		setToolTip("action.suspendTaskToolTip");
	}
			
	@Override
	public void actionPerformed(ActionEvent ev) {
		
		Task targetTask = getActionFocusSelector().getActiveTask();
		if( targetTask == null ) {
			return;
		}
		
		synchronized( targetTask ) {
			if( targetTask.getStatus().isSuspendable() ) {
				targetTask.suspend(false);
			}
		}
		
	}
	
	public void setEnabledAsNeeded() {
		boolean enabled = false;
		Task targetTask = getActionFocusSelector().getActiveTask();		
		if( targetTask != null ) {
			enabled = targetTask.getStatus().isSuspendable();
		}
		setEnabled(enabled);
	}
			
}
