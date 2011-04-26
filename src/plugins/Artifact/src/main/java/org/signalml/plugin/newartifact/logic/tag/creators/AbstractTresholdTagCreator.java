package org.signalml.plugin.newartifact.logic.tag.creators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;

public abstract class AbstractTresholdTagCreator extends AbstractNewArtifactTagCreator {

	protected Collection<Integer> getTagsFromTreshold(NewArtifactTagData data, double treshold) {
		boolean exclusion[] = this.getExclusionMatrix(data);
		double source[][] = data.source;
		int eegChannels[] = data.eegChannels;

		List<Integer> tags = new LinkedList<Integer>();

		if (eegChannels == null || eegChannels.length == 0) {
			return tags;
		}

		for (int j = 0; j < source[eegChannels[0]].length; ++j) {
			for (int i = 0; i < eegChannels.length; ++i) {
				int channel = eegChannels[i];
				if (!exclusion[channel] && source[channel][j] > treshold) {
					tags.add(j);
					break;
				}
			}
		}

		return tags;
	}

}
