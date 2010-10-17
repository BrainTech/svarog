/* IterateMethodAction.java created 2007-12-05
 *
 */
package org.signalml.app.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.method.ApplicationIterableMethodDescriptor;
import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.method.iterate.IterationSetupDescriptor;
import org.signalml.app.method.iterate.IterationSetupDialog;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.TaskStatusDialog;
import org.signalml.method.Method;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.MethodIteratorData;
import org.signalml.method.iterator.MethodIteratorMethod;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.task.LocalTask;
import org.signalml.task.Task;
import org.springframework.context.support.MessageSourceAccessor;

/** IterateMethodAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class IterateMethodAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(IterateMethodAction.class);

	private ApplicationTaskManager taskManager;
	private ApplicationMethodManager methodManager;
	private IterableMethod method;

	private IterationSetupDialog iterationSetupDialog;
	private MethodIteratorMethod iteratorMethod;

	public IterateMethodAction(MessageSourceAccessor messageSource, IterableMethod method, ApplicationMethodManager methodManager) {

		this.messageSource = messageSource;
		this.method = method;
		this.methodManager = methodManager;

		String nameCode = null;
		String iconPath = null;
		String iterationIconPath = null;

		ApplicationMethodDescriptor anyDescr = methodManager.getMethodData(method);
		ApplicationIterableMethodDescriptor descriptor = null;

		if (anyDescr instanceof ApplicationIterableMethodDescriptor) {
			descriptor = (ApplicationIterableMethodDescriptor) anyDescr;
		}

		if (descriptor != null) {
			nameCode = descriptor.getIterationNameCode();
			iterationIconPath = descriptor.getIterationIconPath();
			iconPath = descriptor.getIconPath();
		}
		if (nameCode != null && !nameCode.isEmpty()) {
			setText(nameCode);
		} else {
			setText("action.iterateMethod", new Object[] { method.getName() });
		}
		if (iterationIconPath != null && !iterationIconPath.isEmpty()) {
			setIconPath(iterationIconPath);
		} else {
			if (iconPath != null && !iconPath.isEmpty()) {
				setIconPath(iconPath);
			} else {
				setIconPath("org/signalml/app/icon/iteratemethod.png");
			}
		}

		iteratorMethod = new MethodIteratorMethod(method);

	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Iterate method [" + method.getName() + "]");

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

		MethodIteratorData iteratorData = (MethodIteratorData) iteratorMethod.createData();
		iteratorData.setSubjectMethodData(data);

		IterationSetupDescriptor iterationDescriptor = new IterationSetupDescriptor();
		iterationDescriptor.setMethod(method);
		iterationDescriptor.setData(iteratorData);

		boolean ok = iterationSetupDialog.showDialog(iterationDescriptor, true);
		if (!ok) {
			return;
		}

		Task task = new LocalTask(iteratorMethod, iteratorData, true);
		taskManager.addTask(task);

		TaskStatusDialog dialog = taskManager.getStatusDialogForTask(task);
		dialog.showDialog(true);

		taskManager.startTask(task);

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

	public MethodIteratorMethod getIteratorMethod() {
		return iteratorMethod;
	}

	public IterationSetupDialog getIterationSetupDialog() {
		return iterationSetupDialog;
	}

	public void setIterationSetupDialog(IterationSetupDialog iterationSetupDialog) {
		this.iterationSetupDialog = iterationSetupDialog;
	}

}
