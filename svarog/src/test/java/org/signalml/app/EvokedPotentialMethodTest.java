package org.signalml.app;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.signalml.SignalMLAssert.assertArrayEquals;
import org.signalml.app.method.ep.EvokedPotentialApplicationData;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import org.signalml.domain.signal.space.AbstractTagSegmentedTest;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.method.ep.EvokedPotentialMethod;
import org.signalml.method.ep.EvokedPotentialParameters;
import org.signalml.method.ep.EvokedPotentialResult;
import org.signalml.plugin.export.signal.Tag;

/**
 * Checks if the {@link EvokedPotentialMethod} works as expected.
 *
 * @author Piotr Szachewicz
 */
public class EvokedPotentialMethodTest extends AbstractTagSegmentedTest {

	EvokedPotentialApplicationData data = new EvokedPotentialApplicationData();

	public EvokedPotentialMethodTest() throws Exception {
		data.setSignalDocument(getSignalDocument());
		data.setTagDocument(getTagDocument());
		data.getParameters().setAveragingStartTime(-1.0F);
		data.getParameters().setAveragingTimeLength(2.0F);

		data.getParameters().setBaselineTimeStart(-2.0F);
		data.getParameters().setBaselineTimeLength(1.0F);

		data.getParameters().setFilteringEnabled(false);

		List<TagStyleGroup> group = new ArrayList<>();
		group.add(new TagStyleGroup(tagStyles[0].getName()));
		data.getParameters().setAveragedTagStyles(group);
	}

	@Test
	public void testZeroTags() throws Exception {

		data.getParameters().setBaselineCorrectionEnabled(false);

		performTest();
	}

	@Test
	public void testOneTag() throws Exception {

		StyledTagSet tagSet = data.getTagDocument().getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, 1.0, 0.0));
		data.getParameters().setBaselineCorrectionEnabled(false);

		performTest();

	}

	@Test
	public void testThreeTags() throws Exception {

		StyledTagSet tagSet = data.getTagDocument().getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, 2.0, 0.0));
		tagSet.addTag(new Tag(averagedTagStyle, 4.0, 0.0));
		tagSet.addTag(new Tag(averagedTagStyle, 5.0, 0.0));

		data.getParameters().setBaselineCorrectionEnabled(false);
		performTest();
	}

	@Test
	public void testThreeTagsWithBaseline() throws Exception {

		StyledTagSet tagSet = data.getTagDocument().getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, 3.0, 0.0));
		tagSet.addTag(new Tag(averagedTagStyle, 5.0, 0.0));
		tagSet.addTag(new Tag(averagedTagStyle, 8.0, 0.0));

		data.getParameters().setBaselineCorrectionEnabled(true);
		performTest();
	}

	@Test
	public void testTagOutsideTheSignal() throws Exception {

		StyledTagSet tagSet = data.getTagDocument().getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, -1.0, 0.0));

		data.getParameters().setBaselineCorrectionEnabled(true);
		performTest();
	}

	@Test
	public void testTagOutsideTheSignalAndTheOtherInside() throws Exception {

		StyledTagSet tagSet = data.getTagDocument().getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, 10000.0, 0.0));
		tagSet.addTag(new Tag(averagedTagStyle, 2.0, 0.0));

		data.getParameters().setBaselineCorrectionEnabled(true);
		performTest();
	}

	@Test
	public void testOneTagBaselineOutside() throws Exception {

		StyledTagSet tagSet = data.getTagDocument().getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, 1.0, 0.0));

		data.getParameters().setBaselineCorrectionEnabled(true);
		performTest();
	}

	@Test
	public void testOneTagBaselineOutside2() throws Exception {

		StyledTagSet tagSet = data.getTagDocument().getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, 1000.0, 0.0));

		data.getParameters().setBaselineCorrectionEnabled(true);
		performTest();
	}

	@Test
	public void testALotOfTags() throws Exception {

		data.getParameters().getAveragedTagStyles().add(new TagStyleGroup(AVERAGED_TAG_NAME_2));

		StyledTagSet tagSet = data.getTagDocument().getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, 10000.0, 0.0));
		tagSet.addTag(new Tag(averagedTagStyle, 2.0, 0.0));
		tagSet.addTag(new Tag(averagedTagStyle2, 30.0, 0.0));
		tagSet.addTag(new Tag(otherTagStyle, 4.0, 0.0));
		tagSet.addTag(new Tag(otherTagStyle, 7.0, 0.0));
		tagSet.addTag(new Tag(otherTagStyle, -1.0, 0.0));
		tagSet.addTag(new Tag(artifactTagStyle, 11.0, 0.0));

		data.getParameters().setBaselineCorrectionEnabled(true);
		performTest();
	}

	public void performTest() throws Exception {

		List<Double> tagPositions = new ArrayList<>();
		for (Tag tag: data.getTagDocument().getTagSet().getTags()) {
			if ((tag.getStyle() == averagedTagStyle || tag.getStyle() == averagedTagStyle2)
					&& tag.getPosition() >= 0.0 &&
					tag.getPosition() * samplingFrequency <= data.getSignalDocument().getSampleSource().getSampleCount(0))
				tagPositions.add(tag.getPosition());
		}

		data.calculate();

		EvokedPotentialMethod method = new EvokedPotentialMethod();
		EvokedPotentialResult result = (EvokedPotentialResult) method.doComputation(data, new DummyMethodExecutionTracker());

		double[][] averagedSamples = result.getAverageSamples().get(0);

		int avgLength = getAveragedSamples(0, 1.0).length;
		for (int channel = 0; channel < CHANNEL_COUNT; channel++) {
			double[][] samplesTag = new double[tagPositions.size()][avgLength];
			for (int i = 0; i < tagPositions.size(); i++) {
				samplesTag[i] = getAveragedSamples(channel, tagPositions.get(i));
			}

			double[] expectedAveragedSamples = new double[avgLength];
			for (int i = 0; i < expectedAveragedSamples.length; i++) {

				double sum = 0.0;
				for (double[] x : samplesTag) {
					sum += x[i];
				}
				if (samplesTag.length > 0)
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
		if (number > 0)
			baseline /= number;

		for (int i = 0; i < samples.length; i++) {
			samples[i] -= baseline;
		}
	}

	public double[] getSamples(int channel, double markerPosition, double startTime, double lengthInSeconds) {
		int startSample = (int) ((markerPosition + startTime) * samplingFrequency);

		int numberOfSamples = (int) (lengthInSeconds * samplingFrequency);
		if (startSample < 0 || startSample + numberOfSamples > samples[0].length)
			numberOfSamples = 0;

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

}
