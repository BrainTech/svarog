package org.signalml.plugin.newartifact.logic.algorithm;

import java.util.Arrays;

import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.logic.stat.Stat;

public class ECGArtifactAlgorithm  extends NewArtifactAlgorithmBase {

	private Stat correlationAlgorithm;

	public ECGArtifactAlgorithm(NewArtifactConstants constants) {
		super(constants);

		this.resultBuffer = new double[1][constants.channelCount];

		this.correlationAlgorithm = new Stat();
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
	public double[][] compute(NewArtifactAlgorithmData data) throws NewArtifactAlgorithmDataException {
		double buffer[] = this.resultBuffer[0];
		double signal[][] = data.signal;

		PreprocessHelper.Preprocess(signal, data.constants);

		int tailLength = data.constants.getPaddingLength();
		int blockLength = data.constants.getBlockLength();
		
		int ecgChannelNumber = this.getChannelNumber(data, "ECG");
		if (ecgChannelNumber < 0 || ecgChannelNumber >= signal.length) {
			return this.zeros(1, this.constants.channelCount);
		}
		
		double ecgChannel[] = Arrays.copyOfRange(signal[ecgChannelNumber],
				      tailLength, tailLength + blockLength);
		for (int i = 0; i < data.constants.channelCount; ++i) {
			double channelData[] = Arrays.copyOfRange(signal[i], tailLength, tailLength + blockLength);
			buffer[i] = Math.abs(this.correlationAlgorithm.computeCorrelation(channelData, ecgChannel));
		}

		return this.resultBuffer;
	}
}
