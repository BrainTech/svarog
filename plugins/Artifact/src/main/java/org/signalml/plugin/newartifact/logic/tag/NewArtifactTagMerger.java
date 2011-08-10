package org.signalml.plugin.newartifact.logic.tag;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.data.tag.PluginChannelTagDef;
import org.signalml.plugin.data.tag.PluginTagGroup;
import org.signalml.plugin.export.signal.ExportedSignalSelectionType;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public class NewArtifactTagMerger {

	private final static String NAME = "a";
	private final static int STRETCH_FACTOR = 4;
	private final static String DESCRIPTION = "artefakt <- Artefakt";

	private List<PluginTagGroup> groups;

	public NewArtifactTagMerger() {
		this.groups = new LinkedList<PluginTagGroup>();
	}

	public void addTag(NewArtifactTagResult tagResult) {
		this.groups.add(tagResult.tagGroup);
	}

	public NewArtifactTagResult merge() {
		return new NewArtifactTagResult(this.mergeGroups(this.groups));
	}

	private PluginTagGroup mergeGroups(
		Collection<PluginTagGroup> groupCollection) {
		float stretchFactor = NewArtifactTagMerger.STRETCH_FACTOR;

		Collection<IPluginTagDef> tags = new LinkedList<IPluginTagDef>();
		ExportedSignalSelectionType type = SignalSelectionType.PAGE;

		for (PluginTagGroup group : groupCollection) {
			if (group.stretchFactor == stretchFactor) {
				tags.addAll(group.tags);
			} else {
				double scale = stretchFactor / group.stretchFactor;
				for (IPluginTagDef tag : group.tags) {
					tags.add(new PluginChannelTagDef(tag.getOffset() * scale,
									 tag.getLength() * scale, tag.getChannel()));
				}
			}

			if (group.type == SignalSelectionType.BLOCK) {
				if (type == SignalSelectionType.PAGE) {
					type = group.type;
				}
			} else {
				if (group.type == SignalSelectionType.CHANNEL
						&& type != SignalSelectionType.CHANNEL) {
					type = group.type;
				}
			}
		}

		return new PluginTagGroup(NewArtifactTagMerger.NAME, type, tags,
					  stretchFactor, NewArtifactTagMerger.DESCRIPTION);
	}
}
