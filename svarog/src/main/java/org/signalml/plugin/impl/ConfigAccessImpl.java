package org.signalml.plugin.impl;

import java.io.File;
import org.signalml.plugin.export.config.SvarogAccessConfig;
import org.signalml.plugin.export.config.SvarogConfiguration;

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
