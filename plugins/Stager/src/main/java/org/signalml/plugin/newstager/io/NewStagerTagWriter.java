package org.signalml.plugin.newstager.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.signalml.app.model.signal.PagingParameterDescriptor;
import org.signalml.plugin.data.io.PluginTagWriterConfig;
import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.data.tag.PluginTagGroup;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.io.PluginTagWriter;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerData;
import org.signalml.plugin.newstager.data.NewStagerFASPThreshold;
import org.signalml.plugin.newstager.data.NewStagerFixedParameters;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;
import org.signalml.util.Util;

public class NewStagerTagWriter {

	private final NewStagerData stagerData;
	private final NewStagerConstants constants;

	public NewStagerTagWriter(final NewStagerData stagerData,
			NewStagerConstants constants) {
		this.stagerData = stagerData;
		this.constants = constants;
	}

	public File writeTags(NewStagerTagCollectionType tagType,
			EnumSet<NewStagerTagCollectionType> stages,
			Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> tagMap)
			throws IOException, SignalMLException {

		final float stretch = this.constants.blockLengthInSecondsINT
				/ PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE;

		List<PluginTagGroup> sleepTags = new LinkedList<PluginTagGroup>();
		for (Entry<NewStagerTagCollectionType, Collection<IPluginTagDef>> entry : tagMap
				.entrySet()) {

			if (stages.contains(entry.getKey())) {
				Collection<IPluginTagDef> tagCollection = entry.getValue();
				if (!tagCollection.isEmpty()) {
					NewStagerTagCollectionType key = entry.getKey();
					sleepTags.add(new PluginTagGroup(this
							.getTagNameFromType(key), this
							.getGroupTypeFromCollectionType(tagType),
							tagCollection, stretch, this
									.getTagDescriptionFromType(key)));
				}
			}
		}

		File resultFile = this.getTagFileName(tagType);
		PluginTagWriter pluginTagWriter = new PluginTagWriter(resultFile,
				new PluginTagWriterConfig(this.constants.blockLengthInSecondsINT,
						PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE));
		pluginTagWriter.writeTags(sleepTags);

		return resultFile;
	}

	private File getTagFileName(NewStagerTagCollectionType key) {
		String path = this.stagerData.getProjectPath();
		String bookPath = this.stagerData.getParameters().bookFilePath;
		String bookName = Util.getFileNameWithoutExtension(new File(bookPath));

		NewStagerFASPThreshold deltaThreshold = this.stagerData.getParameters().thresholds.deltaThreshold;

		String type = "";
		switch (key) {
		case HYPNO_DELTA:
			type = "delta";
			break;
		case HYPNO_ALPHA:
			type = "alpha";
			break;
		case HYPNO_SPINDLE:
			type = "spindles";
			break;
		case SLEEP_PAGES:
			type = String.format(Locale.ROOT, "PrimHypnos_a%3.2f",
					deltaThreshold.amplitude.getMin());
			break;
		case CONSOLIDATED_SLEEP_PAGES:
			type = String.format(Locale.ROOT, "hypnos_a%3.2f",
					deltaThreshold.amplitude.getMin());
			break;
		default:
			return null;
		}

		String name = String.format("%s_%s.tag", bookName, type);
		return new File(path, name);
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
		case CONSOLIDATED_SLEEP_STAGE_REM:
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

	private String getTagDescriptionFromType(NewStagerTagCollectionType tagType) {
		switch (tagType) {
		case HYPNO_ALPHA:
		case HYPNO_SPINDLE:
		case HYPNO_DELTA:
			return this.getChannelTagDescription();
		case SLEEP_STAGE_1:
		case CONSOLIDATED_SLEEP_STAGE_1:
			return "Stage 1";
		case SLEEP_STAGE_2:
		case CONSOLIDATED_SLEEP_STAGE_2:
			return "Stage 2";
		case SLEEP_STAGE_3:
		case CONSOLIDATED_SLEEP_STAGE_3:
			return "Stage 3";
		case SLEEP_STAGE_4:
		case CONSOLIDATED_SLEEP_STAGE_4:
			return "Stage 4";
		case SLEEP_STAGE_R:
		case CONSOLIDATED_SLEEP_STAGE_REM:
			return "Stage REM";
		case SLEEP_STAGE_W:
		case CONSOLIDATED_SLEEP_STAGE_W:
			return "Stage W";
		case CONSOLIDATED_SLEEP_STAGE_M:
			return "MT";
		default:
			return "";
		}

	}

	private SignalSelectionType getGroupTypeFromCollectionType(
			NewStagerTagCollectionType tagType) {
		switch (tagType) {
		case CONSOLIDATED_SLEEP_PAGES:
		case SLEEP_PAGES:
			return SignalSelectionType.PAGE;
		default:
			return SignalSelectionType.CHANNEL;
		}
	}

	private String getChannelTagDescription() {
		final NewStagerParameters parameters = this.stagerData.getParameters();
		final NewStagerFASPThreshold deltaThreshold = parameters.thresholds.deltaThreshold;
		final NewStagerFixedParameters fixedParameters = this.stagerData
				.getFixedParameters();
		return String.format(Locale.ROOT, "%s-%suV, %s-%sHz, w_c %.1f",
				FormatNumber(deltaThreshold.amplitude.getMin()),
				FormatNumber(deltaThreshold.amplitude.getMax()),
				FormatNumber(deltaThreshold.frequency.getMin()),
				FormatNumber(deltaThreshold.frequency.getMax()),
				fixedParameters.widthCoeff);
	}

	private static String FormatNumber(double v) {
		return Double.isInfinite(v) ? "Inf" : String.format(Locale.ROOT,
				"%.1f", v);
	}

}
