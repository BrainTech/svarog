package org.signalml.domain.signal.export.ascii;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.stream.IntStream;

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

		int sampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);
		int numberOfSamplesToGet = 0;

                this.writeMultiChannelSamplesHeader(fileWriter, descriptor, sampleSource);
                for (int sampleNumber = 0; sampleNumber < sampleCount; sampleNumber += numberOfSamplesToGet) {  
			numberOfSamplesToGet = Math.min(sampleCount - sampleNumber, BUFFER_SIZE);

                        double[][] dataChunk = this.getMultiChannelSamplesChunk(sampleSource, sampleNumber, numberOfSamplesToGet);
                        this.writeMultiChannelSamplesChunk(fileWriter, descriptor, dataChunk, BUFFER_SIZE);

                        if (monitor != null) {
				if (monitor.isRequestingAbort())
                                    return;
                                double processedSampleCount = this.channelCount * numberOfSamplesToGet;
                                processedSampleCount = Math.ceil(processedSampleCount / (this.channelCount * sampleCount));
                                monitor.setProcessedSampleCount((int) processedSampleCount); 
			}
                }
		fileWriter.close();
	}

	/**
	 * Returns chunk of samples from all channels.
	 * @param sampleNumber starting point (in time) from which on samples are fetched.
         * @param numberOfSamplesToGet number of samples which will be fetched for each channel.
	 * @throws IOException
	 */
        private double[][] getMultiChannelSamplesChunk(MultichannelSampleSource sampleSource, int sampleNumber, int numberOfSamplesToGet){
                double[][] samplesChunk = new double[this.channelCount][];

                IntStream.range(0, this.channelCount).forEach(
                        channelNumber -> {
                            samplesChunk[channelNumber] = new double[numberOfSamplesToGet];
                            sampleSource.getSamples(channelNumber, samplesChunk[channelNumber], sampleNumber, numberOfSamplesToGet, 0);
                        }
                );

                return samplesChunk;
        }

	/**
	 * Exports signal from a multiple channels to file.
	 * @param samplesChunk double[numberOfChannes][numberOfSamplesInChunk] matrix.
         * @param chunkSize numberOfSamplesInChunk.
	 * @throws IOException
	 */
        private void writeMultiChannelSamplesChunk(FileWriter output, SignalExportDescriptor descriptor, double[][] samplesChunk, int chunkSize) throws IOException {
                for (int sampleNumber = 0; sampleNumber < chunkSize; sampleNumber++){
                        for (int channelNumber = 0; channelNumber < this.channelCount - 1; channelNumber++){
                                output.write(this.formatter.format(samplesChunk[channelNumber][sampleNumber]) + descriptor.getSeparator());
                        }
                        output.write(this.formatter.format(samplesChunk[this.channelCount - 1][sampleNumber]) + "\n");
                }
        }

	/**
	 * Exports channels names as a header to file.
	 * @throws IOException
	 */
        private void writeMultiChannelSamplesHeader(FileWriter output, SignalExportDescriptor descriptor, MultichannelSampleSource sampleSource) throws IOException {
            for (int channelNumber = 0; channelNumber < this.channelCount - 1; channelNumber++){
                output.write(sampleSource.getLabel(channelNumber) + descriptor.getSeparator());
            }
            output.write(sampleSource.getLabel(this.channelCount - 1) + "\n");
        }
}
