package org.signalml.domain.signal.raw;

import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.ArrayOperations;

public class ReversedMultichannelSampleProcessor extends MultichannelSampleProcessor {

	public ReversedMultichannelSampleProcessor(MultichannelSampleSource source) {
		super(source);
	}

	@Override
	public long getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		int newSignalOffset = source.getSampleCount(channel) - signalOffset - count;

		double[] temporaryTarget = new double[count];
		source.getSamples(channel, temporaryTarget, newSignalOffset, count, 0);

		double[] reversedTempTarget = ArrayOperations.reverse(temporaryTarget);
		System.arraycopy(reversedTempTarget, 0, target, arrayOffset, count);

		return 0; // not applicable for on-line signals
	}

}
