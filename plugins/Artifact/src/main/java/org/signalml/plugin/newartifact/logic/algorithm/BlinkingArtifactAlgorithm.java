package org.signalml.plugin.newartifact.logic.algorithm;


import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.logic.stat.Stat;

public class BlinkingArtifactAlgorithm extends NewArtifactAlgorithmBase {

	private Stat correlationAlgorithm;

	public BlinkingArtifactAlgorithm(NewArtifactConstants constants) {
		super(constants);

		this.resultBuffer = new double[constants.getBlockCapacity()][2];

		this.correlationAlgorithm = new Stat();
	}

	@Override
	public double[][] computeHead(NewArtifactAlgorithmData data) {
		return this.zeros(2 * this.constants.getBlockCapacity(), 1);
	}

	@Override
	public double[][] computeTail(NewArtifactAlgorithmData data) {
		return this.zeros(2 * this.constants.getBlockCapacity(), 1);
	}

	@Override
	public double[][] compute(NewArtifactAlgorithmData data) throws NewArtifactAlgorithmDataException {
		double corr1, corr2;

		for (int k = 0; k < data.constants.getBlockCapacity(); ++k) {
			double d1[] = this.shortDiff(data, k, "Fp1", "F3");
			double d2[] = this.shortDiff(data, k, "F3", "C3");
			double d3[] = this.shortDiff(data, k, "Fp2", "F4");
			double d4[] = this.shortDiff(data, k, "F4", "C4");

			double r1 = this.getRange(d1);
			double r2 = this.getRange(d2);
			double r3 = this.getRange(d3);
			double r4 = this.getRange(d4);

			if (r1 <= 0.0 || r2 <= 0.0 || r3 <= 0.0 || r4 <= 0.0) {
				corr1 = 1.0;
				corr2 = 1.0;
			} else {
				corr1 = this.correlationAlgorithm.computeCorrelation(d1, d2);
				corr2 = this.correlationAlgorithm.computeCorrelation(d3, d4);
			}

			this.resultBuffer[k][0] = corr1;
			this.resultBuffer[k][1] = corr2;
		}

		return this.resultBuffer;
	}

	private double getRange(double diffData[]) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < diffData.length; ++i) {
			min = Math.min(min, diffData[i]);
			max = Math.max(max, diffData[i]);
		}

		return max - min;
	}

	private double[] shortDiff(NewArtifactAlgorithmData data, int block, String channelName1,
				   String channelName2) throws NewArtifactAlgorithmDataException {
		int channel1 = this.getChannelNumber(data, channelName1); 
		int channel2 = this.getChannelNumber(data, channelName2);

		if (channel1 < 0 || channel2 < 0) {
			return null;
		}

		final NewArtifactConstants constants = data.constants;

		int start = block * constants.getSmallBlockLength() + constants.getPaddingLength()
			    - constants.getSmallPaddingLength();

		final double channelData1[] = data.signal[channel1];
		final double channelData2[] = data.signal[channel2];
		double diff[] = new double[data.constants.getSmallBlockLengthWithPadding()];
		for (int i = start, j = 0; i < start + constants.getSmallBlockLengthWithPadding(); ++i, ++j) {
			diff[j] = channelData1[i] - channelData2[i];
		}

		return diff;
	}

}
