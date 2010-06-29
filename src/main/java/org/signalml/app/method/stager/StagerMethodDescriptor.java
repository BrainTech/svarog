/* StagerMethodDescriptor.java created 2008-02-08
 *
 */

package org.signalml.app.method.stager;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.config.ConfigurationDefaults;
import org.signalml.app.document.Document;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.MethodResultConsumer;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.method.stager.StagerMethod;
import org.signalml.method.stager.StagerParameters;

/** StagerMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerMethodDescriptor implements ApplicationMethodDescriptor {

	protected static final Logger logger = Logger.getLogger(StagerMethodDescriptor.class);

	public static final String ICON_PATH = "org/signalml/app/icon/runmethod.png";
	public static final String RUN_METHOD_STRING = "stagerMethod.runMethodString";

	private StagerMethod method;
	private StagerMethodConfigurer configurer;
	private StagerMethodConsumer consumer;
	private MethodPresetManager presetManager;

	public StagerMethodDescriptor(StagerMethod method) {
		this.method = method;
	}

	@Override
	public StagerMethod getMethod() {
		return method;
	}

	@Override
	public String getNameCode() {
		return RUN_METHOD_STRING;
	}

	@Override
	public String getIconPath() {
		return ICON_PATH;
	}

	@Override
	public MethodPresetManager getPresetManager(ApplicationMethodManager methodManager, boolean existingOnly) {
		if (presetManager == null && !existingOnly) {
			presetManager = new MethodPresetManager(method.getName(), StagerParameters.class);
			presetManager.setProfileDir(methodManager.getProfileDir());
			presetManager.setStreamer(methodManager.getStreamer());
			try {
				presetManager.readFromPersistence(null);
			} catch (IOException ex) {
				if (ex instanceof FileNotFoundException) {
					logger.debug("Seems like stager preset configuration doesn't exist");
				} else {
					logger.error("Failed to read stager presets - presets lost", ex);
				}
			}
		}
		return presetManager;
	}

	@Override
	public MethodConfigurer getConfigurer(ApplicationMethodManager methodManager) {
		if (configurer == null) {
			configurer = new StagerMethodConfigurer();
			configurer.setPresetManager(getPresetManager(methodManager, false));
			configurer.initialize(methodManager);
		}
		return configurer;
	}

	@Override
	public MethodResultConsumer getConsumer(ApplicationMethodManager methodManager) {
		if (consumer == null) {
			consumer = new StagerMethodConsumer();
			consumer.initialize(methodManager);
		}
		return consumer;
	}

	@Override
	public Object createData(ApplicationMethodManager methodManager) {

		Document document = methodManager.getActionFocusManager().getActiveDocument();
		if (!(document instanceof SignalDocument)) {
			OptionPane.showNoActiveSignal(methodManager.getDialogParent());
			return null;
		}
		SignalDocument signalDocument = (SignalDocument) document;

		StagerApplicationData data = new StagerApplicationData();
		data.setSignalDocument(signalDocument);

		ConfigurationDefaults.setStagerParameters(data.getParameters());

		return data;

	}

}
