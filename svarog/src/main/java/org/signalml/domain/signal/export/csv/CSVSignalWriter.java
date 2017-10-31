/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.domain.signal.export.csv;

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
 *
 * @author Konrad Jan Bednarek
 */
public class CSVSignalWriter implements ISignalWriter {

	/**
	 * Maximum size of buffer used to write the signal to file.
	 */
	private static final int BUFFER_SIZE = 1024 * 1024;

	/**
	 * Formatter used to format sample values.
	 */
	private final DecimalFormat formatter = new DecimalFormat("#####0.0##############");

	/**
	 * Number of channels that will be written to the file.
	 */
	private int channelCount;
        
        @Override
        public void writeSignal(File outputFile, MultichannelSampleSource sampleSource, SignalExportDescriptor descriptor, SignalWriterMonitor monitor) throws IOException {
                try (FileWriter fileWriter = new FileWriter(outputFile)) {
                        this.channelCount = sampleSource.getChannelCount();
                        int bufferSizePerChannel = (int) Math.floor((double) this.BUFFER_SIZE / (double) this.channelCount);
                        int sampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);
                        int numberOfSamplesToGet, numberOfSamplesToGetPerChannel;

                        this.writeMultiChannelSamplesHeader(fileWriter, descriptor, sampleSource);
                        for (int sampleNumber = 0; sampleNumber < sampleCount; sampleNumber += numberOfSamplesToGet) {
                                numberOfSamplesToGetPerChannel = Math.min((sampleCount - sampleNumber), bufferSizePerChannel);
                                numberOfSamplesToGet = numberOfSamplesToGetPerChannel;

                                double[][] dataChunk = this.getMultiChannelSamplesChunk(sampleSource, sampleNumber, numberOfSamplesToGetPerChannel);
                                this.writeMultiChannelSamplesChunk(fileWriter, descriptor, dataChunk, numberOfSamplesToGetPerChannel);

                                if (monitor != null) {
                                    if (monitor.isRequestingAbort())
                                        return;
                                    monitor.setProcessedSampleCount(sampleNumber + numberOfSamplesToGet);
                                }
                        }
                }
	}

	/**
	 * Returns chunk of samples from all channels.
	 * @param sampleNumber starting point (in time) from which on samples are fetched.
         * @param numberOfSamplesToGet number of samples which will be fetched for each channel.
	 * @throws IOException
	 */
        private double[][] getMultiChannelSamplesChunk(MultichannelSampleSource sampleSource, int sampleNumber, int numberOfSamplesToGetPerChannel){
                double[][] samplesChunk = new double[this.channelCount][];
                IntStream.range(0, this.channelCount).forEach(
                        channelNumber -> {
                            samplesChunk[channelNumber] = new double[numberOfSamplesToGetPerChannel];
                            sampleSource.getSamples(channelNumber, samplesChunk[channelNumber], sampleNumber, numberOfSamplesToGetPerChannel, 0);
                        }
                );

                return samplesChunk;
        }

	/**
	 * Exports signal from a multiple channels to file.
	 * @param samplesChunk double[numberOfChannes][numberOfSamplesInChunk] matrix.
         * @param singleChannelChunkSize numberOfSamplesInChunk for a single channel.
	 * @throws IOException
	 */
        private void writeMultiChannelSamplesChunk(FileWriter output, SignalExportDescriptor descriptor, double[][] samplesChunk, int singleChannelChunkSize) throws IOException {
                for (int sampleNumber = 0; sampleNumber < singleChannelChunkSize; sampleNumber++){
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
            if (descriptor.isExportChannelNames()) {
                output.write("#");
                for (int channelNumber = 0; channelNumber < this.channelCount - 1; channelNumber++) {
                    output.write(sampleSource.getLabel(channelNumber) + descriptor.getSeparator());
                }
                output.write(sampleSource.getLabel(this.channelCount - 1) + "\n");
            }
        }
    
}
