package org.signalml.domain.signal.eeglab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLInt64;
import com.jmatio.types.MLStructure;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.plugin.export.signal.Tag;

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
        private SortedSet<Tag> allTags;
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
                allTags=extractTags(signalDocument);
        }
        /**
         * Extracts all the tags from signal document
         * @param signalDocument the signal document which has tags
         * @return SortedSet<Tag> all tags from this signal document
         * @author Maciej Pawlisz
         */
        private SortedSet<Tag> extractTags(SignalDocument signalDocument) {
        	List<TagDocument> tagDocuments = signalDocument.getTagDocuments();
        	SortedSet<Tag> tags=new TreeSet<Tag>();
        	for (int d = 0;d < tagDocuments.size();d++)
        		tags.addAll(tagDocuments.get(d).getTagSet().getTags());
        	return tags;
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
                eegStruct.setField("event",getEventStruct());
                eegStruct.setField("icawinv", new MLDouble("icawinv", new double[][] { { } } ));
                eegStruct.setField("icaweights", new MLDouble("icaweights", new double[][] { { } } ));
                eegStruct.setField("icasphere", new MLDouble("icasphere", new double[][] { { } } ));

		ArrayList<MLArray> list = new ArrayList<MLArray>();
		list.add(eegStruct);
		new MatFileWriter(output, list);
	}
	/** 
	 * Returns event EEGLab event structure 
	 * 
	 * @return MLStructure event structure
	 * @author Maciej Pawlisz
	 */
	private MLStructure getEventStruct() {
		MLStructure eventStruct = new MLStructure("event", new int[] { 1,
				allTags.size() });
		int i = 0;
		for (Iterator<Tag> it = allTags.iterator(); it.hasNext();) {
			Tag tag = it.next();
			eventStruct.setField("type", 
					new MLChar("type", tag.getStyle().getName()), 0, i);
			eventStruct.setField("latency", 
							new MLDouble("latency",
									new double[] { tag.getPosition() * samplingRate },1),
									0, i);
			eventStruct.setField("duration", new MLDouble("duration",
					new double[] { tag.getLength() * samplingRate }, 1), 0, i);
			eventStruct.setField("tag_type", new MLChar("tag_type",tag.getType().getName()),0,i);
			eventStruct.setField("channel", new MLInt64("channel", new long[]{tag.getChannel()},1),0,i);
			i++;
		}
		return eventStruct;
	}
}