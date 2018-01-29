package org.signalml.app.config.preset.managers;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.app.util.XMLUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;

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
		return new String[] {
			"default_EEG_10_10_gel_cap.xml",
			"default_EEG_10_10_easy_cap.xml",
			"default_EEG_10_20_easy_cap.xml",
			"default_EEG_10_20_easy_cap_SPO2.xml",
			"default_EEG_10_20_black_water_cap.xml",
			"default_EEG_10_20_black_water_cap_SPO2.xml",
			"default_EEG_10_20_blue_water_cap.xml",
			"default_EEG_10_20_blue_water_cap_SPO2.xml",
			"default_MOBI6.xml",
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
