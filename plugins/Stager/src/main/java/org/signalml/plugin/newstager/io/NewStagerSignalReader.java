package org.signalml.plugin.newstager.io;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.plugin.io.IPluginDataSourceReader;

public class NewStagerSignalReader implements IPluginDataSourceReader {

	private final MultichannelSampleSource source;
	private final int channelCount;
	private final int samplesCount;
	private int signalOffset;
	private final int blockLengthInSapmles;


	public NewStagerSignalReader(MultichannelSampleSource sampleSource, int blockLengthInSamples) {
		this.source = sampleSource;
		this.channelCount = this.source.getChannelCount();
		this.samplesCount = this.computeSamplesCount();
		this.signalOffset = 0;
		this.blockLengthInSapmles = blockLengthInSamples;
	}

	@Override
	public boolean hasMoreSamples() throws InterruptedException {
		synchronized (this) {
			return this.signalOffset + this.blockLengthInSapmles <= this.samplesCount;
		}
	}

	@Override
	public void getSample(double[][] buffer) throws InterruptedException {
		assert(buffer.length == this.channelCount);

		for (int i = 0; i < buffer.length; ++i) {
			this.source.getSamples(i, buffer[i], this.signalOffset, buffer[i].length, 0);
		}

		synchronized (this) {
			this.signalOffset += buffer[0].length;
		}
	}

	private int computeSamplesCount() {
		int result = -1;
		for (int i = 0; i < this.channelCount; ++i) {
			result = Math.max(this.source.getSampleCount(i), result);
		}
		return result;
	}

}
