package org.signalml.plugin.newstager.data.tag;

import java.util.Collection;

import org.signalml.plugin.data.tag.IPluginTagDef;

public class NewStagerTagCollection {

	public final NewStagerTagCollectionType type;
	public final Collection<IPluginTagDef> tags;

	public NewStagerTagCollection(NewStagerTagCollectionType type, Collection<IPluginTagDef> tags) {
		this.type = type;
		this.tags = tags;
	}
}
