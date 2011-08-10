package org.signalml.plugin.newartifact.logic.tag.creators;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.data.tag.PluginChannelTagDef;
import org.signalml.plugin.data.tag.PluginTagGroup;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public abstract class AbstractNewArtifactTagCreator {

	protected NewArtifactTagResult constructResult(Collection<Integer> offsets) {
		int stretch = this.getTagStretch();

		Collection<IPluginTagDef> tags = new LinkedList<IPluginTagDef>();
		for (Integer offset : offsets) {
			tags.add(new PluginChannelTagDef((double) stretch * offset, (double) stretch, -1));
		}

		return new NewArtifactTagResult(new PluginTagGroup(
							this.getTagName(),
							SignalSelectionType.BLOCK,
							tags,
							this.getTagStretch(),
							this.getTagDescription()));
	}

	protected boolean[] getExclusionMatrix(NewArtifactTagData data) {
		double source[][] = data.source;
		boolean exclusion[] = new boolean[source.length];
		Arrays.fill(exclusion, false);
		if (data.excludedChannels != null) {
			for (int i = 0; i < data.excludedChannels.length; ++i) {
				if (data.excludedChannels[i] > 0) {
					exclusion[data.excludedChannels[i] - 1] = true;
				}
			}
		}

		return exclusion;
	}

	protected double[] getTresholdMatrix(NewArtifactTagData data,
					     boolean exclusion[], double baseTreshold) {
		double source[][] = data.source;
		int eegChannels[] = data.eegChannels;
		double tresholdMatrix[] = new double[eegChannels.length];
		for (int i = 0; i < eegChannels.length; ++i) {
			if (!exclusion[eegChannels[i]]) {
				double channelData[] = source[eegChannels[i]];
				channelData = Arrays.copyOf(channelData, channelData.length);
				Arrays.sort(channelData);
				double median = (channelData.length % 2 != 0) ? channelData[channelData.length / 2]
						: (channelData[(channelData.length / 2) - 1] + channelData[channelData.length / 2]) / 2.0D;

				tresholdMatrix[i] = median + (1.0 - median) * baseTreshold;
			}
		}

		return tresholdMatrix;
	}

	protected List<Integer> createTagsUsingTreshold(NewArtifactTagData data,
			double[] tresholdMatrix, boolean[] exclusion) {
		double source[][] = data.source;
		int blockCount = source[0].length;
		int eegChannels[] = data.eegChannels;

		List<Integer> tags = new LinkedList<Integer>();
		for (int j = 0; j < blockCount; ++j) {
			for (int i = 0; i < eegChannels.length; ++i) {
				if (!exclusion[eegChannels[i]]
						&& source[eegChannels[i]][j] > tresholdMatrix[i]) {
					tags.add(j);
					break;
				}
			}
		}

		return tags;
	}

	abstract protected String getTagName();

	abstract protected int getTagStretch();

	abstract protected String getTagDescription();

}
