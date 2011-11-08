/* SuspendTaskAction.java created 2007-10-19
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TaskFocusSelector;
import org.signalml.task.Task;

/** SuspendTaskAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SuspendTaskAction extends AbstractFocusableSignalMLAction<TaskFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SuspendTaskAction.class);

	public  SuspendTaskAction( TaskFocusSelector taskFocusSelector) {
		super( taskFocusSelector);
		setText(_("Suspend"));
		setIconPath("org/signalml/app/icon/suspend.png");
		setToolTip(_("Suspend this task (the task might not suspend immediately)"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		Task targetTask = getActionFocusSelector().getActiveTask();
		if (targetTask == null) {
			return;
		}

		synchronized (targetTask) {
			if (targetTask.getStatus().isSuspendable()) {
				targetTask.suspend(false);
			}
		}

	}

	public void setEnabledAsNeeded() {
		boolean enabled = false;
		Task targetTask = getActionFocusSelector().getActiveTask();
		if (targetTask != null) {
			enabled = targetTask.getStatus().isSuspendable();
		}
		setEnabled(enabled);
	}

}
