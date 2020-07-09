package org.signalml.plugin.tool;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import java.util.List;
import org.signalml.app.util.XMLUtils;
import org.signalml.plugin.data.PluginConfig;

public class PluginStreamCreator {

	public XStream createInstance(PluginConfig config, List<Class<?>> aliases) {
		XStream streamer = XMLUtils.getDefaultStreamer();
		streamer.setClassLoader(config.getPluginClass().getClassLoader());
		Annotations.configureAliases(streamer, aliases.toArray(new Class<?>[0]));
		streamer.setMode(XStream.NO_REFERENCES);

		return streamer;
	}
}
