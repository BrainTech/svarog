package org.signalml.app.config.preset;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.app.util.XMLUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("experimentsPresets")
public class ExperimentsSettingsPresetManager extends AbstractPresetManager {

	@Override
	public Class<?> getPresetClass() {
		return ExperimentDescriptor.class;
	}

	@Override
	public String getStandardFilename() {
		return "experimentsPresets.xml";
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

			streamer.setMode(XStream.XPATH_RELATIVE_REFERENCES);
		}
		return streamer;
	}

}
