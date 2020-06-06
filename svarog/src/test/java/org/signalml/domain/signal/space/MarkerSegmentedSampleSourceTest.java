package org.signalml.domain.signal.space;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.signalml.SignalMLAssert.assertArrayEquals;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

public class MarkerSegmentedSampleSourceTest extends AbstractTagSegmentedTest {

	private double samplingInterval = 1.0/samplingFrequency;

	@Test
	public void testGetSamplesWithZeroTags() {

		MarkerSegmentedSampleSource markerSampleSource = new MarkerSegmentedSampleSource(
				getSampleSource(), getTagSet(), getMarkerStyleNames(), 0.0, 1.0, null);

		assertEquals(0, markerSampleSource.getSegmentCount());
	}

	@Test
	public void testGetSamplesWithOneTag() {

		testGetSamplesWithOneTag(0.0, 0.0, null);
		testGetSamplesWithOneTag(2.0, -1.0, null);
		testGetSamplesWithOneTag(0.0, 1.0, null);
		testGetSamplesWithOneTag(-1.0, 3.0, null);

		testGetSamplesWithOneTag(0.0, 0.0, new ChannelSpace(new int[] {0, 1, 2}));
		testGetSamplesWithOneTag(0.0, 0.0, new ChannelSpace(new int[] {0, 2}));
		testGetSamplesWithOneTag(2.0, -1.0, new ChannelSpace(new int[] {1, 2}));
		testGetSamplesWithOneTag(2.0, -1.0, new ChannelSpace(new int[] {2}));
		testGetSamplesWithOneTag(0.0, 1.0, new ChannelSpace(new int[] {1}));
		testGetSamplesWithOneTag(-1.0, 3.0, new ChannelSpace(new int[] {}));
	}

	public void testGetSamplesWithOneTag(double tagPosition, double startTime, ChannelSpace channelSpace) {

		StyledTagSet tagSet = getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, tagPosition, 2.0));

		MarkerSegmentedSampleSource markerSampleSource = new MarkerSegmentedSampleSource(
				getSampleSource(), tagSet, getMarkerStyleNames(), startTime, 1.0, channelSpace);

		int segmentLengthInSamples = markerSampleSource.getSegmentLengthInSamples();

		assertEquals(1, markerSampleSource.getSegmentCount());
		assertEquals(1.0 * samplingFrequency, segmentLengthInSamples, 1e-10);

		int count = channelSpace == null ? CHANNEL_COUNT : channelSpace.size();
		double[] actualSegmentSamples = new double[segmentLengthInSamples];
		for (int i = 0; i < count; i++) {
			int channel = channelSpace == null ? i : channelSpace.getSelectedChannels()[i];
			markerSampleSource.getSegmentSamples(i, actualSegmentSamples, 0);

			double[] expectedSegmentSamples = new double[segmentLengthInSamples];
			System.arraycopy(getSamples()[channel], (int) ((tagPosition+startTime)*samplingFrequency), expectedSegmentSamples, 0, segmentLengthInSamples);

			assertArrayEquals(expectedSegmentSamples, actualSegmentSamples, 1e-5);
		}
	}

	@Test
	public void testGetSamplesWithOneTagBeforeTheSignal() {

		//inside the signal
		testTagCountWithOneTag(1.0, null, null, -0.5, 1.0, 1);

		//before the signal
		testTagCountWithOneTag(-1.0, null, null, 0.0, 1.0, 0);

		//start time is before the signal
		testTagCountWithOneTag(0.0, null, null, -samplingInterval, 1.0, 0);

		//tag before startMarkerSelection
		testTagCountWithOneTag(0.0, 1.0, 3.0, 0.0, 1.0, 0);

		//tag before startMarkerSelection
		testTagCountWithOneTag(1.0-samplingInterval, 1.0, 3.0, 0.0, 1.0, 0);

		//tag just at the startMarkerSelection
		testTagCountWithOneTag(1.0, 1.0, 3.0, 0.0, 1.0, 1);
	}

	@Test
	public void testGetSamplesWithOneTagAfterTheSignal() {

		//after the signal
		testTagCountWithOneTag(SAMPLE_COUNT/samplingFrequency, null, null, 0.0, 1.0, 0);

		//just before the end of the signal
		testTagCountWithOneTag(SAMPLE_COUNT/samplingFrequency-1.0, null, null, 0.0, 1.0, 1);

		//too long length
		testTagCountWithOneTag(SAMPLE_COUNT/samplingFrequency-1.0, null, null, -1.0, 2.0+samplingInterval, 0);

		//inside
		testTagCountWithOneTag(2.0, 1.0, 3.0, -1.0, 2.0, 1);

		//length too long
		testTagCountWithOneTag(2.0, 1.0, 3.0, -1.0, 2.0+samplingInterval, 0);

		//whole tag after the endMarkerSelection
		testTagCountWithOneTag(3.1, 1.0, 3.0, 0.0, 0.9, 0);

		//the end of the tag after endMarkerSelection
		testTagCountWithOneTag(3.0, 1.0, 3.0, -0.5, 1.0, 0);
	}

	@Test
	public void testSegmentCountWithArtifactTags() {
		testTagCountWithOneTag(1.0, 3.0, 1.0, null, null, 0.0, 1.0, 1);
		testTagCountWithOneTag(0.0, 1.0, 1.0, null, null, 0.0, 1.0, 1);
		testTagCountWithOneTag(1.0, 2.0-samplingInterval, 1.0, null, null, 0.0, 1.0, 0);

		testTagCountWithOneTag(1.0, 1.0-samplingInterval, 1.0, null, null, 0.0, 1.0, 0);
		testTagCountWithOneTag(1.0, 1.0-samplingInterval, 2.0, null, null, 0.0, 1.0, 0);
		testTagCountWithOneTag(1.0, 1.1, 0.1, null, null, 0.0, 1.0, 0);
		testTagCountWithOneTag(1.0, 0.1, 0.1, null, null, 0.0, 1.0, 1);

		testTagCountWithOneTag(2.0, 0.1, 0.1, null, null, -1.0, 1.5, 1);
		testTagCountWithOneTag(2.0, 0.0, 1.0, null, null, -1.0, 1.5, 1);
		testTagCountWithOneTag(2.0, 2.5, 1.0, null, null, -1.0, 1.5, 1);
		testTagCountWithOneTag(2.0, 2.5-samplingInterval, 1.0, null, null, -1.0, 1.5, 0);
		testTagCountWithOneTag(2.0, 1.5, 0.7, null, null, -1.0, 1.5, 0);
		testTagCountWithOneTag(2.0, 1.0, 1.5, null, null, -1.0, 1.5, 0);
		testTagCountWithOneTag(2.0, 1.0-samplingInterval, 1.0, null, null, -1.0, 1.5, 0);
		testTagCountWithOneTag(2.0, 1.0-samplingInterval, 3.0, null, null, -1.0, 1.5, 0);

	}

	protected void testTagCountWithOneTag(double tagPosition, Double startMarkerTime,
			Double endMarkerTime, double startTime, double length, int expectedCount) {
		testTagCountWithOneTag(tagPosition, null, null, startMarkerTime, endMarkerTime, startTime, length, expectedCount);
	}

	protected void testTagCountWithOneTag(double tagPosition, Double artifactTagPosition, Double artifactTagLength, Double startMarkerTime,
			Double endMarkerTime, double startTime, double length, int expectedCount) {
		StyledTagSet tagSet = getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, tagPosition, 0.1));
		tagSet.addTag(new Tag(otherTagStyle, 1.0, 1.0));
		tagSet.addTag(new Tag(otherTagStyle, 3.0, 1.0));

		List<String> artifactStyleNames = new ArrayList<>();
		if (artifactTagPosition != null && artifactTagLength != null) {
			tagSet.addTag(new Tag(artifactTagStyle, artifactTagPosition, artifactTagLength));
			artifactStyleNames = getArtifactStyleNames();
		}

		MarkerSegmentedSampleSource markerSampleSource = new MarkerSegmentedSampleSource(
			getSampleSource(), startMarkerTime, endMarkerTime, tagSet, getMarkerStyleNames(), artifactStyleNames,
			startTime, length, null);

		assertEquals(expectedCount, markerSampleSource.getSegmentCount());
	}

	@Test
	public void testTwoTagsGrouped() {
		StyledTagSet tagSet = getTagSet();
		tagSet.addTag(new Tag(averagedTagStyle, 1.0, 0.1));
		tagSet.addTag(new Tag(averagedTagStyle, 2.0, 0.1));
		tagSet.addTag(new Tag(otherTagStyle, 1.0, 1.0));
		tagSet.addTag(new Tag(otherTagStyle, 3.0, 1.0));

		List<String> markerStyleNames = new ArrayList<>();
		markerStyleNames.add(AVERAGED_TAG_NAME);
		markerStyleNames.add(AVERAGED_TAG_NAME_2);

		MarkerSegmentedSampleSource markerSampleSource = new MarkerSegmentedSampleSource(
			getSampleSource(), null, null, tagSet, markerStyleNames, new ArrayList<>(),
			0.0, 1.0, null);

		assertEquals(2, markerSampleSource.getSegmentCount());
	}

	protected List<String> getMarkerStyleNames() {
		List<String> markerStyleNames = new ArrayList<>();
		markerStyleNames.add(AVERAGED_TAG_NAME);

		return markerStyleNames;
	}

	protected List<String> getArtifactStyleNames() {
		List<String> markerStyleNames = new ArrayList<>();
		markerStyleNames.add(ARTIFACT_TAG_NAME);

		return markerStyleNames;
	}

	protected MultichannelSampleSource getSampleSource() {
		DoubleArraySampleSource doubleArraySampleSource = new DoubleArraySampleSource(getSamples());
		doubleArraySampleSource.setSamplingFrequency((float) samplingFrequency);
		return doubleArraySampleSource;
	}

	protected StyledTagSet getTagSet() {
		StyledTagSet tagSet = new StyledTagSet();
		for (TagStyle style: tagStyles)
			tagSet.addStyle(style);
		return tagSet;
	}

}
