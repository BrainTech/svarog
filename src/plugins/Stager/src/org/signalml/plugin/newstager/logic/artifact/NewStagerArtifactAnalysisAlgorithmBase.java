package org.signalml.plugin.newstager.logic.artifact;

import java.util.Arrays;
import java.util.Map;

import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.exception.NewStagerPluginException;
import org.signalml.plugin.newstager.logic.helper.NewStagerFilterHelper;

public abstract class NewStagerArtifactAnalysisAlgorithmBase {

	protected static final double butterEMGNum[] = { 0.787775776356201d,
			-3.938878881781003d, 7.877757763562006d, -7.877757763562006d,
			3.938878881781003d, -0.787775776356201d
						       };

	protected static final double butterEMGDen[] = { 1.000000000000000d,
			-4.523574464001561d, 8.205821335099809d, -7.460219756968050d,
			3.398618613518345d, -0.620590673810655d
						       };


	public static double[] GetChannelSignal(Map<String, Integer> channelMap,
						EegChannel channel, double signal[][]) throws NewStagerPluginException {
		int channelNum = channelMap.get(channel.getName());
		if (channelNum < 0 || channelNum >= signal.length) {
			throw new NewStagerPluginException("Invalid channel " + channel.getName());
		}
		return signal[channelNum];
	}

	protected double[] computeItermediateMeanFiltered(double channelSignal[],
			double filterNum[], double filterDen[], NewStagerConstants constants) {
		double filteredSignal[] = NewStagerFilterHelper.LowPassFilter(
						  channelSignal, filterNum, filterDen);
		return this.computeItermediateMean(filteredSignal, constants);
	}

	protected double[] computeItermediateMean(double channelSignal[],
			NewStagerConstants constants)
	{
		int length = constants.blockLengthInSeconds;
		int frequency = (int) constants.frequency;
		int lengthNN = (int) Math.round(0.8d * frequency);

		double mean[] = new double[length];

		assert(channelSignal.length == length * frequency);
		if (lengthNN <= 0) {
			return null;
		}

		for (int i = 0; i < channelSignal.length; ++i) {
			channelSignal[i] = channelSignal[i] * channelSignal[i];
		}

		for (int i = 0; i < length; ++i) {
			int start = i * frequency;
			double v = 0d;

			Arrays.sort(channelSignal, start, start + frequency);
			for (int j = 0; j < lengthNN; ++j) {
				v += channelSignal[start + j];
			}
			mean[i] = Math.sqrt(v / lengthNN);
		}

		return mean;
	}

}
