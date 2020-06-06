/* GetTaskErrorAction.java created 2007-10-31
 *
 */
package org.signalml.app.action.workspace.tasks;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.TaskFocusSelector;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.task.Task;
import org.signalml.task.TaskResult;

/** GetTaskErrorAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class GetTaskErrorAction extends AbstractFocusableSignalMLAction<TaskFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(GetTaskErrorAction.class);

	public GetTaskErrorAction(TaskFocusSelector taskFocusSelector) {
		super(taskFocusSelector);
		setText(_("Get error"));
		setIconPath("org/signalml/app/icon/geterror.png");
		setToolTip(_("Get the errors from this task"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		Task targetTask = getActionFocusSelector().getActiveTask();
		if (targetTask == null) {
			return;
		}

		TaskResult result = null;
		synchronized (targetTask) {
			if (targetTask.getStatus().isError()) {
				result = targetTask.getResult();
			}
		}
		if (result == null) {
			logger.warn("No error to get");
			return;
		}

		Exception exception = result.getException();
		if (exception == null) {
			logger.warn("No exception to get");
			return;
		}

		Dialogs.showExceptionDialog(exception);

	}

	public void setEnabledAsNeeded() {
		boolean enabled = false;
		Task targetTask = getActionFocusSelector().getActiveTask();
		if (targetTask != null) {
			enabled = targetTask.getStatus().isError();
		}
		setEnabled(enabled);
	}

}
