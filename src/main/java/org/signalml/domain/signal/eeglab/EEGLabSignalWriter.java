package org.signalml.domain.signal.eeglab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLInt64;
import com.jmatio.types.MLStructure;
import org.signalml.app.document.SignalDocument;
import org.signalml.domain.signal.MultichannelSampleSource;

/**
 * Exports signal to EEGLab format.
 *
 * @author Tomasz Sawicki
 */
public class EEGLabSignalWriter {

        /**
         * Number of samples in the document.
         */
        private int sampleCount;

        /**
         * Number of channels in the document.
         */
        private int channelCount;

        /**
         * Sampling rate.
         */
        private double samplingRate;

        /**
         * Length of the signal in seconds
         */
        private double signalLength;

        /**
         * Reference channel's index.
         */
        private int referenceChannel;

        /**
         * Signal data.
         */
        private double[][] data;
        
        /**
         * Vector of samples times in seconds.
         */
        private double[] samplesTimes;

        /**
         * Constructor.
         *
         * @param signalDocument currently open signal document
         */
	public EEGLabSignalWriter(SignalDocument signalDocument) {

                extractData(signalDocument);
	}

        /**
         * Extracts all needed data from the signal document.
         *
         * @param signalDocument the signal document to extract data from.
         */
        private void extractData(SignalDocument signalDocument) {
                
                MultichannelSampleSource sampleSource = signalDocument.getSampleSource();

                sampleCount = sampleSource.getSampleCount(0);
                channelCount = sampleSource.getChannelCount();
                samplingRate = sampleSource.getSamplingFrequency();
                signalLength = sampleCount / samplingRate;

                for (int i = 0; i < channelCount; i++) {

                        if (sampleSource.getLabel(i).equals("Fz")) {

                                referenceChannel = i;
                                break;
                        }
                }

                data = new double[channelCount][sampleCount];

                for (int i = 0; i < channelCount; i++)
                        sampleSource.getSamples(0, data[i], 0, sampleCount, 0);

                samplesTimes = new double[sampleCount];
                samplesTimes[0] = 0;
                for (int i = 1; i < sampleCount; i++)
                        samplesTimes[i] = i * (1 / samplingRate);
        }

        /**
         * Writes the signal to a file in EEGLab format.
         *
         * @param outputPath output file path
         * @throws IOException when the file cannot be writen
         */
	public void writeSignal(String outputPath) throws IOException {

                File output = new File(outputPath);                
                String filename = output.getName();              

                MLStructure eegStruct = new MLStructure("EEG", new int[]{1, 1});

		eegStruct.setField("setname", new MLChar("setname", filename));
                eegStruct.setField("filename", new MLChar("filename", filename));
                eegStruct.setField("trials", new MLDouble("trials", new double[] { 1 }, 1));
		eegStruct.setField("pnts", new MLDouble("pnts", new double[] { sampleCount }, 1));
		eegStruct.setField("nbchan", new MLDouble("nbchan", new double[]{ channelCount }, 1));
		eegStruct.setField("srate", new MLDouble("srate", new double[]{ samplingRate }, 1));
                eegStruct.setField("xmin", new MLDouble("xmin", new double[]{ 0 }, 1));
                eegStruct.setField("xmax", new MLDouble("xmax", new double[]{ signalLength }, 1));
                eegStruct.setField("times", new MLDouble("times", samplesTimes, 1));
                eegStruct.setField("ref", new MLInt64("ref", new long[]{ referenceChannel }, 1));
                eegStruct.setField("saved", new MLChar("saved", "no"));
                eegStruct.setField("data", new MLDouble("data", data));

                double[] icasphere = new double[channelCount];
                for (double i : icasphere)
                        i = 0;

                eegStruct.setField("icawinv", new MLDouble("icawinv", new double[] { 0 }, 1));
                eegStruct.setField("icaweights", new MLDouble("icaweights", new double[] { 0 }, 1));
                eegStruct.setField("icasphere", new MLDouble("icasphere", icasphere, 1));
						
		ArrayList<MLArray> list = new ArrayList<MLArray>();
		list.add(eegStruct);
		new MatFileWriter(output, list);
	}
}