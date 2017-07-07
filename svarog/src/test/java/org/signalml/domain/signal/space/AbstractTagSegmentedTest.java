package org.signalml.domain.signal.space;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.signalml.BaseTestCase;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.RawSignalDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;

public class AbstractTagSegmentedTest extends BaseTestCase {

	protected static final int CHANNEL_COUNT = 3;
	protected static final int SAMPLE_COUNT = 128 * 15;
	protected static final String AVERAGED_TAG_NAME = "averaged";
	protected static final String AVERAGED_TAG_NAME_2 = "averaged2";
	protected static final String ARTIFACT_TAG_NAME = "artifact";
	protected double samplingFrequency = 128.0;

	protected double[][] samples;

	protected File signalFile = new File("epTestSignalFile.bin");

	protected TagStyle averagedTagStyle = new TagStyle(SignalSelectionType.CHANNEL, AVERAGED_TAG_NAME, "", Color.black, Color.BLACK, 1);
	protected TagStyle averagedTagStyle2 = new TagStyle(SignalSelectionType.CHANNEL, AVERAGED_TAG_NAME_2, "", Color.black, Color.BLACK, 1);
	protected TagStyle artifactTagStyle = new TagStyle(SignalSelectionType.CHANNEL, ARTIFACT_TAG_NAME, "", Color.blue, Color.blue, 1);
	protected TagStyle otherTagStyle = new TagStyle(SignalSelectionType.CHANNEL, "not_averaged", "", Color.black, Color.BLACK, 1);

	protected TagStyle[] tagStyles = new TagStyle[] {
			averagedTagStyle, averagedTagStyle2, artifactTagStyle, otherTagStyle
	};

	protected double[][] getSamples() {

		if (samples == null) {
			samples = new double[CHANNEL_COUNT][SAMPLE_COUNT];

			for (int channel = 0; channel < CHANNEL_COUNT; channel++) {
				for (int i = 0; i < SAMPLE_COUNT; i++)
					if (channel == 0)
						samples[channel][i] = (i) / samplingFrequency;
					else
						samples[channel][i] = SAMPLE_COUNT - (i) / samplingFrequency;
			}
		}
		return samples;
	}

	protected SignalDocument getSignalDocument() throws SignalMLException, IOException {
		//write data to file
		DoubleArraySampleSource sampleSource = new DoubleArraySampleSource(getSamples());

		RawSignalWriter writer = new RawSignalWriter();
		writer.writeSignal(signalFile, sampleSource, new SignalExportDescriptor(), null);

		//get signal document
		RawSignalDescriptor descriptor = new RawSignalDescriptor();
		descriptor.setSampleCount(getSamples()[0].length);
		descriptor.setChannelCount(getSamples().length);
		RawSignalDocument signalDocument = new RawSignalDocument(descriptor);
		signalDocument.setBackingFile(signalFile);
		signalDocument.openDocument();

		return signalDocument;
	}

	protected TagDocument getTagDocument() throws SignalMLException, IOException {

		TagDocument tagDocument = new TagDocument();
		for (TagStyle style: tagStyles)
			tagDocument.getTagSet().addStyle(style);

		return tagDocument;
	}

	@After
	public void cleanUp() {
		signalFile.delete();
	}
}
