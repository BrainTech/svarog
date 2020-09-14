package org.signalml.domain.signal.filter.iir.helper;

import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.domain.signal.samplesource.SampleSourceEngine;

/**
 * Trims the given sample source so that it is a part of the
 * original sample source.
 *
 * @author Piotr Szachewicz
 */
public class TrimSignalSampleSource extends SampleSourceEngine {

	private int startIndex;
	private int endIndex;

	/**
	 * Constructor.
	 * @param source the sample source to be trimmed.
	 * @param startIndex the first index of the original sample source that should be
	 * included in this trimmed sample source.
	 * @param endIndex the last index of the original sample source that should be
	 * included in this trimmed sample source.
	 */
	public TrimSignalSampleSource(SampleSource source, int startIndex, int endIndex) {
		super(source);

		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	@Override
	public long getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		int realOffset = startIndex + signalOffset;
		return source.getSamples(target, realOffset, count, arrayOffset) + startIndex;
	}

	@Override
	public int getSampleCount() {
		return endIndex - startIndex;
	}

}
