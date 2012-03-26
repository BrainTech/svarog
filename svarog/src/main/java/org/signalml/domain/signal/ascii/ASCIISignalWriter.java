package org.signalml.domain.signal.ascii;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.SignalWriterMonitor;

/**
 * Exports signal to ASCII format.
 * 
 * @author Paweł Kordowski
 */
public class ASCIISignalWriter {

	private static final int BUFFER_SIZE = 8192;

	/**
	 * Writes the signal to a file in ASCII format.
	 * 
	 * @param outputPath
	 *            output file path
	 * @throws IOException
	 *             when the file cannot be written
	 */
	public void writeSignal(File outputFile,
			MultichannelSampleSource sampleSource,
			SignalExportDescriptor descriptor, SignalWriterMonitor monitor)
			throws IOException {
		int sampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);
		int bufferSize = Math.min(BUFFER_SIZE, sampleCount);
		int channelCount = sampleSource.getChannelCount();
		int multiSampleCount = channelCount * bufferSize;
		int cnt = 0;
		int length = channelCount * sampleCount;
		double[][] data = new double[channelCount][bufferSize];
		int bOffset;
		int channel = 0;
		int sample = 0;
		int currentSample = 0;
		int toGetCnt = 0;
		int samplesRemaining = sampleCount;
		int i;
		FileWriter output = new FileWriter(outputFile);
		do {
			if (monitor != null && monitor.isRequestingAbort()) {
				return;
			}
			toGetCnt = Math.min(bufferSize, samplesRemaining);
			for (i = 0; i < channelCount; i++) {
				sampleSource.getSamples(i, data[i], currentSample, toGetCnt, 0);
			}
			samplesRemaining -= toGetCnt;
			currentSample += toGetCnt;
			sample = 0;

			bOffset = 0;
			for (; cnt < length && bOffset < multiSampleCount; cnt++) {
				output.write(String.valueOf(data[channel][sample]) + descriptor.getSeparator());
				bOffset++;
				channel = (channel + 1) % channelCount;
				if (channel == 0) {
					sample++;
					output.write("\n");
				}
			}
			if (monitor != null) {
				monitor.setProcessedSampleCount(currentSample - 0);
			}
		} while (cnt < length);
	}
}
