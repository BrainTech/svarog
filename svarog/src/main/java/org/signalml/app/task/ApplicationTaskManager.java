/* ApplicationTaskManager.java created 2007-10-06
 *
 */

package org.signalml.app.task;

import java.awt.Window;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.signalml.SignalMLOperationMode;
import org.signalml.app.method.ApplicationMethodManager;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.app.view.common.dialogs.TaskStatusDialog;
import org.signalml.method.CleanupMethod;
import org.signalml.method.Method;
import org.signalml.task.DefaultTaskManager;
import org.signalml.task.Task;

/** ApplicationTaskManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ApplicationTaskManager extends DefaultTaskManager {

	protected static final Logger logger = Logger.getLogger(ApplicationTaskManager.class);

	private Map<Task,ApplicationTaskWorker> workerMap = new HashMap<>();
	private Map<Task,TaskStatusDialog> dialogMap = new HashMap<>();
	private Map<Task,TaskEventProxy> proxyMap = new HashMap<>();
	private ApplicationMethodManager methodManager;

	private PleaseWaitDialog pleaseWaitDialog;

	private Window statusDialogParent;

	private SignalMLOperationMode mode;

	public ApplicationTaskManager() {
		super();
	}

	protected ApplicationTaskWorker getWorkerForTask(Task task) {
		return workerMap.get(task);
	}

	public TaskEventProxy getEventProxyForTask(Task task) {
		return proxyMap.get(task);
	}

	public TaskStatusDialog getStatusDialogForTask(Task task) {
		TaskStatusDialog dialog = dialogMap.get(task);
		if (dialog == null) {
			dialog = createStatusDialog(task);
			dialogMap.put(task, dialog);
		}
		return dialog;
	}

	protected TaskStatusDialog createStatusDialog(Task task) {

		TaskStatusDialog dialog = new TaskStatusDialog(task, mode);
		dialog.setTaskManager(this);
		dialog.setMethodManager(methodManager);

		return dialog;

	}

	@Override
	public void addTask(Task task) {
		TaskEventProxy proxy = new TaskEventProxy();
		proxyMap.put(task, proxy);

		super.addTask(task);
	}

	public void startTask(Task task) {
		ApplicationTaskWorker worker = new ApplicationTaskWorker(task);
		workerMap.put(task, worker);

		TaskEventProxy proxy = proxyMap.get(task);
		proxy.setWorker(worker);

		worker.execute();
	}

	public void resumeTask(Task task) {
		ApplicationTaskWorker worker = new ApplicationTaskWorker(task);
		workerMap.put(task, worker);

		TaskEventProxy proxy = proxyMap.get(task);
		proxy.setWorker(worker);

		worker.execute();
	}

	@Override
	public void removeTask(Task task) {
		synchronized (this) {
			super.removeTask(task);

			synchronized (task) {
				if (task.getStatus().isAbortable()) {
					task.abort(false);
				}
			}

			ApplicationTaskWorker worker = workerMap.get(task);
			if (worker != null) {
				worker.cancel(true);
				workerMap.remove(task);
			}

			TaskEventProxy proxy = proxyMap.get(task);
			if (proxy != null) {
				proxy.setWorker(null);
				proxyMap.remove(task);
			}

			TaskStatusDialog dialog = dialogMap.get(task);
			if (dialog != null) {
				if (dialog.isVisible()) {
					dialog.hideDialog();
				}
				dialog.dispose();

				dialogMap.remove(task);
			}

			Method method = task.getMethod();
			if (method instanceof CleanupMethod) {
				((CleanupMethod) method).cleanUp(task.getData());
			}

		}
	}

	public void waitForTaskToStopWorking(Task task) {

		ApplicationTaskWorker worker = workerMap.get(task);
		if (worker == null) {
			return;
		}

		if (worker.isDone()) {
			return;
		}

		pleaseWaitDialog.setActivity(_("waiting for task"));

		pleaseWaitDialog.configureForIndeterminateSimulated();
		worker.setPleaseWaitDialog(pleaseWaitDialog);
		pleaseWaitDialog.waitAndShowDialogIn(statusDialogParent, 500, worker);

	}

	public SignalMLOperationMode getMode() {
		return mode;
	}

	public void setMode(SignalMLOperationMode mode) {
		this.mode = mode;
	}

	public ApplicationMethodManager getMethodManager() {
		return methodManager;
	}

	public void setMethodManager(ApplicationMethodManager methodManager) {
		this.methodManager = methodManager;
	}

	public Window getStatusDialogParent() {
		return statusDialogParent;
	}

	public void setStatusDialogParent(Window statusDialogParent) {
		this.statusDialogParent = statusDialogParent;
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		return pleaseWaitDialog;
	}

	public void setPleaseWaitDialog(PleaseWaitDialog pleaseWaitDialog) {
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

}
