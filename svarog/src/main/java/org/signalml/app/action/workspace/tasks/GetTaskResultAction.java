/* GetTaskResultAction.java created 2007-10-19
 *
 */
package org.signalml.app.action.workspace.tasks;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.TaskFocusSelector;
import org.signalml.app.method.ApplicationIterableMethodDescriptor;
import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodIterationResultConsumer;
import org.signalml.app.method.MethodResultConsumer;
import org.signalml.app.view.components.dialogs.DialogResultListener;
import org.signalml.app.view.components.dialogs.ErrorsDialog;
import org.signalml.method.Method;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.MethodIteratorData;
import org.signalml.method.iterator.MethodIteratorMethod;
import org.signalml.method.iterator.MethodIteratorResult;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.task.Task;
import org.signalml.task.TaskResult;

/** GetTaskResultAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class GetTaskResultAction extends AbstractFocusableSignalMLAction<TaskFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(GetTaskResultAction.class);

	private ApplicationMethodManager methodManager;
	private DialogResultListener dialogResultListener;



	public GetTaskResultAction(
	                           TaskFocusSelector actionFocusSelector,
	                           DialogResultListener dialogResultListener) {
		this(actionFocusSelector);
		this.dialogResultListener = dialogResultListener;
	}

	public GetTaskResultAction(TaskFocusSelector taskFocusSelector) {
		super(taskFocusSelector);
		setText(_("Get result"));
		setIconPath("org/signalml/app/icon/getresult.png");
		setToolTip(_("Get the results of this task"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		Task targetTask = getActionFocusSelector().getActiveTask();
		if (targetTask == null) {
			return;
		}

		TaskResult result = null;
		synchronized (targetTask) {
			if (targetTask.getStatus().isFinished()) {
				result = targetTask.getResult();
			}
		}
		if (result == null) {
			logger.warn("No result to get");
			return;
		}

		logger.debug("Got result [" + result + "]");

		Method method = targetTask.getMethod();

		if (method instanceof MethodIteratorMethod) {

			MethodIteratorMethod iteratorMethod = (MethodIteratorMethod) method;
			IterableMethod subjectMethod = iteratorMethod.getSubjectMethod();

			// special treatment of the iterator, use an iterable result consumer of the subject method
			ApplicationMethodDescriptor anyDescr = methodManager.getMethodData(subjectMethod);
			ApplicationIterableMethodDescriptor descriptor = null;
			if (anyDescr instanceof ApplicationIterableMethodDescriptor) {
				descriptor = (ApplicationIterableMethodDescriptor) anyDescr;
			}
			if (descriptor == null) {
				logger.warn("No descriptor, can't get consumer");
				return;
			}

			MethodIterationResultConsumer consumer = descriptor.getIterationConsumer(methodManager);
			if (consumer == null) {
				logger.warn("No consumer");
				return;
			}

			MethodIteratorData iteratorData = (MethodIteratorData) targetTask.getData();
			MethodIteratorResult iteratorResult = (MethodIteratorResult) result.getResult();

			try {
				consumer.consumeIterationResult(subjectMethod, iteratorData, iteratorResult);
			} catch (SignalMLException ex) {
				logger.error("Failed to consume result", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return;
			}

		} else {
			// normal case - use normal consumer

			ApplicationMethodDescriptor descriptor = methodManager.getMethodData(method);
			if (descriptor == null) {
				logger.warn("No descriptor, can't get consumer");
				return;
			}

			MethodResultConsumer consumer = descriptor.getConsumer(methodManager);
			if (consumer == null) {
				logger.warn("No consumer");
				return;
			}

			boolean consumerResult = false;

			try {
				consumerResult = consumer.consumeResult(method, targetTask.getData(), result.getResult());
			} catch (SignalMLException ex) {
				logger.error("Failed to consume result", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return;
			}

			if (dialogResultListener != null) {
				dialogResultListener.dialogCompleted(consumerResult);
			}

		}

	}

	@Override
	public void setEnabledAsNeeded() {
		boolean enabled = false;
		Task targetTask = getActionFocusSelector().getActiveTask();
		if (targetTask != null) {
			enabled = targetTask.getStatus().isFinished();
		}
		setEnabled(enabled);
	}

	public ApplicationMethodManager getMethodManager() {
		return methodManager;
	}

	public void setMethodManager(ApplicationMethodManager methodManager) {
		this.methodManager = methodManager;
	}

}
