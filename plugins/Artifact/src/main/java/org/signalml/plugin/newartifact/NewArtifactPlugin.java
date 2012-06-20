package org.signalml.plugin.newartifact;

import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.tool.PluginAccessHelper;
import org.signalml.plugin.tool.PluginResourceRepository;

/**
 * @author kdr
 */

public class NewArtifactPlugin implements Plugin {

	private PluginMethodManager manager;
	private static NewArtifactI18nDelegate i18nDelegate;

	@Override
	public void register(SvarogAccess access) throws SignalMLException {
		PluginAccessHelper.SetupConfig(this,
									   access,
									   "classpath:org/signalml/plugin/newartifact/resource/config.xml");

		i18nDelegate = new NewArtifactI18nDelegate(access);
		this.manager = new PluginMethodManager(access,
											   (PluginConfigForMethod) PluginResourceRepository.GetResource("config", this.getClass()));

		this.setupGUI(access.getGUIAccess());
	}

	private void setupGUI(SvarogAccessGUI guiAccess)
	throws UnsupportedOperationException, PluginException {
		guiAccess.addButtonToToolsMenu(new NewArtifactPluginAction(this.manager));
	}

	/**
	 * I18n shortcut.
	 *
	 * @param msgKey message to translate (English version)
	 * @return
	 */
	public static String _(String msgKey) {
		return i18nDelegate._(msgKey);
	}

	/**
	 * I18n shortcut.
	 *
	 * @param msgKey message to translate and render (English version)
	 * @param arguments actual values to render
	 * @return
	 */
	public static String _R(String msgKey, Object ... arguments) {
		return i18nDelegate._R(msgKey, arguments);
	}

	/**
	 * Svarog i18n delegate getter.
	 * @return the shared delegate instance
	 */
	public static NewArtifactI18nDelegate i18n() {
		return i18nDelegate;
	}

	public static final String iconPath = "org/signalml/app/icon/runmethod.png";
}
