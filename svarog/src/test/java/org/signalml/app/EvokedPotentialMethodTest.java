package org.signalml.app;

import static org.signalml.SignalMLAssert.assertArrayEquals;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.RawSignalDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.method.ep.EvokedPotentialApplicationData;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.method.ep.EvokedPotentialMethod;
import org.signalml.method.ep.EvokedPotentialParameters;
import org.signalml.method.ep.EvokedPotentialResult;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * Checks if the {@link EvokedPotentialMethod} works as expected.
 *
 * @author Piotr Szachewicz
 */
public class EvokedPotentialMethodTest {

	private static final int CHANNEL_COUNT = 2;
	private static final int SAMPLE_COUNT = 128 * 15;
	private double samplingFrequency = 128.0;

	private double[][] samples;

	protected File signalFile = new File("epTestSignalFile.bin");
	protected TagStyle[] tagStyles = new TagStyle[] {
			new TagStyle(SignalSelectionType.CHANNEL, "averaged", "", Color.black, Color.BLACK, 1),
			new TagStyle(SignalSelectionType.CHANNEL, "not_averaged", "", Color.black, Color.BLACK, 1)
	};

	EvokedPotentialApplicationData data = new EvokedPotentialApplicationData();

	public EvokedPotentialMethodTest() throws Exception {
		data.setSignalDocument(getSignalDocument());
		data.setTagDocument(getTagDocument());
		data.getParameters().setAveragingStartTime(-1.0F);
		data.getParameters().setAveragingTimeLength(2.0F);

		data.getParameters().setBaselineTimeStart(-2.0F);
		data.getParameters().setBaselineTimeLength(1.0F);

		data.getParameters().setFilteringEnabled(false);

		List<TagStyleGroup> group = new ArrayList<TagStyleGroup>();
		group.add(new TagStyleGroup(tagStyles[0].getName()));
		data.getParameters().setAveragedTagStyles(group);
	}

	@Test
	public void testOneTag() throws Exception {

		List<Double> tagPositions = new ArrayList<Double>();
		tagPositions.add(1.0);
		data.getParameters().setBaselineCorrectionEnabled(false);

		performTest(tagPositions);

	}

	@Test
	public void testThreeTags() throws Exception {

		List<Double> tagPositions = new ArrayList<Double>();
		tagPositions.add(2.0);
		tagPositions.add(4.0);
		tagPositions.add(5.0);

		data.getParameters().setBaselineCorrectionEnabled(false);
		performTest(tagPositions);
	}

	@Test
	public void testThreeTagsWithBaseline() throws Exception {

		List<Double> tagPositions = new ArrayList<Double>();
		tagPositions.add(3.0);
		tagPositions.add(5.0);
		tagPositions.add(8.0);

		data.getParameters().setBaselineCorrectionEnabled(true);
		performTest(tagPositions);
	}

	public void performTest(List<Double> tagPositions) throws Exception {

		for (Double tagPosition: tagPositions)
			data.getTagDocument().getTagSet().addTag(new Tag(tagStyles[0], tagPosition, 0.0));
		data.calculate();

		EvokedPotentialMethod method = new EvokedPotentialMethod();
		EvokedPotentialResult result = (EvokedPotentialResult) method.doComputation(data, new DummyMethodExecutionTracker());

		double[][] averagedSamples = result.getAverageSamples().get(0);

		for (int channel = 0; channel < CHANNEL_COUNT; channel++) {
			double[][] samplesTag = new double[tagPositions.size()][getAveragedSamples(0, 1.0).length];
			for (int i = 0; i < tagPositions.size(); i++) {
				samplesTag[i] = getAveragedSamples(channel, tagPositions.get(i));
			}

			double[] expectedAveragedSamples = new double[samplesTag[0].length];
			for (int i = 0; i < expectedAveragedSamples.length; i++) {

				double sum = 0.0;
				for (int j = 0; j < samplesTag.length; j++)
					sum += samplesTag[j][i];
				expectedAveragedSamples[i] = sum / samplesTag.length;
			}

			if (data.getParameters().isBaselineCorrectionEnabled()) {
				performBaselineCorrection(channel, tagPositions, expectedAveragedSamples);
			}

			assertArrayEquals(averagedSamples[channel], expectedAveragedSamples, 1e-3);
		}
	}

	protected void performBaselineCorrection(int channel, List<Double> tagPositions, double[] samples) {
		double baseline = 0.0;
		int number = 0;
		for (int i = 0; i < tagPositions.size(); i++) {
			double[] baselineSamples = getBaselineSamples(channel, tagPositions.get(i));
			for (double sample: baselineSamples) {
				baseline += sample;
				number++;
			}
		}
		baseline /= number;

		for (int i = 0; i < samples.length; i++) {
			samples[i] -= baseline;
		}
	}

	protected double[][] getSamples() {

		if (samples == null) {
			samples = new double[CHANNEL_COUNT][SAMPLE_COUNT];

			for (int channel = 0; channel < 2; channel++) {
				for (int i = 0; i < SAMPLE_COUNT; i++)
					if (channel == 0)
						samples[channel][i] = ((float) i) / samplingFrequency;
					else
						samples[channel][i] = SAMPLE_COUNT - ((float) i) / samplingFrequency;
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

	public double[] getSamples(int channel, double markerPosition, double startTime, double lengthInSeconds) {
		int startSample = (int) ((markerPosition + startTime) * samplingFrequency);
		int numberOfSamples = (int) (lengthInSeconds * samplingFrequency);

		double[] sampleChunk = new double[numberOfSamples];
		for (int i = 0; i < sampleChunk.length; i++) {
			sampleChunk[i] = samples[channel][startSample + i];
		}
		return sampleChunk;
	}

	public double[] getAveragedSamples(int channel, double markerPosition) {
		EvokedPotentialParameters parameters = data.getParameters();
		return getSamples(channel, markerPosition, parameters.getAveragingStartTime(), parameters.getAveragingTimeLength());
	}

	public double[] getBaselineSamples(int channel, double startPosition) {
		EvokedPotentialParameters parameters = data.getParameters();
		return getSamples(channel, startPosition, parameters.getBaselineTimeStart(), parameters.getBaselineTimeLength());
	}

	@After
	public void cleanUp() {
		signalFile.delete();
	}
}
