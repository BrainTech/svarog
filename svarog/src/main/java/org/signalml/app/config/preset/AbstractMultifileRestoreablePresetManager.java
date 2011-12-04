package org.signalml.app.config.preset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.signalml.util.FileUtils;


/**
 * This class allows to load presets from a specified directory and restore
 * (i.e. copy the files from resources to the user's profile directory)
 * default presets files from resource when it's needed.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractMultifileRestoreablePresetManager extends AbstractPresetManager {

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

	/**
	 * Copies the default files from resources directory to the user's profile
	 * directory.
	 * @throws FileNotFoundException thrown when the file to be copied is not found
	 * @throws IOException thrown when an error occurs while copying (reading from
	 * buffer/writing to buffer) the files from resources
	 */
	public void restoreDefaultPresetFilesIfNecessary() throws FileNotFoundException, IOException {
		File directory = new File(getDirectoryFullPath());
		if (directory.exists() && directory.listFiles().length > 0)
			return;

		FileUtils.createDirectory(getDirectoryFullPath());
		for (String file: getDefaultFileNames())
			FileUtils.copyFileFromResource(resourcesDirectory + getDirectoryName() + file, getDirectoryFullPath() + File.separator + file);
	}

}
