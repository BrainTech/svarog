package org.signalml.app.config.preset;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.channel.ChannelFunction;

/**
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("channelFunctions")
public class ChannelFunctionsPresetManager extends AbstractResourceXMLConfiguration {

	@Override
	public String getStandardFilename() {
		return "org/signalml/app/config/channel_functions.xml";
	}

	@Override
	public Class<?> getPresetClass() {
		return ChannelFunction.class;
	}

	@Override
	public XStream getStreamer() {
		if (streamer == null)
			streamer = createStreamer();
		return streamer;
	}

	protected XStream createStreamer() {
		streamer = XMLUtils.getDefaultStreamer();
		Annotations.configureAliases(streamer, ChannelFunction.class, ChannelFunctionsPresetManager.class);
		streamer.setMode(XStream.XPATH_RELATIVE_REFERENCES);
		return streamer;
	}

}
