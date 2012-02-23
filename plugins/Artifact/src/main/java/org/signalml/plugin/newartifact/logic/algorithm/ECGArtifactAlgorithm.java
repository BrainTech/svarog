package org.signalml.plugin.newartifact.logic.algorithm;

import java.util.Arrays;

import org.signalm.plugin.domain.montage.PluginChannel;
import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.exception.NewArtifactPluginException;
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
	public double[][] compute(NewArtifactAlgorithmData data) throws NewArtifactPluginException {
		double buffer[] = this.resultBuffer[0];
		double signal[][] = data.signal;

		PreprocessHelper.Preprocess(signal, data.constants);

		final int tailLength = data.constants.getPaddingLength();
		final int blockLength = data.constants.getBlockLength();
		
		double signalData[] = this.getChannelData(data, PluginChannel.ECG);
		
		double ecgChannel[] = Arrays.copyOfRange(signalData,
				      tailLength, tailLength + blockLength);
		for (int i = 0; i < data.constants.channelCount; ++i) {
			double channelData[] = Arrays.copyOfRange(signal[i], tailLength, tailLength + blockLength);
			buffer[i] = Math.abs(this.correlationAlgorithm.computeCorrelation(channelData, ecgChannel));
		}

		return this.resultBuffer;
	}
}
