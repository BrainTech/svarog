package org.signalml.app.worker;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.signalml.app.model.tag.SlopeType;
import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.math.ArrayOperations;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

public class SynchronizeTagsWithTriggerWorkerTest {

	private SynchronizeTagsWithTriggerParameters parameters;

	private final int sampleCount = 3000;
	private final int[] ascendingSlopes = new int[] { 10, 1034, 1500, 2523, 2550, 2600, 2800, 2901 };
	private final int[] descendingSlopes = new int[] { 15, 1400, 1600, 2524, 2551, 2700, 2900, 2950 };

	public SynchronizeTagsWithTriggerWorkerTest() {
		parameters = new SynchronizeTagsWithTriggerParameters();

		parameters.setSampleSource(createSampleSource());
		parameters.setTagSet(createTagSet());
		parameters.setThresholdValue(1.0);
		parameters.setTriggerChannel(0);

	}

	protected MultichannelSampleSource createSampleSource() {

		double[][] samples = new double[2][sampleCount];

		for (int i = 0; i < ascendingSlopes.length; i++) {
			ArrayOperations.fillArrayWithValue(samples[0], 1.0, ascendingSlopes[i], descendingSlopes[i]);
		}
		for (int i = 0; i < ascendingSlopes.length; i++) {
			samples[1][i] = Math.random();
		}

		DoubleArraySampleSource sampleSource = new DoubleArraySampleSource(samples);
		sampleSource.setSamplingFrequency(128.0F);

		return sampleSource;

	}

	protected StyledTagSet createTagSet() {

		StyledTagSet tagSet = new StyledTagSet();
		TagStyle tagStyle = new TagStyle(SignalSelectionType.CHANNEL, "style", "", Color.black, Color.blue, 1.0F);
		tagSet.addStyle(tagStyle);
		tagSet.addTag(new Tag(tagStyle, 1.0, 0.1));
		tagSet.addTag(new Tag(tagStyle, 2.0, 0.1));
		tagSet.addTag(new Tag(tagStyle, 4.0, 0.1));
		tagSet.addTag(new Tag(tagStyle, 5.0, 0.1));
		tagSet.addTag(new Tag(tagStyle, 6.0, 0.1));

		return tagSet;

	}

	public void testSlope(SlopeType slopeType, int[] expectedPositions) throws InterruptedException, ExecutionException {
		parameters.setSlopeType(slopeType);

		SynchronizeTagsWithTriggerWorker worker = new SynchronizeTagsWithTriggerWorker(parameters);
		worker.execute();
		worker.get();

		StyledTagSet tagSet = parameters.getTagSet();
		int i = 0;
		for (Tag tag: tagSet.getTags()) {
			double position = tag.getPosition() * parameters.getSampleSource().getSamplingFrequency();
			assertEquals(expectedPositions[i], position, 1e-5);
			i++;
		}
	}

	@Test
	public void testSynchronizeAscendingSlope() throws InterruptedException, ExecutionException {
		testSlope(SlopeType.ASCENDING, ascendingSlopes);
	}

	@Test
	public void testSynchronizeDescendingSlope() throws InterruptedException, ExecutionException {
		testSlope(SlopeType.DESCENDING, descendingSlopes);
	}

	@Test
	public void testSynchronizeToBothSlopes() throws InterruptedException, ExecutionException {
		int[] slopePositions = new int[ascendingSlopes.length*2];

		for (int i = 0; i < ascendingSlopes.length; i++) {
			slopePositions[2*i] = ascendingSlopes[i];
			slopePositions[2*i + 1] = descendingSlopes[i];
		}

		testSlope(SlopeType.BOTH, slopePositions);
	}

}
