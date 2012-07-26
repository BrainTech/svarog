/* MP5MethodDescriptor.java created 2007-10-28
 *
 */

package org.signalml.app.method.mp5;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.ApplicationSerializableMethodDescriptor;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.MethodResultConsumer;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.method.mp5.MP5Method;
import org.signalml.method.mp5.MP5Parameters;
import org.signalml.plugin.export.method.BaseMethodData;
import org.signalml.plugin.export.signal.Document;

/** MP5MethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5MethodDescriptor implements ApplicationMethodDescriptor, ApplicationSerializableMethodDescriptor {

	protected static final Logger logger = Logger.getLogger(MP5MethodDescriptor.class);

	public static final String ICON_PATH = "org/signalml/app/icon/runmethod.png";
	public static final String RUN_METHOD_STRING = _("MP decomposition");

	private MP5Method method;
	private MP5MethodDialog configurer;
	private MP5MethodConsumer consumer;
	private MethodPresetManager presetManager;

	public MP5MethodDescriptor(MP5Method method) {
		this.method = method;
	}

	@Override
	public MP5Method getMethod() {
		return method;
	}

	@Override
	public String getName() {
		return RUN_METHOD_STRING;
	}

	@Override
	public String getIconPath() {
		return ICON_PATH;
	}

	@Override
	public MethodPresetManager getPresetManager(ApplicationMethodManager methodManager, boolean existingOnly) {
		if (presetManager == null && !existingOnly) {
			presetManager = new MethodPresetManager(method.getName(), MP5Parameters.class);
			File profileDir = methodManager.getProfileDir();
			presetManager.setStreamer(methodManager.getStreamer());
			if (profileDir != null) {
				presetManager.setProfileDir(profileDir);
				try {
					presetManager.readFromPersistence(null);
				} catch (IOException ex) {
					if (ex instanceof FileNotFoundException) {
						logger.debug("Seems like mp5 preset configuration doesn't exist");
					} else {
						logger.error("Failed to read mp5 presets - presets lost", ex);
					}
				}
			}
		}
		return presetManager;
	}

	@Override
	public MethodConfigurer getConfigurer(ApplicationMethodManager methodManager) {
		if (configurer == null) {
			configurer = new MP5MethodDialog(getPresetManager(methodManager, false), methodManager.getDialogParent());
			configurer.initialize(methodManager);
		}
		return configurer;
	}

	@Override
	public MethodResultConsumer getConsumer(ApplicationMethodManager methodManager) {
		if (consumer == null) {
			consumer = new MP5MethodConsumer();
			consumer.initialize(methodManager);
		}
		return consumer;
	}

	@Override
	public BaseMethodData createData(ApplicationMethodManager methodManager) {

		Document document = methodManager.getActionFocusManager().getActiveDocument();
		if (!(document instanceof SignalDocument)) {
			OptionPane.showNoActiveSignal(methodManager.getDialogParent());
			return null;
		}
		if (document instanceof MonitorSignalDocument) {
			OptionPane.showThisToolWorksOnlyForNonMonitorSignals(methodManager.getDialogParent());
			return null;
		}

		SignalDocument signalDocument = (SignalDocument) document;

		MP5ApplicationData data = new MP5ApplicationData();
		data.setSignalDocument(signalDocument);

		return data;

	}

	@Override
	public Object createDeserializedData(ApplicationMethodManager methodManager) {

		MP5ApplicationData data = new MP5ApplicationData();

		return data;

	}

}
