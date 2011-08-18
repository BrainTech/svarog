package org.signalml.plugin.method;

import java.awt.Window;

import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.method.TrackableMethod;
import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.data.PluginConfigMethodData;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.method.SvarogAccessMethod;
import org.signalml.plugin.export.method.SvarogMethodConfigurer;
import org.signalml.plugin.export.method.SvarogMethodDescriptor;
import org.signalml.plugin.export.method.SvarogTask;
import org.signalml.plugin.export.method.SvarogTaskStatusDialog;
import org.signalml.plugin.i18n.PluginMessageSourceManager;
import org.signalml.task.LocalTask;
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

		SvarogAccessMethod methodManager = this.svarogAccess.getMethodAccess();
		methodManager.registerMethod(method);
		methodManager.setMethodDescriptor(method, methodDescriptor);
	}

	public void runMethod() {
		SvarogAccessMethod methodManager = this.svarogAccess.getMethodAccess();
		SvarogMethodDescriptor descriptor = methodManager.getMethodDescriptor(this.method);
		SvarogMethodConfigurer configurer = null;
		Object data = null;

		if (descriptor != null) {
			configurer = methodManager.getConfigurer(descriptor);
			data = methodManager.createData(descriptor);
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

		SvarogTask task = new LocalTask(this.method, data,
					  (method instanceof TrackableMethod));

		SvarogAccessMethod svarogMethods = this.svarogAccess.getMethodAccess();
		svarogMethods.setTaskMessageSource(task, source);
		svarogMethods.addTask(task);
		svarogMethods.startTask(task);

		SvarogTaskStatusDialog dialog = svarogMethods.getTaskStatusDialog(task);
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
