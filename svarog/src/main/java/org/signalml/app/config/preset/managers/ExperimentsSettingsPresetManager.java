package org.signalml.app.config.preset.managers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.app.util.XMLUtils;

@XStreamAlias("experimentsPresets")
public class ExperimentsSettingsPresetManager extends AbstractMultifileResourcesPresetManager {

	@Override
	public Class<?> getPresetClass() {
		return ExperimentDescriptor.class;
	}

	@Override
	public String getStandardFilename() {
		return "experimentsPresets.xml";
	}

	@Override
	public String getDirectoryName() {
		return "eegSystems/presets/";
	}

	@Override
	public String[] getDefaultFileNames() {
		return new String[]{
			"Perun32 A EEG 10-20 Cap",
			"Perun32 B EEG 10-20 Cap",
			"Perun32 C EEG 10-20 Cap",
			"Perun32 D EEG 10-20 Cap",
			"TMSI EEG 10-10 gel cap",
			"TMSI EEG 10-10 EasyCap",
			"TMSI EEG 10-20 EasyCap",
			"TMSI EEG 10-20 EasyCap SpO2",
			"TMSI EEG 10-20 Black Water Cap",
			"TMSI EEG 10-20 Black Water Cap SpO2",
			"TMSI EEG 10-20 Blue Water Cap",
			"TMSI EEG 10-20 Blue Water Cap SpO2",
			"TMSI MOBI6",
			};
	}

	@Override
	public XStream getStreamer() {
		if (streamer == null) {
			streamer = XMLUtils.getDefaultStreamer();

			Annotations.configureAliases(
					streamer,
					ExperimentsSettingsPresetManager.class,
					ExperimentDescriptor.class,
					Amplifier.class,
					AmplifierChannel.class,
					SignalParameters.class
			);
		}
		return streamer;
	}
}
