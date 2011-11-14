/* AbortTaskAction.java created 2007-10-19
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TaskFocusSelector;
import org.signalml.task.Task;

/** AbortTaskAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AbortTaskAction extends AbstractFocusableSignalMLAction<TaskFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(AbortTaskAction.class);

	public AbortTaskAction( TaskFocusSelector taskFocusSelector) {
		super( taskFocusSelector);
		setText(_("Abort"));
		setIconPath("org/signalml/app/icon/abort.png");
		setToolTip(_("Abort this task (the task might not terminate immediately)"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		Task targetTask = getActionFocusSelector().getActiveTask();
		if (targetTask == null) {
			return;
		}

		synchronized (targetTask) {
			if (targetTask.getStatus().isAbortable()) {
				targetTask.abort(false);
			}
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		boolean enabled = false;
		TaskFocusSelector x = getActionFocusSelector();
		if (null != x) {
			Task targetTask = x.getActiveTask();
			if (targetTask != null) {
				enabled = targetTask.getStatus().isAbortable();
			}
		}
		setEnabled(enabled);
	}

}
