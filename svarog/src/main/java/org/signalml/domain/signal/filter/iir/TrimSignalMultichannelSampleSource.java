package org.signalml.domain.signal.filter.iir;

import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

public class TrimSignalMultichannelSampleSource extends MultichannelSampleProcessor {

	private int startIndex;
	private int endIndex;

	public TrimSignalMultichannelSampleSource(MultichannelSampleSource paddedSampleSource, int startIndex, int endIndex) {
		super(paddedSampleSource);

		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	@Override
	public int getSampleCount(int channel) {
		return endIndex - startIndex;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		int realOffset = startIndex + signalOffset;
		source.getSamples(channel, target, realOffset, count, arrayOffset);
	}

}
