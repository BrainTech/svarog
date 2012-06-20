package org.signalml.plugin.newartifact.logic.tag.creators;

import java.util.Arrays;
import java.util.List;

import org.signalml.plugin.newartifact.data.NewArtifactType;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public class MuscleTagCreator extends AbstractNewArtifactTagCreator implements
	INewArtifactTagCreator {

	private static final NewArtifactType CREATOR_TYPE = NewArtifactType.MUSCLE_ACTIVITY;

	private static final double FACTOR_A = 6;
	private static final double FACTOR_B = 1.5;

	private static final double TRESHOLD_A = 1;
	private static final double TRESHOLD_B = 0.05;

	private static final double FACTOR = 0.3777;

	@Override
	protected String getTagName() {
		return "M";
	}

	@Override
	protected String getTagDescription() {
		return "Art. miesniowe";
	}

	@Override
	protected int getTagStretch() {
		return 1;
	}

	@Override
	public NewArtifactTagResult tag(NewArtifactTagData data) {
		final double sensitivity = data.parameters
								   .getSensitivity(MuscleTagCreator.CREATOR_TYPE) / 100.0;
		final double treshold = MuscleTagCreator.FACTOR_A + sensitivity
								* (MuscleTagCreator.FACTOR_B - MuscleTagCreator.FACTOR_A);

		boolean exclusion[] = this.getExclusionMatrix(data);
		int eegChannels[] = data.eegChannels;

		double factorMatrix[] = new double[eegChannels.length];
		Arrays.fill(factorMatrix, MuscleTagCreator.TRESHOLD_A + sensitivity
					*(MuscleTagCreator.TRESHOLD_B - MuscleTagCreator.TRESHOLD_A));

		List<Integer> tags = this.createTagsUsingTreshold(data, factorMatrix, exclusion);

		double tresholdMatrix[] = this.getTresholdMatrix(data, exclusion, treshold);
		for (Integer tag : this.createTagsUsingTreshold(data, tresholdMatrix, exclusion)) {
			tags.add(tag);
		}

		return this.constructResult(tags);
	}

	@Override
	protected double[] getTresholdMatrix(NewArtifactTagData data,
										 boolean exclusion[], double treshold) {
		double source[][] = data.source;
		int blockCount = source[0].length;
		int eegChannels[] = data.eegChannels;
		int tail = blockCount >> 2;

		double tresholdMatrix[] = new double[eegChannels.length];

		double positiveMin = Double.POSITIVE_INFINITY;
		for (int i = 0; i < eegChannels.length; ++i) {
			if (exclusion[i]) {
				continue;
			}
			double channelData[] = source[eegChannels[i]];
			for (int j = 0; j < channelData.length; ++j) {
				if (channelData[j] > 0 && positiveMin > channelData[j]) {
					positiveMin = channelData[j];
				}
			}
		}

		positiveMin = Math.log(positiveMin);
		for (int i = 0; i < eegChannels.length; ++i) {
			if (!exclusion[i]) {
				double channelData[] = source[eegChannels[i]];
				for (int j = 0; j < channelData.length; ++j) {
					if (channelData[j] <= 0) {
						channelData[j] = positiveMin;
					} else {
						channelData[j] = Math.log(channelData[j]);
					}
				}

				channelData = Arrays.copyOf(channelData, channelData.length);
				Arrays.sort(channelData);

				double sum = 0.0, mean = 0.0;
				for (int j = tail; j < blockCount - tail - 1; ++j) {
					mean += channelData[j];
				}
				mean /= blockCount - 2 * tail - 1;
				for (int j = tail; j < blockCount - tail - 1; ++j) {
					double v = channelData[j] - mean;
					sum += v * v;
				}
				sum /= (blockCount - 2 * tail - 2);
				sum = Math.sqrt(sum);

				//double median = ((blockCount & 1) == 1) ? channelData[blockCount >> 1] :
				//			(channelData[blockCount >> 1] + channelData[(blockCount >> 1) - 1]) / 2.0D;

				double median = channelData[(blockCount >> 1) - 1];	//Czy tak sie powinno liczyc mediane?

				tresholdMatrix[i] = median + (sum / MuscleTagCreator.FACTOR) * treshold;
			}
		}

		return tresholdMatrix;
	}

}
