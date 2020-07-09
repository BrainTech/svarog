package org.signalml.app.config.preset.managers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.signalml.app.config.AbstractXMLConfiguration;
import org.signalml.util.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * This class allows to load presets both from a specified directory in the resources
 * and in the profile directory.
 *
 * The resulting presets are the union of the presets read from these two sources.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractMultifileResourcesPresetManager extends AbstractPresetManager {

	/**
	 * The resource directory which is the root for storing all presets configurations.
	 */
	protected static final String resourcesDirectory = "org/signalml/app/config/";

	/**
	 * Returns the name of the directory in which the presets are (resources)
	 * and will be (profile directory) stored.
	 * @return the name of the directory with presets
	 */
	public abstract String getDirectoryName();

	/**
	 * The names of the default files in the resources directory that should
	 * be restored to the user's profile directory whenever restoration is
	 * performed.
	 * @return the names of the files to be restored
	 */
	public abstract String[] getDefaultFileNames();

	@Override
	public String getStandardFilename() {
		return getDirectoryName();
	}

	/**
	 * Returns the full absolute path to the 'eegSystems' directory.
	 * @return
	 */
	private String getDirectoryFullPath() {
		return profileDir.getAbsolutePath() + File.separator + getDirectoryName();
	}

	public void readFromPersistence(File file) throws IOException {

		// first - read from resources
		AbstractXMLConfiguration resourcesConfiguration = createConfiguration();
		if (resourcesConfiguration == null)
			return;

		for (String resourceFileName: getDefaultFileNames()) {
			AbstractXMLConfiguration config = createConfiguration();

			if (config == null)
				break;

			Resource resource = new ClassPathResource(resourcesDirectory + getDirectoryName() + resourceFileName);
			InputStream is = new BufferedInputStream(resource.getInputStream());

			getStreamer().fromXML(is, config);
			is.close();

			resourcesConfiguration.copyFrom(config);
		}
		// then - read from profileDir
		try {
				super.readFromPersistence(file);
			} catch (IOException e) {
					//do nothing
				}
		// merge both results
		this.copyFrom(resourcesConfiguration);
	}

	protected AbstractXMLConfiguration createConfiguration() {
		AbstractXMLConfiguration config = null;

		try {
			config = this.getClass().newInstance();
		} catch (InstantiationException e) {
			logger.error("", e);
		} catch (IllegalAccessException e) {
			logger.error("", e);
		}

		return config;

	}

	/**
	 * Creates the default directory in the user's profile directory.
	 */
	public void createProfileDirectoryIfNecessary() {

		File directory = new File(getDirectoryFullPath());
		if (!directory.exists())
			FileUtils.createDirectory(getDirectoryFullPath());

	}

}
