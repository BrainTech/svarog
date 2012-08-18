package org.signalml.domain.signal.raw;

import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.domain.signal.samplesource.SampleSourceEngine;
import org.signalml.math.ArrayOperations;

public class ReversedSampleSource extends SampleSourceEngine {

	public ReversedSampleSource(SampleSource source) {
		super(source);
	}

	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		int newSignalOffset = source.getSampleCount() - signalOffset - count;

		double[] temporaryTarget = new double[count];
		source.getSamples(temporaryTarget, newSignalOffset, count, 0);

		double[] reversedTempTarget = ArrayOperations.reverse(temporaryTarget);
		System.arraycopy(reversedTempTarget, 0, target, arrayOffset, count);
	}

}
