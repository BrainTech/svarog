package org.signalml.app.config.preset;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
	 * The name of directory which is used to store eegSystems.
	 */
	public static String eegSystemsDirectory = "eegSystems";
	/**
	 * The path to eegSystems directory stored in resource.
	 * (If user defined EEG systems do not exist, the files from this
	 * resource directory are copied to the profile directory).
	 */
	protected static String eegSystemsDirectoryInResources = "org/signalml/app/config/eegSystems/";

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
	 * Returns if eegSystems directory exists and is not empty.
	 * @return false if eegSystems directory does not exist or if it is
	 * empty, true otherwise
	 */
	public boolean eegSystemsDirectoryExistsAndIsNotEmpty() {
		File directory = new File(getEegSystemsDirectoryFullPath());
		if (directory.exists()) {
			if (directory.listFiles().length == 0) {
				return false;
			}
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Copies the predefined EEG systems stored in Svarog resources
	 * to the {@link EegSystemsPresetManager#eegSystemsDirectory} in the profile
	 * directory.
	 */
	public void copyDefaultEegSystemsFromResource() {
		try {
			FileUtils.copyDirectoryFromResource(eegSystemsDirectoryInResources, getEegSystemsDirectoryFullPath());
		} catch (IOException ex) {
			Logger.getLogger(EegSystemsPresetManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
