package org.signalml.domain.signal.filter.iir;

import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.domain.signal.samplesource.SampleSourceEngine;

public class TrimSignalSampleSource extends SampleSourceEngine {

	private int startIndex;
	private int endIndex;

	public TrimSignalSampleSource(SampleSource source, int startIndex, int endIndex) {
		super(source);

		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		int realOffset = startIndex + signalOffset;
		source.getSamples(target, realOffset, count, arrayOffset);
	}

	@Override
	public int getSampleCount() {
		return endIndex - startIndex;
	}

}
