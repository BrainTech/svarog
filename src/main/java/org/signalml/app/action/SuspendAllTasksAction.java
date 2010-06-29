/* SuspendAllTasksAction.java created 2008-02-07
 *
 */
package org.signalml.app.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.method.SuspendableMethod;
import org.signalml.task.Task;
import org.springframework.context.support.MessageSourceAccessor;

/** SuspendAllTasksAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SuspendAllTasksAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SuspendAllTasksAction.class);

	private ApplicationTaskManager taskManager;
	private Component optionPaneParent;

	public SuspendAllTasksAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.suspendAllTasks");
		setIconPath("org/signalml/app/icon/suspendall.png");
		setToolTip("action.suspendAllTasksToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		int ans = OptionPane.showSuspendAllTasks(optionPaneParent);
		if (ans != OptionPane.OK_OPTION) {
			return;
		}

		synchronized (taskManager) {

			int count = taskManager.getTaskCount();
			Task task;

			for (int i=0; i<count; i++) {
				task = taskManager.getTaskAt(i);
				synchronized (task) {
					if ((task.getMethod() instanceof SuspendableMethod) && task.getStatus().isSuspendable()) {
						task.suspend(false);
					}
				}
			}
		}

	}

	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	public Component getOptionPaneParent() {
		return optionPaneParent;
	}

	public void setOptionPaneParent(Component optionPaneParent) {
		this.optionPaneParent = optionPaneParent;
	}

	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(ApplicationTaskManager taskManager) {
		this.taskManager = taskManager;
	}

}
