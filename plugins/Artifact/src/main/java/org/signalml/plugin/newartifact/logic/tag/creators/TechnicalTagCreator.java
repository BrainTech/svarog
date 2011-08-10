package org.signalml.plugin.newartifact.logic.tag.creators;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.signalml.plugin.newartifact.data.NewArtifactType;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;
import org.signalml.plugin.newartifact.logic.stat.Stat;

public class TechnicalTagCreator extends AbstractNewArtifactTagCreator
	implements INewArtifactTagCreator {

	private static final NewArtifactType CREATOR_TYPE = NewArtifactType.TECHNICAL;

	private static final double FACTOR = 0.3777D;

	private static final double TRESHOLDM_A = 10D;
	private static final double TRESHOLDM_B = 2D;

	private static final double TRESHOLDS_A = 6D;
	private static final double TRESHOLDS_B = 1D;

	private Stat stdDeviationAlgorithm;

	public TechnicalTagCreator() {
		super();
		this.stdDeviationAlgorithm = new Stat();
	}

	@Override
	protected String getTagName() {
		return "X";
	}

	@Override
	protected String getTagDescription() {
		return "Aparaturowe.";
	}

	@Override
	protected int getTagStretch() {
		return 4;
	}

	@Override
	public NewArtifactTagResult tag(NewArtifactTagData data) {
		final double sensitivity = data.parameters
					   .getSensitivity(TechnicalTagCreator.CREATOR_TYPE) / 100.0;
		final double tresholdM = TechnicalTagCreator.TRESHOLDM_A
					 + sensitivity
					 * (TechnicalTagCreator.TRESHOLDM_B - TechnicalTagCreator.TRESHOLDM_A);
		final double tresholdS = TechnicalTagCreator.TRESHOLDS_A
					 + sensitivity
					 * (TechnicalTagCreator.TRESHOLDS_B - TechnicalTagCreator.TRESHOLDS_A);

		boolean exclusions[] = this.getExclusionMatrix(data);
		double channelDataCopy[][] = new double[3][];
		double median[] = new double[3];
		double std[] = new double[3];
		double tresholds[] = new double[] { tresholdM, tresholdM, tresholdS };
		int eegChannels[] = data.eegChannels;

		int blockCount = data.source[0].length / 3;
		int tailLength = blockCount >> 2;

		List<Integer> tags = new LinkedList<Integer>();

		for (int j = 0; j < eegChannels.length; ++j) {
			double channelData[] = data.source[eegChannels[j]];

			for (int i = 0; i < channelData.length; i += 3) {
				channelData[i] = -channelData[i];
			}

			for (int k = 0; k < 2; ++k) {
				double min = Double.POSITIVE_INFINITY;
				for (int i = k; i < channelData.length; i += 3) {
					if (min > channelData[i]) {
						min = channelData[i];
					}
				}

				channelDataCopy[k] = shiftMin(channelData, k, 3, min);
			}

			channelDataCopy[2] = shiftMin(channelData, 2, 3, 0.0D);

			for (int k = 0; k < 3; ++k) {
				double sortedData[] = Arrays.copyOf(channelDataCopy[k],
								    channelDataCopy[k].length);
				Arrays.sort(sortedData);
				median[k] = sortedData[blockCount >> 1];
				std[k] = this.stdDeviationAlgorithm.standardDeviation(Arrays.copyOfRange(sortedData,
						tailLength - 1, blockCount - tailLength - 1))
					 / TechnicalTagCreator.FACTOR;
			}

			for (int i = 0; i < blockCount; ++i) {
				for (int k = 0; k < 3; ++k) {
					double value = exclusions[eegChannels[j]] ? 0.0
						       : channelDataCopy[k][i];
					if (value > median[k] + std[k] * tresholds[k]) {
						tags.add(i);
						break;
					}
				}
			}
		}

		return this.constructResult(tags);
	}

	private double[] shiftMin(double channelData[], int start, int step,
				  double min) {
		double channelCopy[] = new double[(channelData.length + step - 1)
						  / step];

		double positiveMin = Double.POSITIVE_INFINITY;
		for (int i = start; i < channelData.length; i += step) {
			if (channelData[i] >= min && positiveMin > channelData[i]) {
				positiveMin = channelData[i];
			}
		}

		positiveMin -= min;
		for (int j = 0, i = start; i < channelData.length; i += step, ++j) {
			channelCopy[j] = Math.log(channelData[i] > min ? channelData[i]
						  - min : positiveMin);
		}

		return channelCopy;
	}

}
