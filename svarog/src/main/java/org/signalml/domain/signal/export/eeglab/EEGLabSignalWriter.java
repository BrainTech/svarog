package org.signalml.domain.signal.export.eeglab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.export.ISignalWriter;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.util.matfiles.CompressedMatlabFileWriter;
import org.signalml.util.matfiles.MatlabFileWriter;
import org.signalml.util.matfiles.array.AbstractArray;
import org.signalml.util.matfiles.array.CharacterArray;
import org.signalml.util.matfiles.array.DoubleArray;
import org.signalml.util.matfiles.array.IntegerArray;
import org.signalml.util.matfiles.array.lazy.LazyExportDoubleArray;
import org.signalml.util.matfiles.structure.Structure;

/**
 * Exports signal to EEGLab format.
 *
 * @author Tomasz Sawicki
 */
public class EEGLabSignalWriter implements ISignalWriter {

	private SortedSet<Tag> allTags;

	private SignalDocument signalDocument;

	public void setSignalDocument(SignalDocument signalDocument) {
		this.signalDocument = signalDocument;
	}

	/**
	 * Extracts all the tags from signal document
	 *
	 * @param signalDocument
	 *            the signal document which has tags
	 * @return SortedSet<Tag> all tags from this signal document
	 * @author Maciej Pawlisz
	 */
	private SortedSet<Tag> extractTags(SignalDocument signalDocument) {
		List<TagDocument> tagDocuments = signalDocument.getTagDocuments();
		SortedSet<Tag> tags = new TreeSet<Tag>();
		for (int d = 0; d < tagDocuments.size(); d++)
			tags.addAll(tagDocuments.get(d).getTagSet().getTags());
		return tags;
	}

	/**
	 * Writes the signal to a file in EEGLab format.
	 *
	 * @param outputPath
	 *            output file path
	 * @throws IOException
	 *             when the file cannot be writen
	 */
	public void writeSignal(File output, MultichannelSampleSource sampleSource, SignalExportDescriptor descriptor, SignalWriterMonitor monitor) throws IOException {

		int channelCount = sampleSource.getChannelCount();
		int sampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);
		String[] channelNames = new String[channelCount];

		int referenceChannel = 0;
		for (int i = 0; i < channelCount; i++) {
			channelNames[i] = sampleSource.getLabel(i);
			if (sampleSource.getLabel(i).equals("Fz")) {
				referenceChannel = i;
			}
		}
		double samplingRate = sampleSource.getSamplingFrequency();

		Structure eegStruct = new Structure("EEG");
		String filename = output.getName();
		eegStruct.setField("setname", new CharacterArray("setname", filename.substring(0, filename.lastIndexOf("."))));
		eegStruct.setField("filename", new CharacterArray("filename", filename));
		eegStruct.setField("trials", new DoubleArray("trials", new double[] { 1.0 }));
		eegStruct.setField("pnts", new DoubleArray("pnts", new double[] { (double) sampleCount }));
		eegStruct.setField("nbchan", new DoubleArray("nbchan", new double[] { (double) channelCount }));
		eegStruct.setField("srate", new DoubleArray("srate", new double[] { samplingRate }));
		eegStruct.setField("xmin", new DoubleArray("xmin", new double[] { 0.0 }));
		eegStruct.setField("xmax", new DoubleArray("xmax", new double[] { sampleCount / samplingRate }));
		eegStruct.setField("ref", new IntegerArray("ref", new Integer[] { referenceChannel }));
		eegStruct.setField("saved", new CharacterArray("saved", "no"));

		eegStruct.setField("icawinv", new DoubleArray("icawinv", new Double[][] { {} }));
		eegStruct.setField("icaweights", new DoubleArray("icaweights", new Double[][] { {} }));
		eegStruct.setField("icasphere", new DoubleArray("icasphere", new Double[][] { {} }));
		eegStruct.setField("icaact", new DoubleArray("icaact", new Double[][] { {} }));

		List<String> keys = new ArrayList<String>();
		List<AbstractArray> arrays = new ArrayList<AbstractArray>();

		keys.add("labels");
		for (int i = 0; i < channelCount; i++) {
			arrays.add(new CharacterArray("labels", channelNames[i]));
		}
		Structure chanlocs = new Structure("chanlocs");
		chanlocs.setFieldsForStructureArray(keys, arrays);
		eegStruct.setField("chanlocs", chanlocs);

		if (signalDocument != null && descriptor.isExportTags())
			eegStruct.setField("event", getEventStruct(samplingRate));

		LazySampleProvider lazySampleProvider = new LazySampleProvider(sampleSource);
		lazySampleProvider.setSignalWriterMonitor(monitor);
		eegStruct.setField("data", new LazyExportDoubleArray("data", lazySampleProvider));
		MatlabFileWriter writer = new CompressedMatlabFileWriter(output);
		writer.addElement(eegStruct);
		writer.write();

		if (monitor != null) {
			monitor.setProcessedSampleCount(sampleCount);
		}
	}

	/**
	 * Returns event EEGLab event structure
	 *
	 * @return MLStructure event structure
	 * @author Maciej Pawlisz
	 */
	private Structure getEventStruct(double samplingRate) {
		allTags = extractTags(signalDocument);

		List<String> keys = new ArrayList<String>();
		keys.add("type");
		keys.add("latency");
		keys.add("duration");
		keys.add("tag_type");
		keys.add("channel");

		List<AbstractArray> arrays = new ArrayList<AbstractArray>();

		for (Iterator<Tag> it = allTags.iterator(); it.hasNext();) {
			Tag tag = it.next();
			arrays.add(new CharacterArray("type", tag.getStyle().getName()));
			arrays.add(new DoubleArray("latency", new double[] { tag.getPosition() * samplingRate }));
			arrays.add(new DoubleArray("duration", new double[] { tag.getLength() * samplingRate }));
			arrays.add(new CharacterArray("tag_type", tag.getType().getName()));
			arrays.add(new IntegerArray("channel", new Integer[] { tag.getChannel() }));
		}

		Structure eventStruct = new Structure("event");
		eventStruct.setFieldsForStructureArray(keys, arrays);
		return eventStruct;
	}

}