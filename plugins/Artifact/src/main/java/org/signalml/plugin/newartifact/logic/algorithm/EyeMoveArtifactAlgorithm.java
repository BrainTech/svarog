package org.signalml.plugin.newartifact.logic.algorithm;

import java.util.Arrays;

import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.logic.stat.Stat;

public class EyeMoveArtifactAlgorithm extends NewArtifactAlgorithmBase {

	private static double DEFAULT_EOG_CORRELATION = 7.0D;
	private static double DEFAULT_F78_CORRELATION = 8.0D;
	private static double DEFAULT_T34_CORRELATION = 9.0D;

	private Stat correlationAlgorithm;

	public EyeMoveArtifactAlgorithm(NewArtifactConstants constants) {
		super(constants);

		this.resultBuffer = new double[4][this.constants.channelCount];

		this.correlationAlgorithm = new Stat();
	}

	@Override
	public double[][] computeHead(NewArtifactAlgorithmData data) {
		return this.zeros(4, data.constants.channelCount);
	}

	@Override
	public double[][] computeTail(NewArtifactAlgorithmData data) {
		return this.zeros(4, data.constants.channelCount);
	}

	@Override
	public double[][] compute(NewArtifactAlgorithmData data)
			throws NewArtifactAlgorithmDataException {
		int blockLength = data.constants.getBlockLength();
		int tailLength = data.constants.getPaddingLength();

		PreprocessHelper.Preprocess(data.signal, data.constants);

		double signal[][] = new double[data.signal.length][];
		for (int i = 0; i < signal.length; ++i) {
			signal[i] = Arrays.copyOfRange(data.signal[i], tailLength,
					blockLength + tailLength);
		}

		double resultBuffer[] = this.resultBuffer[3];
		Arrays.fill(resultBuffer, 0.0D);

		resultBuffer[0] = this.computeCorrelation(signal, data, "EOGL", "EOGP",
				0, EyeMoveArtifactAlgorithm.DEFAULT_EOG_CORRELATION);
		resultBuffer[1] = this.computeCorrelation(signal, data, "F7", "F8", 1,
				EyeMoveArtifactAlgorithm.DEFAULT_F78_CORRELATION);
		resultBuffer[2] = this.computeCorrelation(signal, data, "T3", "T4", 2,
				EyeMoveArtifactAlgorithm.DEFAULT_T34_CORRELATION);

		return this.resultBuffer;
	}

	private double computeCorrelation(double signal[][],
			NewArtifactAlgorithmData data, String channelName1,
			String channelName2, int resultColumn, double defaultValue)
			throws NewArtifactAlgorithmDataException {
		int channel1 = this.getChannelNumber(data, channelName1);
		int channel2 = this.getChannelNumber(data, channelName2);

		if (channel1 < 0 || channel2 < 0 || channel1 >= signal.length
				|| channel2 >= signal.length) { // FIXME: < 0 czy <= 0?
			return defaultValue;
		} else {
			this.computeSingleCorrelation(signal, channel1, channel2,
					resultColumn);
			return this.correlationAlgorithm.computeCorrelation(
					signal[channel1], signal[channel2]);
		}
	}

	private void computeSingleCorrelation(double signal[][], int channel1,
			int channel2, int resultColumn) {
		double channel1Data[] = signal[channel1];
		double channel2Data[] = signal[channel2];
		double resultBuffer[] = this.resultBuffer[resultColumn];

		for (int i = 0; i < signal.length; ++i) {
			if (i != channel1 && i != channel2) {
				double c1 = this.correlationAlgorithm.computeCorrelation(
						channel1Data, signal[i]);
				double c2 = this.correlationAlgorithm.computeCorrelation(
						channel2Data, signal[i]);
				resultBuffer[i] = Math.max(Math.abs(c1), Math.abs(c2));
			} else {
				resultBuffer[i] = 0;
			}
		}
	}
}
