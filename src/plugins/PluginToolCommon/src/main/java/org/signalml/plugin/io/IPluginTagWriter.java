package org.signalml.plugin.io;

import java.io.IOException;
import java.util.Collection;

import org.signalml.plugin.data.tag.PluginTagGroup;
import org.signalml.plugin.export.SignalMLException;

public interface IPluginTagWriter {

	public static int MAX_TAG_COUNT = 16380; //TODO move this elsewhere

	void writeTags(Collection<PluginTagGroup> tags) throws IOException, SignalMLException;

}
