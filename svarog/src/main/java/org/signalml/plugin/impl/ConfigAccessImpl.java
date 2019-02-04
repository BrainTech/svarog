package org.signalml.plugin.impl;

import java.io.File;
import java.util.ArrayList;

import org.signalml.plugin.export.config.SvarogAccessConfig;
import org.signalml.plugin.export.config.SvarogConfiguration;
import org.signalml.plugin.loader.PluginLoader;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Svarog configuration facade (for plugins).
 *
 * @author Stanislaw Findeisen
 */
public class ConfigAccessImpl extends AbstractAccess implements SvarogAccessConfig {

	private ConfigAccessImpl() { }

	private static final ConfigAccessImpl _instance = new ConfigAccessImpl();

	protected static ConfigAccessImpl getInstance() {
		return _instance;
	}

	@Override
	public SvarogConfiguration getSvarogConfiguration() {
		return getViewerElementManager().getApplicationConfig();
	}

	@Override
	public File getProfileDirectory() {
		return new File(getViewerElementManager().getProfileDir().getAbsolutePath());
	}

}
