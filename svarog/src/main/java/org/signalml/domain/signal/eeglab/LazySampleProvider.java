package org.signalml.domain.signal.eeglab;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

public class LazySampleProvider {

	private MultichannelSampleSource sampleSource;

	public LazySampleProvider(MultichannelSampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	public double[][] getSampleChunk(int i, int length) {
		double[][] target = new double[sampleSource.getChannelCount()][length];

		for (int channel = 0; channel < sampleSource.getChannelCount(); channel++) {
			sampleSource.getSamples(channel, target[channel], i, length, 0);
		}
		return target;
	}

	public int getWidth() {
		return sampleSource.getSampleCount(0);
	}

	public int getHeight() {
		return sampleSource.getChannelCount();
	}
}
