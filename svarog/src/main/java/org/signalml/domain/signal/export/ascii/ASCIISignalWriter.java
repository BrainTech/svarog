package org.signalml.domain.signal.export.ascii;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.export.ISignalWriter;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * Exports signal to ASCII format.
 *
 * @author Paweł Kordowski, Piotr Szachewicz
 */
public class ASCIISignalWriter implements ISignalWriter {

	/**
	 * Maximum size of buffer used to write the signal to file.
	 */
	private static final int BUFFER_SIZE = 8192;

	/**
	 * Formatter used to format sample values.
	 */
	private DecimalFormat formatter = new DecimalFormat("#####0.0##############");

	/**
	 * Number of channels that will be written to the file.
	 */
	private int channelCount;

	/**
	 * Writes the signal to a file in ASCII format.
	 *
	 * @param outputPath
	 *            output file path
	 * @throws IOException
	 *             when the file cannot be written
	 */
	@Override
	public void writeSignal(File outputFile, MultichannelSampleSource sampleSource, SignalExportDescriptor descriptor, SignalWriterMonitor monitor) throws IOException {

		FileWriter fileWriter = new FileWriter(outputFile);
		this.channelCount = sampleSource.getChannelCount();

		for (int channelNumber = 0; channelNumber < channelCount; channelNumber++) {
			if(!writeSingleChannel(fileWriter, sampleSource, descriptor, monitor, channelNumber))
				return;

			if (channelNumber < sampleSource.getChannelCount()-1)
				fileWriter.write("\n");
		}
		fileWriter.close();
	}

	/**
	 * Exports signal from a single channel to file.
	 * @param channelNumber number of channel to be exported.
	 * @return false if writing data was cancelled, true if continuing is ok.
	 * @throws IOException
	 */
	protected boolean writeSingleChannel(
			FileWriter output, MultichannelSampleSource sampleSource,
			SignalExportDescriptor descriptor, SignalWriterMonitor monitor,
			int channelNumber) throws IOException {

		int sampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);
		int numberOfSamplesToGet = 0;

		for (int sampleNumber = 0; sampleNumber < sampleCount; sampleNumber += numberOfSamplesToGet) {
			numberOfSamplesToGet = Math.min(sampleCount - sampleNumber, BUFFER_SIZE);

			double[] data = new double[numberOfSamplesToGet];
			sampleSource.getSamples(channelNumber, data, sampleNumber, numberOfSamplesToGet, 0);

			for (double sample: data) {
				output.write(formatter.format(sample) + descriptor.getSeparator());
			}

			if (monitor != null && monitor.isRequestingAbort()) {
				return false;
			}

			if (monitor != null) {
				double processedSampleCount = channelNumber * sampleCount + sampleNumber + numberOfSamplesToGet;
				processedSampleCount = Math.ceil(processedSampleCount / channelCount);
				monitor.setProcessedSampleCount((int) processedSampleCount);
			}
		}
		return true;

	}

}
