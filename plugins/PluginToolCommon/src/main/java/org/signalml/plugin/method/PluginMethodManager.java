package org.signalml.plugin.method;

import java.awt.Window;

import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.TaskStatusDialog;
import org.signalml.method.TrackableMethod;
import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.data.PluginConfigMethodData;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.i18n.PluginMessageSourceManager;
import org.signalml.task.LocalTask;
import org.signalml.task.Task;
import org.springframework.context.support.MessageSourceAccessor;

public class PluginMethodManager {

	private SvarogAccess svarogAccess;
	private PluginConfigMethodData methodConfig;
	private PluginAbstractMethod method;

	public PluginMethodManager(SvarogAccess svarogAccess,
				   PluginConfigForMethod config) {
		this.svarogAccess = svarogAccess;

		this.methodConfig = config.getMethodConfig();

		this.method = methodConfig.getMethod();

		PluginAbstractMethodDescriptor methodDescriptor = methodConfig
				.getMethodDescriptor();
		methodDescriptor.setPluginMethodManager(this);

		ApplicationMethodManager methodManager = this.svarogAccess
				.getGUIAccess().getManager().getMethodManager();
		methodManager.registerMethod(method);
		methodManager.setMethodData(method, methodDescriptor);
	}

	public void runMethod() {

		ApplicationMethodManager methodManager = this.svarogAccess
				.getGUIAccess().getManager().getMethodManager();
		ApplicationMethodDescriptor descriptor = methodManager
				.getMethodData(this.method);
		MethodConfigurer configurer = null;
		Object data = null;

		if (descriptor != null) {
			configurer = descriptor.getConfigurer(methodManager);
			data = descriptor.createData(methodManager);
			if (data == null) {
				return;
			}
		}

		if (configurer != null) {
			try {
				boolean configurationOk = configurer.configure(this.method,
							  data);
				if (!configurationOk) {
					return;
				}
			} catch (SignalMLException ex) {
				this.handleException(ex);
				return;
			}
		}

		MessageSourceAccessor source;
		try {
			source = PluginMessageSourceManager.GetMessageSource();
		} catch (PluginException e) {
			this.handleException(e);
			return;
		}

		Task task = new LocalTask(this.method, data,
					  (method instanceof TrackableMethod));

		ViewerElementManager viewerManager = this.svarogAccess.getGUIAccess()
						     .getManager();
		viewerManager.getTaskTableModel().setMessageSourceForTask(task, source);
		ApplicationTaskManager taskManager = viewerManager.getTaskManager();

		taskManager.addTask(task);
		taskManager.startTask(task);

		TaskStatusDialog dialog = taskManager.getStatusDialogForTask(task);

		dialog.setMessageSource(source);
		dialog.showDialog(true);
	}

	public SvarogAccess getSvarogAccess() {
		return this.svarogAccess;
	}

	public PluginConfigMethodData getMethodConfig() {
		return this.methodConfig;
	}

	public void handleException(SignalMLException ex) {
		ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
	}

}
