package org.signalml.app.worker.amplifiers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.List;
import org.signalml.app.config.preset.AbstractPresetManager;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.util.XMLUtils;

/**
 * Amplifier definition preset manager.
 *
 * @author Tomasz Sawicki
 */
@XStreamAlias("definitions")
public class AmplifierDefinitionPresetManager extends AbstractPresetManager {

        @Override
        public String getStandardFilename() {
                return "definitions.xml";
        }

        @Override
        public Class<?> getPresetClass() {
                return AmplifierDefinition.class;
        }

	@Override
	public XStream getStreamer() {

		if (streamer == null)
			streamer = createAmplifierDefinitionPresetStreamer();
		return streamer;

	}

	private XStream createAmplifierDefinitionPresetStreamer() {

		XStream streamer = XMLUtils.getDefaultStreamer();
		XMLUtils.configureStreamerForAmplifierDefinition(streamer);
		streamer.setMode(XStream.XPATH_RELATIVE_REFERENCES);

		return streamer;
	}

        /**
         * Returns a list of presets. Checks if data is consistent.
         *
         * @return list of presets.
         * @throws Exception when data is not consistent.
         */
        public List<AmplifierDefinition> getDefinitionList() throws Exception {

                ArrayList<AmplifierDefinition> definitions = new ArrayList<AmplifierDefinition>();

                for (Preset preset : presets) {
                        AmplifierDefinition definition = (AmplifierDefinition) preset;

                        int channelNoSize = definition.getChannelNumbers().size();
                        int channelGainSize = definition.getChannelGain().size();
                        int channelOffsetSize = definition.getChannelOffset().size();

                        if (channelNoSize != channelGainSize) {
                                throw new Exception();
                        }
                        else if (channelNoSize != channelOffsetSize) {
                                throw new Exception();
                        }
                        
                        definitions.add(definition);
                }

                return definitions;
        }
}
