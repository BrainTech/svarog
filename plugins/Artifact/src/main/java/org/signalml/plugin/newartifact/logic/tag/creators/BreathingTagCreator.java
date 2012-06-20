package org.signalml.plugin.newartifact.logic.tag.creators;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.signalml.plugin.newartifact.data.NewArtifactType;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public class BreathingTagCreator extends AbstractNewArtifactTagCreator implements
	INewArtifactTagCreator {

	private static final NewArtifactType CREATOR_TYPE = NewArtifactType.BREATHING;

	private static final double FACTOR_A = 1;
	private static final double FACTOR_B = 0.88;

	private static final double TRESHOLD_A = 1;
	private static final double TRESHOLD_B = 0.5;


	@Override
	protected String getTagName() {
		return "G";
	}

	@Override
	protected String getTagDescription() {
		return "Galw. oddech.";
	}

	@Override
	protected int getTagStretch() {
		return 4;
	}

	@Override
	public NewArtifactTagResult tag(NewArtifactTagData data) {
		final double sensitivity = data.parameters
								   .getSensitivity(BreathingTagCreator.CREATOR_TYPE) / 100.0;
		final double factor = BreathingTagCreator.FACTOR_A + sensitivity
							  * (BreathingTagCreator.FACTOR_B - BreathingTagCreator.FACTOR_A);
		final double treshold = BreathingTagCreator.TRESHOLD_A + sensitivity
								* (BreathingTagCreator.TRESHOLD_B - BreathingTagCreator.TRESHOLD_A);

		double source[][] = data.source;
		int blockCount = source[0].length;
		int eegChannels[] = data.eegChannels;
		boolean exclusion[] = this.getExclusionMatrix(data);
		double tresholdMatrix[] = this.getTresholdMatrix(data, exclusion, treshold);
		List<Integer> tags = this.createTagsUsingTreshold(data, tresholdMatrix, exclusion);

		SortedSet<Integer> sortedTags = new TreeSet<Integer>(tags);
		if (factor != 1.0) {
			for (Integer j : tags) {
				if (j != 0 && j != blockCount - 1) {
					boolean hasPrev = false;
					boolean hasNext = false;
					for (int i = 0; i < eegChannels.length; ++i) {
						double channelData[] = source[eegChannels[i]];
						if (channelData[j] > tresholdMatrix[i]) {// redundant
							hasPrev = hasPrev
									  || channelData[j - 1] > tresholdMatrix[i]
									  * factor;
							hasNext = hasNext
									  || channelData[j + 1] > tresholdMatrix[i]
									  * factor;
						}
						if (hasPrev && hasNext) {
							break;
						}
					}
					if (hasPrev) {
						sortedTags.add(j - 1);
					}
					if (hasNext) {
						sortedTags.add(j + 1);
					}
				}
			}
		}

		return this.constructResult(sortedTags);
	}

}
