/* RunMethodAction.java created 2007-10-06
 *
 */
package org.signalml.app.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.TaskStatusDialog;
import org.signalml.method.Method;
import org.signalml.method.TrackableMethod;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.task.LocalTask;
import org.signalml.task.Task;

/** RunMethodAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RunMethodAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RunMethodAction.class);

	private ApplicationTaskManager taskManager;
	private ApplicationMethodManager methodManager;
	private Method method;

	public  RunMethodAction( Method method, ApplicationMethodManager methodManager) {
		this.method = method;
		this.methodManager = methodManager;
		String nameCode = null;
		String iconPath = null;
		ApplicationMethodDescriptor descriptor = methodManager.getMethodData(method);
		if (descriptor != null) {
			nameCode = descriptor.getNameCode();
			iconPath = descriptor.getIconPath();
		}
		if (nameCode != null && !nameCode.isEmpty()) {
			setText(nameCode);
		} else {
			setText("action.runMethod");
		}
		if (iconPath != null && !iconPath.isEmpty()) {
			setIconPath(iconPath);
		} else {
			setIconPath("org/signalml/app/icon/runmethod.png");
		}
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Run method [" + method.getName() + "]");

		ApplicationMethodDescriptor descriptor = methodManager.getMethodData(method);
		MethodConfigurer configurer = null;
		Object data = null;

		if (descriptor != null) {
			configurer = descriptor.getConfigurer(methodManager);
			data = descriptor.createData(methodManager);
			if (data == null) {
				logger.debug("Data creation aborted");
				return;
			}
		}

		if (data == null) {
			data = method.createData();
		}

		if (configurer != null) {
			try {
				boolean configurationOk = configurer.configure(method, data);
				if (!configurationOk) {
					return;
				}
			} catch (SignalMLException ex) {
				logger.error("Failed to configure method", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return;
			}
		}

		Task task = new LocalTask(method, data, (method instanceof TrackableMethod));
		taskManager.addTask(task);

		taskManager.startTask(task);

		TaskStatusDialog dialog = taskManager.getStatusDialogForTask(task);
		dialog.showDialog(true);

	}

	public Method getMethod() {
		return method;
	}

	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(ApplicationTaskManager taskManager) {
		this.taskManager = taskManager;
	}

	public ApplicationMethodManager getMethodManager() {
		return methodManager;
	}


}
