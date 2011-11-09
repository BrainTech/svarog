package org.signalml.app.config.preset;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.system.ChannelType;
import org.signalml.domain.montage.system.EegElectrode;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.math.geometry.Polar3dPoint;
import org.signalml.util.FileUtils;

/**
 * This {@link PresetManager} manages the available EEG systems definitions which
 * are stored in the 'eegSystems' directory inside the profile directory.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("eegSystems")
public class EegSystemsPresetManager extends AbstractPresetManager {

	/**
	 * The path to eegSystems directory stored in resource.
	 * (If user defined EEG systems do not exist, the files from this
	 * resource directory are copied to the profile directory).
	 */
	protected static String eegSystemsDirectoryInResources = "org/signalml/app/config/eegSystems/";

	/**
	 * The names of the files containing the EEG systems definitions.
	 */
	public static String[] defaultEegSystemsFileNames = {"eeg10_20_cap19.xml", "eeg10_20_cap25.xml", "eeg10_10_cap33.xml", "eeg10_10_cap47.xml"};

	/**
	 * The name of directory which is used to store eegSystems.
	 */
	public static String eegSystemsDirectory = "eegSystems";

	@Override
	public String getStandardFilename() {
		return "eegSystems/";
	}

	@Override
	public Class<?> getPresetClass() {
		return EegSystem.class;
	}

	@Override
	public XStream getStreamer() {
		if (streamer == null) {
			streamer = createStreamer();
		}
		return streamer;
	}

	/**
	 * Creates and returns an {@link XStreamer} used by this preset manager.
	 * @return
	 */
	protected XStream createStreamer() {
		streamer = XMLUtils.getDefaultStreamer();
		Annotations.configureAliases(streamer, EegSystemsPresetManager.class, EegSystem.class, EegElectrode.class, Polar3dPoint.class, ChannelType.class, IMontageGenerator.class);
		return streamer;
	}

	/**
	 * Returns the full absolute path to the 'eegSystems' directory.
	 * @return
	 */
	protected String getEegSystemsDirectoryFullPath() {
		return profileDir.getAbsolutePath() + File.separator + eegSystemsDirectory;
	}

	/**
	 * Creates a 'eegSystems' directory inside of the user directory and
	 * copies the default EEG systems definitions there. This action is performed
	 * only when the directory doesn't exist or if it is empty, otherwise
	 * this action has no effect. (The idea is to always have at least one
	 * EEG system definition inside of that directory).
	 * @throws FileNotFoundException thrown when the file to be copied is not found
	 * @throws IOException thrown when an error occurs while copying (reading from
	 * buffer/writing to buffer) the files with EEG systems
	 */
	public void createDefaultEegSystemsFilesIfNecessary() throws FileNotFoundException, IOException {
		File directory = new File(getEegSystemsDirectoryFullPath());
		if (directory.exists() && directory.listFiles().length > 0)
			return;

		FileUtils.createDirectory(getEegSystemsDirectoryFullPath());
		for (String file: defaultEegSystemsFileNames)
			FileUtils.copyFileFromResource(eegSystemsDirectoryInResources + file, getEegSystemsDirectoryFullPath() + File.separator + file);
	}

}
