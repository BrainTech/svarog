/* EvokedPotentialMethodDescriptor.java created 2008-01-12
 *
 */

package org.signalml.app.method.ep;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.MethodResultConsumer;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.method.ep.EvokedPotentialMethod;
import org.signalml.method.ep.EvokedPotentialParameters;
import org.signalml.plugin.export.method.BaseMethodData;
import org.signalml.plugin.export.signal.Document;

/** EvokedPotentialMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialMethodDescriptor implements ApplicationMethodDescriptor {

	protected static final Logger logger = Logger.getLogger(EvokedPotentialMethodDescriptor.class);

	public static final String ICON_PATH = "org/signalml/app/icon/runmethod.png";
	public static final String RUN_METHOD_STRING = _("ERP");

	private EvokedPotentialMethod method;
	private EvokedPotentialMethodConfigurer configurer;
	private EvokedPotentialMethodConsumer consumer;
	private MethodPresetManager presetManager;

	public EvokedPotentialMethodDescriptor(EvokedPotentialMethod method) {
		this.method = method;
	}

	@Override
	public EvokedPotentialMethod getMethod() {
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
			presetManager = new MethodPresetManager(method.getName(), EvokedPotentialParameters.class);
			presetManager.setProfileDir(methodManager.getProfileDir());
			presetManager.setStreamer(methodManager.getStreamer());
			try {
				presetManager.readFromPersistence(null);
			} catch (IOException ex) {
				if (ex instanceof FileNotFoundException) {
					logger.debug("Seems like ep preset configuration doesn't exist");
				} else {
					logger.error("Failed to read ep presets - presets lost", ex);
				}
			}
		}
		return presetManager;
	}

	@Override
	public MethodConfigurer getConfigurer(ApplicationMethodManager methodManager) {
		if (configurer == null) {
			configurer = new EvokedPotentialMethodConfigurer();
			configurer.setPresetManager(getPresetManager(methodManager, false));
			configurer.initialize(methodManager);
		}
		return configurer;
	}

	@Override
	public MethodResultConsumer getConsumer(ApplicationMethodManager methodManager) {
		if (consumer == null) {
			consumer = new EvokedPotentialMethodConsumer();
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

		TagDocument tagDocument = signalDocument.getActiveTag();

		EvokedPotentialApplicationData data = new EvokedPotentialApplicationData();
		data.setSignalDocument(signalDocument);
		data.setTagDocument(tagDocument);

		return data;

	}

}
