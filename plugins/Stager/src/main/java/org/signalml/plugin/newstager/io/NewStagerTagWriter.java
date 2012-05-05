package org.signalml.plugin.newstager.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.signalml.plugin.data.io.PluginTagWriterConfig;
import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.data.tag.PluginTagGroup;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.io.PluginTagWriter;
import org.signalml.plugin.newstager.data.NewStagerData;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;
import org.signalml.util.Util;

public class NewStagerTagWriter {

	private final NewStagerData stagerData;

	public NewStagerTagWriter(final NewStagerData stagerData) {
		this.stagerData = stagerData;
	}

	public File writeTags(NewStagerTagCollectionType tagType,
			EnumSet<NewStagerTagCollectionType> stages,
			Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> tagMap) throws IOException, SignalMLException {

		List<PluginTagGroup> sleepTags = new LinkedList<PluginTagGroup>();
		for (Entry<NewStagerTagCollectionType, Collection<IPluginTagDef>> entry : tagMap
				.entrySet()) {

			if (stages.contains(entry.getKey())) {
				Collection<IPluginTagDef> tagCollection = entry.getValue();
				if (!tagCollection.isEmpty()) {
					sleepTags.add(new PluginTagGroup(this
							.getTagNameFromType(entry.getKey()),
							SignalSelectionType.CHANNEL, tagCollection, 1,
							"test")); // TODO 1
				}
			}
		}

		File resultFile = this.getTagFileName(tagType);
		PluginTagWriter pluginTagWriter = new PluginTagWriter(
				resultFile, new PluginTagWriterConfig());
		pluginTagWriter.writeTags(sleepTags);
		
		return resultFile;
	}

	private File getTagFileName(NewStagerTagCollectionType key) {
		String path = this.stagerData.getProjectPath();
		String bookPath = this.stagerData.getParameters().getBookFilePath();
		String bookName = Util.getFileNameWithoutExtension(new File(bookPath));
		String ext = "test.tag";

		switch (key) {
		case HYPNO_DELTA:
			return new File(path, bookName + "_delta" + ext);
		case HYPNO_ALPHA:
			return new File(path, bookName + "_alpha" + ext);
		case HYPNO_SPINDLE:
			return new File(path, bookName + "_spindles" + ext);
		case SLEEP_PAGES:
			return new File(path, bookName + "_PrimHypnos_a" + ext);
		case CONSOLIDATED_SLEEP_PAGES:
			return new File(path, bookName + "_hypnos_a" + ext);
		default:
			return null;
		}
	}

	private String getTagNameFromType(NewStagerTagCollectionType tagType) {
		switch (tagType) {
		case HYPNO_ALPHA:
			return "A";
		case HYPNO_DELTA:
			return "S";
		case HYPNO_SPINDLE:
			return "W";
		case SLEEP_STAGE_1:
		case CONSOLIDATED_SLEEP_STAGE_1:
			return "1";
		case SLEEP_STAGE_2:
		case CONSOLIDATED_SLEEP_STAGE_2:
			return "2";
		case SLEEP_STAGE_3:
		case CONSOLIDATED_SLEEP_STAGE_3:
			return "3";
		case SLEEP_STAGE_4:
		case CONSOLIDATED_SLEEP_STAGE_4:
			return "4";
		case SLEEP_STAGE_R:
		case CONSOLIDATED_SLEEP_STAGE_R:
			return "r";
		case SLEEP_STAGE_W:
		case CONSOLIDATED_SLEEP_STAGE_W:
			return "w";
		case CONSOLIDATED_SLEEP_STAGE_M:
			return "m";
		default:
			return "";
		}
	}
}
