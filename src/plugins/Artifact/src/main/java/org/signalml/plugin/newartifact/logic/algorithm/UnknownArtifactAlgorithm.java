package org.signalml.plugin.newartifact.logic.algorithm;

import org.signalml.plugin.newartifact.data.NewArtifactConstants;

public class UnknownArtifactAlgorithm extends NewArtifactAlgorithmBase {

	private final static double EPSILON = Double.MIN_NORMAL;

	public UnknownArtifactAlgorithm(NewArtifactConstants constants) {
		super(constants);
		this.resultBuffer = new double[1][constants.channelCount];
	}

	@Override
	public double[][] computeHead(NewArtifactAlgorithmData data) {
		return this.zeros(1, this.constants.channelCount);
	}

	@Override
	public double[][] computeTail(NewArtifactAlgorithmData data) {
		return this.zeros(1, this.constants.channelCount);
	}

	@Override
	public double[][] compute(NewArtifactAlgorithmData data) {
		NewArtifactConstants constants = data.constants;
		int tailLength = constants.getPaddingLength();
		int blockLength = constants.getBlockLength();

		double buffer[] = this.resultBuffer[0];
		for (int i = 0; i < data.constants.channelCount; ++i) {
			double channelData[] = data.signal[i];
			double diff;
			int prevIdx = -2;
			int prevShiftIdx = -1;
			int maxIdxDiff = Integer.MIN_VALUE;
			int maxIdxShiftDiff = Integer.MIN_VALUE;

			for (int j = tailLength; j < blockLength + tailLength - 1; ++j) {
				diff = channelData[j + 1] - channelData[j];
				if (diff > EPSILON) {
					maxIdxDiff = Math.max(maxIdxDiff, j - tailLength - prevIdx);
					prevIdx = j - tailLength;
				} else {
					maxIdxShiftDiff = Math.max(maxIdxShiftDiff, j - tailLength
								   - prevShiftIdx);
					prevShiftIdx = j - tailLength;
				}
			}
			maxIdxDiff = Math.max(maxIdxDiff, blockLength - 1 - prevIdx);
			maxIdxShiftDiff = Math.max(maxIdxShiftDiff, blockLength
						   - prevShiftIdx);

			buffer[i] = Math.max(maxIdxDiff, maxIdxShiftDiff);
		}

		return this.resultBuffer;
	}
}
