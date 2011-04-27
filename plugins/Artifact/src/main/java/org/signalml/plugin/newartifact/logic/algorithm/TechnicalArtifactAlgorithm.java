package org.signalml.plugin.newartifact.logic.algorithm;

import java.util.Arrays;

import org.signalml.plugin.newartifact.data.NewArtifactConstants;

public class TechnicalArtifactAlgorithm extends NewArtifactAlgorithmBase {

	public TechnicalArtifactAlgorithm(NewArtifactConstants constants) {
		super(constants);

		this.resultBuffer = new double[3][this.constants.channelCount];
	}

	@Override
	public double[][] computeHead(NewArtifactAlgorithmData data) {
		return this.zeros(1, data.constants.channelCount * 3);
	}

	@Override
	public double[][] computeTail(NewArtifactAlgorithmData data) {
		return this.zeros(1, data.constants.channelCount * 3);
	}

	@Override
	public double[][] compute(NewArtifactAlgorithmData data) {
		int blockLength = data.constants.getBlockWithSlopesLength();
		int tailLength = data.constants.getPaddingLength();
		int slopeLength = data.constants.getSlopeLength();
		int channelCount = data.constants.channelCount;

		double min[] = this.resultBuffer[0];
		double max[] = this.resultBuffer[1];
		double slope[] = this.resultBuffer[2];

		Arrays.fill(min, Double.POSITIVE_INFINITY);
		Arrays.fill(max, Double.NEGATIVE_INFINITY);
		Arrays.fill(slope, Double.NEGATIVE_INFINITY);

		double signal[][] = data.signal;

		for (int i = 0; i < channelCount; ++i) {
			double channelData[] = signal[i];
			for (int j = tailLength - slopeLength; j < tailLength - slopeLength + blockLength; ++j) {
				double value = channelData[j];
				min[i] = Math.min(min[i], value);
				max[i] = Math.max(max[i], value);
			}

			for (int j = tailLength - slopeLength;
					j < blockLength - slopeLength + tailLength - slopeLength; ++j) {
				slope[i] = Math.max(slope[i], Math.abs(channelData[j + slopeLength] - channelData[j]));
			}
		}


		return this.resultBuffer;
	}
}
