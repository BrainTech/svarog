package org.signalml.plugin.newartifact.method;

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
import org.signalml.plugin.method.PluginAbstractMethodDescriptor;
import org.signalml.plugin.newartifact.data.NewArtifactApplicationData;
import org.signalml.plugin.newartifact.data.NewArtifactParameters;
import org.signalml.plugin.tool.PluginResourceRepository;
import org.signalml.plugin.newartifact.NewArtifactPlugin;

import com.thoughtworks.xstream.XStream;

public class NewArtifactMethodDescriptor extends PluginAbstractMethodDescriptor implements
	ApplicationIterableMethodDescriptor {

	protected static final Logger logger = Logger
					       .getLogger(NewArtifactMethodDescriptor.class);

	private NewArtifactMethodConfigurer configurer;
	private MethodPresetManager presetManager;

	private NewArtifactMethodConsumer consumer;

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
		return NewArtifactPlugin.iconPath;
	}

	@Override
	public Object createData(ApplicationMethodManager methodManager) {
		ExportedSignalDocument signalDocument;
		try {
			signalDocument = this.methodManager.getSvarogAccess()
					 .getSignalAccess().getActiveSignalDocument();
		} catch (NoActiveObjectException e) {
			signalDocument = null;
		}

		if (signalDocument == null) {
			OptionPane.showNoActiveSignal(methodManager.getDialogParent());
			return null;
		}

		NewArtifactApplicationData data = new NewArtifactApplicationData();
		data.setSignalDocument(signalDocument);

		return data;
	}

	@Override
	public MethodConfigurer getConfigurer(ApplicationMethodManager methodManager) {
		if (configurer == null) {
			configurer = new NewArtifactMethodConfigurer();
			configurer.setPresetManager(getPresetManager(methodManager, false));
			configurer.initialize(this.methodManager);
		}
		return configurer;
	}

	@Override
	public MethodResultConsumer getConsumer(
		ApplicationMethodManager methodManager) {
		if (consumer == null) {
			consumer = new NewArtifactMethodConsumer();
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
								NewArtifactParameters.class);
			presetManager.setProfileDir(methodManager.getProfileDir());
			try {
				presetManager.setStreamer((XStream) PluginResourceRepository
							  .GetResource("streamer", NewArtifactPlugin.class));
			} catch (PluginException e) {
				logger.error("Can't get proper streamer", e);
				return presetManager;
			}
			try {
				presetManager.readFromPersistence(null);
			} catch (IOException e) {
				if (e instanceof FileNotFoundException) {
					logger.debug("Seems like artifact preset configuration doesn't exist");
				} else {
					logger.error(
						"Failed to read artifact presets - presets lost", e);
				}
			}
		}
		return presetManager;
	}

}
