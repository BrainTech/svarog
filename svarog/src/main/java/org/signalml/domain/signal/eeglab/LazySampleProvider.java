package org.signalml.domain.signal.eeglab;

import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.util.matfiles.array.lazy.ILazyDoubleArrayDataProvider;

public class LazySampleProvider implements ILazyDoubleArrayDataProvider {

	private MultichannelSampleSource sampleSource;
	private SignalWriterMonitor signalWriterMonitor;

	public LazySampleProvider(MultichannelSampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	public void setSignalWriterMonitor(SignalWriterMonitor signalWriterMonitor) {
		this.signalWriterMonitor = signalWriterMonitor;
	}

	public double[][] getDataChunk(int i, int length) {
		double[][] target = new double[sampleSource.getChannelCount()][length];

		for (int channel = 0; channel < sampleSource.getChannelCount(); channel++) {
			sampleSource.getSamples(channel, target[channel], i, length, 0);
		}

		if (signalWriterMonitor != null)
			signalWriterMonitor.setProcessedSampleCount(i + length);

		return target;
	}

	public int getWidth() {
		return sampleSource.getSampleCount(0);
	}

	public int getHeight() {
		return sampleSource.getChannelCount();
	}
}
