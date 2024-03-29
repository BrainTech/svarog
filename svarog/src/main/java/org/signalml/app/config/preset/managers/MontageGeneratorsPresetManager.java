package org.signalml.app.config.preset.managers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.system.EegElectrode;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.system.MontageGenerators;

/**
 *
 * This {@link PresetManager} manages the
 * {@link MontageGenerators montage generators} definitions. Montage generators
 * definitions and the list of {@link EegElectrode EEG electrodes} form an
 * {@link EegSystem} - these definitions are read and linked together by the
 * {@link EegSystemsPresetManager}.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("monageGeneratorPresets")
public class MontageGeneratorsPresetManager extends AbstractMultifileResourcesPresetManager {

	@Override
	public String getDirectoryName() {
		return "eegSystems/generators/";
	}

	@Override
	public String[] getDefaultFileNames() {
		return new String[]{
			"eeg_easy_cap_10_10_gen",
			"eeg_easy_cap_10_20_gen",
			"eeg_gel_cap_10_10_gen.xml",
			"eeg_gel_cap_10_20_gen.xml",
			"eeg_black_water_cap_gen.xml",};
	}

	@Override
	public Class<?> getPresetClass() {
		return MontageGenerators.class;
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
	 *
	 * @return
	 */
	protected XStream createStreamer() {
		streamer = XMLUtils.getDefaultStreamer();
		Annotations.configureAliases(streamer, EegSystemsPresetManager.class, MontageGenerators.class, IMontageGenerator.class);
		return streamer;
	}
}
