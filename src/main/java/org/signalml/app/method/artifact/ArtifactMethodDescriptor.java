/* ArtifactMethodDescriptor.java created 2007-11-02
 *
 */

package org.signalml.app.method.artifact;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.method.ApplicationIterableMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.method.MethodIterationResultConsumer;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.MethodResultConsumer;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.method.artifact.ArtifactMethod;
import org.signalml.method.artifact.ArtifactParameters;
import org.signalml.plugin.export.signal.Document;

/** ArtifactMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactMethodDescriptor implements ApplicationIterableMethodDescriptor {

	protected static final Logger logger = Logger.getLogger(ArtifactMethodDescriptor.class);

	public static final String ICON_PATH = "org/signalml/app/icon/runmethod.png";
	public static final String ITERATION_ICON_PATH = "org/signalml/app/icon/iteratemethod.png";
	public static final String RUN_METHOD_STRING = "artifactMethod.runMethodString";

	private ArtifactMethod method;
	private ArtifactMethodConfigurer configurer;
	private ArtifactMethodConsumer consumer;
	private ArtifactMethodIterationConsumer iterationConsumer;
	private MethodPresetManager presetManager;

	public ArtifactMethodDescriptor(ArtifactMethod method) {
		this.method = method;
	}

	@Override
	public ArtifactMethod getMethod() {
		return method;
	}

	@Override
	public String getNameCode() {
		return RUN_METHOD_STRING;
	}

	@Override
	public String getIterationNameCode() {
		return "artifactMethod.iterateMethodString";
	}

	@Override
	public String getIconPath() {
		return ICON_PATH;
	}

	@Override
	public String getIterationIconPath() {
		return ITERATION_ICON_PATH;
	}

	@Override
	public MethodPresetManager getPresetManager(ApplicationMethodManager methodManager, boolean existingOnly) {
		if (presetManager == null && !existingOnly) {
			presetManager = new MethodPresetManager(method.getName(), ArtifactParameters.class);
			presetManager.setProfileDir(methodManager.getProfileDir());
			presetManager.setStreamer(methodManager.getStreamer());
			try {
				presetManager.readFromPersistence(null);
			} catch (IOException ex) {
				if (ex instanceof FileNotFoundException) {
					logger.debug("Seems like artifact preset configuration doesn't exist");
				} else {
					logger.error("Failed to read artifact presets - presets lost", ex);
				}
			}
		}
		return presetManager;
	}

	@Override
	public MethodConfigurer getConfigurer(ApplicationMethodManager methodManager) {
		if (configurer == null) {
			configurer = new ArtifactMethodConfigurer();
			configurer.setPresetManager(getPresetManager(methodManager, false));
			configurer.initialize(methodManager);
		}
		return configurer;
	}

	@Override
	public MethodResultConsumer getConsumer(ApplicationMethodManager methodManager) {
		if (consumer == null) {
			consumer = new ArtifactMethodConsumer();
			consumer.initialize(methodManager);
		}
		return consumer;
	}

	@Override
	public MethodIterationResultConsumer getIterationConsumer(ApplicationMethodManager methodManager) {
		if (iterationConsumer == null) {
			iterationConsumer = new ArtifactMethodIterationConsumer();
			iterationConsumer.initialize(methodManager);
		}
		return iterationConsumer;
	}

	@Override
	public Object createData(ApplicationMethodManager methodManager) {

		Document document = methodManager.getActionFocusManager().getActiveDocument();
		if (!(document instanceof SignalDocument)) {
			OptionPane.showNoActiveSignal(methodManager.getDialogParent());
			return null;
		}
		SignalDocument signalDocument = (SignalDocument) document;

		ArtifactApplicationData data = new ArtifactApplicationData();
		data.setSignalDocument(signalDocument);

		return data;

	}

}
