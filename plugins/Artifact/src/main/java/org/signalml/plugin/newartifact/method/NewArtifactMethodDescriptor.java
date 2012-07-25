package org.signalml.plugin.newartifact.method;

import org.apache.log4j.Logger;
import org.signalml.app.method.ApplicationIterableMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.method.MethodIterationResultConsumer;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.MethodResultConsumer;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.method.Method;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.method.BaseMethodData;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.method.PluginAbstractMethodDescriptor;
import org.signalml.plugin.method.helper.PluginPresetManagerHelper;
import org.signalml.plugin.newartifact.NewArtifactPlugin;
import org.signalml.plugin.newartifact.data.NewArtifactApplicationData;
import org.signalml.plugin.newartifact.data.NewArtifactParameters;

public class NewArtifactMethodDescriptor extends PluginAbstractMethodDescriptor
		implements ApplicationIterableMethodDescriptor {

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
	public BaseMethodData createData(ApplicationMethodManager methodManager) {
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

		NewArtifactApplicationData data = new NewArtifactApplicationData();
		data.setSignalAccess(signalAccess);
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
		return "FIXME"; // FIXME
	}

	@Override
	public MethodPresetManager getPresetManager(
			ApplicationMethodManager methodManager, boolean existingOnly) {
		if (presetManager == null && !existingOnly) {
			presetManager = PluginPresetManagerHelper.GetPresetForMethod(
					methodManager, this.methodManager, this.getMethod()
							.getName(), NewArtifactParameters.class);
		}
		return presetManager;
	}

}
