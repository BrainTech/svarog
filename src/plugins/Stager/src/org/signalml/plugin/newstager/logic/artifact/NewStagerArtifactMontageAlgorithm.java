package org.signalml.plugin.newstager.logic.artifact;

import java.util.Arrays;

import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.plugin.newstager.data.NewStagerArtifactAlgorithmData;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.exception.NewStagerPluginException;

public class NewStagerArtifactMontageAlgorithm extends
	NewStagerArtifactAnalysisAlgorithmBase {

	private static final double NORMALIZING_FACTOR = 20.0d;

	private static final double butterEEGNum[] = { 0.293416181696000d,
			-1.467080908480002d, 2.934161816960004d, -2.934161816960004d,
			1.467080908480002d, -0.293416181696000d
						     };

	private static final double butterEEGDen[] = { 1.000000000000000d,
			-2.630709206701736d, 3.100229979210853d, -1.937767771895702d,
			0.634524080553226d, -0.086086775910497d
						     };

	private int montageArtifactCount;
	private int c3a2Count;
	private int c4a1Count;

	private NewStagerArtifactAlgorithmData data;

	public NewStagerArtifactMontageAlgorithm(NewStagerArtifactAlgorithmData data) {
		this.data = data;
	}

	public boolean run(double signal[][]) {

		this.montageArtifactCount = 0;
		this.c3a2Count = 0;
		this.c4a1Count = 0;

		int halfEpochSize = this.data.constants.blockLengthInSeconds >> 1;
		boolean eegFlag = this.data.parameters.analyseEEGChannelsFlag;
		boolean emgFlag = this.data.parameters.analyseEMGChannelFlag;

		if (!eegFlag && !emgFlag) {
			return false;
		}

		try {
			if (emgFlag) {
				this.computeMontageArtifactCount(signal);
			}

			if (eegFlag) {
				this.computeEEGArtifactCount(signal);
			}

		} catch (NewStagerPluginException e) {
			return false;
		}

		if (emgFlag & eegFlag) {
			return (this.montageArtifactCount > halfEpochSize
				|| this.c3a2Count > halfEpochSize || this.c4a1Count > halfEpochSize);
		} else if (emgFlag) {
			return this.montageArtifactCount > halfEpochSize;
		} else {
			return this.c3a2Count > halfEpochSize
			       || this.c4a1Count > halfEpochSize;
		}
	}

	private void computeMontageArtifactCount(double signal[][])
	throws NewStagerPluginException {
		NewStagerConstants constants = this.data.constants;
		double montageThreshold = this.data.parameters.thresholds.montageEMGThreshold;
		double montageToneThreshold = this.data.parameters.thresholds.montageToneEMGThreshold;
		double emgChannelSignal[] = GetChannelSignal(this.data.channels,
					    EegChannel.EMG, signal);

		double emgMean[] = this.computeItermediateMean(
					   Arrays.copyOf(emgChannelSignal, emgChannelSignal.length),
					   constants);
		double emgFilteredMean[] = this.computeItermediateMeanFiltered(
						   emgChannelSignal,
						   NewStagerArtifactMontageAlgorithm.butterEEGNum,
						   NewStagerArtifactMontageAlgorithm.butterEEGDen, constants);

		assert(emgFilteredMean.length == emgMean.length);

		int count = 0;
		for (int i = 0; i < emgMean.length; ++i) {
			if (emgMean[i] > montageThreshold
					&& emgFilteredMean[i] > montageToneThreshold) {
				++count;
			}
		}

		this.montageArtifactCount = count;
	}

	private void computeEEGArtifactCount(double signal[][])
	throws NewStagerPluginException {
		this.c3a2Count = this.computeDifferenceArtifactCount(signal,
				 EegChannel.C3, EegChannel.A2);
		this.c4a1Count = this.computeDifferenceArtifactCount(signal,
				 EegChannel.C4, EegChannel.A1);
	}

	private int computeDifferenceArtifactCount(double signal[][],
			EegChannel channel1, EegChannel channel2)
	throws NewStagerPluginException {
		double channelSignal1[] = GetChannelSignal(this.data.channels, channel1, signal);
		double channelSignal2[] = GetChannelSignal(this.data.channels, channel2, signal);
		double threshold = this.data.parameters.thresholds.montageEEGThreshold;

		assert(channelSignal1.length == channelSignal2.length);

		int length = channelSignal1.length;

		double channelSignalDifference[] = new double[length];

		for (int i = 0; i < length; ++i) {
			channelSignalDifference[i] = (channelSignal1[i] - channelSignal2[i])
						     / NewStagerArtifactMontageAlgorithm.NORMALIZING_FACTOR;
		}

		double mean[] = this.computeItermediateMeanFiltered(
					channelSignalDifference,
					NewStagerArtifactMontageAlgorithm.butterEEGNum,
					NewStagerArtifactMontageAlgorithm.butterEEGDen,
					this.data.constants);

		int count = 0;
		for (int i = 0; i < mean.length; ++i) {
			if (mean[i] > threshold) {
				++count;
			}
		}
		return count;
	}
}
