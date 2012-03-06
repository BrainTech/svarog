package org.signalml.plugin.newstager.method;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.method.ApplicationIterableMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.method.MethodIterationResultConsumer;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.MethodResultConsumer;
import org.signalml.app.view.components.dialogs.OptionPane;
import org.signalml.method.Method;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.method.PluginAbstractMethodDescriptor;
import org.signalml.plugin.newstager.NewStagerPlugin;
import org.signalml.plugin.newstager.data.NewStagerApplicationData;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.tool.PluginResourceRepository;

import com.thoughtworks.xstream.XStream;

public class NewStagerMethodDescriptor extends PluginAbstractMethodDescriptor
		implements ApplicationIterableMethodDescriptor {

	protected static final Logger logger = Logger
			.getLogger(NewStagerMethodDescriptor.class);

	private NewStagerMethodConfigurer configurer;
	private MethodPresetManager presetManager;

	private NewStagerMethodConsumer consumer;

	@Override
	public MethodIterationResultConsumer getIterationConsumer(
			ApplicationMethodManager methodManager) {
		return null;
	}

	@Override
	public String getIterationIconPath() {
		return null;
	}

	@Override
	public String getIterationName() {
		return null;
	}

	@Override
	public String getIconPath() {
		return NewStagerPlugin.iconPath;
	}

	@Override
	public Object createData(ApplicationMethodManager methodManager) {
		SvarogAccessSignal signalAccess = this.methodManager.getSvarogAccess()
				.getSignalAccess();

		ExportedSignalDocument signalDocument;
		try {
			signalDocument = signalAccess.getActiveSignalDocument();
		} catch (NoActiveObjectException e) {
			signalDocument = null;
		}

		if (signalDocument == null) {
			OptionPane.showNoActiveSignal(methodManager.getDialogParent());
			return null;
		}

		NewStagerApplicationData data = new NewStagerApplicationData();
		data.setSignalAccess(signalAccess);
		data.setSignalDocument(signalDocument);

		// ConfigurationDefaults.setStagerParameters(data.getParameters());
		// //FIXME: what's this?

		return data;
	}

	@Override
	public MethodConfigurer getConfigurer(ApplicationMethodManager methodManager) {
		if (configurer == null) {
			configurer = new NewStagerMethodConfigurer();
			configurer.setPresetManager(getPresetManager(methodManager, false));
			configurer.initialize(this.methodManager);
		}
		return configurer;
	}

	@Override
	public MethodResultConsumer getConsumer(
			ApplicationMethodManager methodManager) {
		if (consumer == null) {
			consumer = new NewStagerMethodConsumer();
			consumer.initialize(this.methodManager);
		}
		return consumer;
	}

	@Override
	public Method getMethod() {
		return this.methodManager.getMethodConfig().getMethod();
	}

	@Override
	public String getName() {
		return "xxx";
	}

	@Override
	public MethodPresetManager getPresetManager(
			ApplicationMethodManager methodManager, boolean existingOnly) {
		if (presetManager == null && !existingOnly) {
			presetManager = new MethodPresetManager(this.getMethod().getName(),
					NewStagerParameters.class);
			presetManager.setProfileDir(methodManager.getProfileDir());
			try {
				presetManager.setStreamer((XStream) PluginResourceRepository
						.GetResource("streamer", NewStagerPlugin.class));
			} catch (PluginException e) {
				logger.error("Can't get proper streamer", e);
				return presetManager;
			}
			try {
				presetManager.readFromPersistence(null);
			} catch (IOException ex) {
				if (ex instanceof FileNotFoundException) {
					logger.debug("Seems like stager preset configuration doesn't exist");
				} else {
					logger.error(
							"Failed to read stager presets - presets lost", ex);
				}
			}
		}
		return presetManager;
	}

}
