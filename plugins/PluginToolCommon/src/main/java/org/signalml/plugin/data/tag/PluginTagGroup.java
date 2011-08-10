package org.signalml.plugin.data.tag;

import java.util.Collection;

import org.signalml.plugin.export.signal.ExportedSignalSelectionType;

public class PluginTagGroup {

	public final String name;
	public final ExportedSignalSelectionType type;
	public final Collection<IPluginTagDef> tags;
	public final float stretchFactor;
	public final String description;


	public PluginTagGroup(String name,
			      ExportedSignalSelectionType type,
			      Collection<IPluginTagDef> tags,
			      float stretchFactor,
			      String description) {
		this.name = name;
		this.type = type;
		this.tags = tags;
		this.stretchFactor = stretchFactor;
		this.description = description;
	}

}
