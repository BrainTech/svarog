package org.signalml.app.config.preset.managers;


import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.system.ChannelType;
import org.signalml.domain.montage.system.EegElectrode;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.system.EegSystemName;
import org.signalml.math.geometry.Polar3dPoint;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamer;
import com.thoughtworks.xstream.annotations.Annotations;

/**
 * This {@link PresetManager} manages the electrodes definitions of a {@link EegSystem}
 * which are handled like presets. It is capable of reading the electrodes definitions
 * from a directory and restoring default electrodes configuration from resource.
 *
 * @author Piotr Szachewicz
 */
public class EegElectrodesPresetManager extends AbstractMultifileResourcesPresetManager {

	@Override
	public String getDirectoryName() {
		return "eegSystems/electrodes/";
	}

	@Override
	public String[] getDefaultFileNames() {
		return new String[] {
			"EEG_10_10_easy_cap.xml",
			"EEG_10_20_easy_cap.xml",
			"EEG_10_10_gel_cap.xml",
			"EEG_10_20_blue_water_cap.xml",
			"EEG_10_20_black_water_cap.xml",
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
