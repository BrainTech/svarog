/* ShowTaskDialogAction.java created 2007-10-19
 *
 */
package org.signalml.app.action.workspace.tasks;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.TaskFocusSelector;
import org.signalml.app.task.ApplicationTaskManager;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.TaskStatusDialog;
import org.signalml.task.Task;

/** ShowTaskDialogAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ShowTaskDialogAction extends AbstractFocusableSignalMLAction<TaskFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ShowTaskDialogAction.class);

	private ApplicationTaskManager taskManager;

	public ShowTaskDialogAction(TaskFocusSelector taskFocusSelector) {
		super(taskFocusSelector);
		setText(_("Show dialog"));
		setIconPath("org/signalml/app/icon/running.png");
		setToolTip(_("Show the status dialog"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		Task targetTask = getActionFocusSelector().getActiveTask();
		if (targetTask == null) {
			return;
		}

		TaskStatusDialog dialog = taskManager.getStatusDialogForTask(targetTask);
		dialog.showDialog(true);

	}

	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveTask() != null);
	}

	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(ApplicationTaskManager taskManager) {
		this.taskManager = taskManager;
	}

}
