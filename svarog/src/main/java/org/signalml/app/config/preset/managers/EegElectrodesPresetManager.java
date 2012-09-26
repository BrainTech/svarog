package org.signalml.app.config.preset.managers;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.system.ChannelType;
import org.signalml.domain.montage.system.EegElectrode;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.system.EegSystemName;
import org.signalml.math.geometry.Polar3dPoint;

/**
 * This {@link PresetManager} manages the electrodes definitions of a {@link EegSystem}
 * which are handled like presets. It is capable of reading the electrodes definitions
 * from a directory and restoring default electrodes configuration from resource.
 *
 * @author Piotr Szachewicz
 */
public class EegElectrodesPresetManager extends AbstractMultifileRestoreablePresetManager {

	@Override
	public String getDirectoryName() {
		return "eegSystems/electrodes/";
	}

	@Override
	public String[] getDefaultFileNames() {
		return new String[] {
			"eeg10_20_cap19.xml",
			"eeg10_20_cap25.xml",
			"eeg10_10_cap33.xml",
			"eeg10_10_cap47.xml",
			"eeg10_20_capsleep.xml",
		};
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
		Annotations.configureAliases(streamer, EegSystemsPresetManager.class, EegSystem.class, EegElectrode.class, Polar3dPoint.class, ChannelType.class, IMontageGenerator.class, EegSystemName.class);
		return streamer;
	}

}
