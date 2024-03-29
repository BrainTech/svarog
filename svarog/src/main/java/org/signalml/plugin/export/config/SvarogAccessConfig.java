package org.signalml.plugin.export.config;

import java.io.File;

/**
 * Svarog configuration facade (for plugins).
 *
 * @author Stanislaw Findeisen
 */
public interface SvarogAccessConfig {
	SvarogConfiguration getSvarogConfiguration();

	/**
	 * Returns the new file object pointing to profile directory.
	 * @return the profile directory
	 */
	File getProfileDirectory();
}
