package org.signalml.domain.signal.eeglab;

import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.util.matfiles.MatlabFileWriter;
import org.signalml.util.matfiles.array.lazy.ILazyDoubleArrayDataProvider;

/**
 * This class can provide parts of the signal data to the {@link MatlabFileWriter}
 * in order to make the process lazy - i.e. not the whole sample array at once,
 * but part after part.
 *
 * @author Piotr Szachewicz
 */
public class LazySampleProvider implements ILazyDoubleArrayDataProvider {

	/**
	 * The sample source from which the samples will be provided.
	 */
	private MultichannelSampleSource sampleSource;

	/**
	 * The monitor of progress for the signal exporter.
	 */
	private SignalWriterMonitor signalWriterMonitor;

	public LazySampleProvider(MultichannelSampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	public void setSignalWriterMonitor(SignalWriterMonitor signalWriterMonitor) {
		this.signalWriterMonitor = signalWriterMonitor;
	}

	@Override
	public double[][] getDataChunk(int i, int length) {
		double[][] target = new double[sampleSource.getChannelCount()][length];

		for (int channel = 0; channel < sampleSource.getChannelCount(); channel++) {
			sampleSource.getSamples(channel, target[channel], i, length, 0);
		}

		if (signalWriterMonitor != null)
			signalWriterMonitor.setProcessedSampleCount(i + length);

		return target;
	}

	@Override
	public int getNumberOfColumns() {
		return sampleSource.getSampleCount(0);
	}

	@Override
	public int getNumberOfRows() {
		return sampleSource.getChannelCount();
	}
}
