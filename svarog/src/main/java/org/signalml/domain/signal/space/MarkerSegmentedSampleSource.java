/* MarkerSegmentedSignalSource.java created 2008-01-27
 *
 */

package org.signalml.domain.signal.space;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSegmentedSampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * This class represents the {@link MultichannelSampleSource source} of samples
 * for the neighbourhood of the markers for the selected channels.
 * Contains the number of samples before and after the markers that should be available
 * and subset of channels that can be accessed.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MarkerSegmentedSampleSource extends MultichannelSampleProcessor implements MultichannelSegmentedSampleSource {

	/**
	 * the number of samples in the segment
	 */
	private int segmentLength;
	/**
	 * an array of the beginnings of segments
	 */
	private int[] offsets;

	/**
	 * the number of channels in this source
	 */
	private int channelCount;
	/**
	 * an array mapping indexes in this source to the indexes of channels
	 * in the actual source
	 */
	private int[] channelIndices;

	/**
	 * the number of segments that can not be used (the required
	 * neighbourhood of the marker is not in the signal).
	 */
	private int unusableSegmentCount;
	/**
	 * the number of samples before the marker that should be included in
	 * the segment
	 */
	private int samplesBefore;
	/**
	 * the number of samples after the marker that should be included in
	 * the segment
	 */
	private int samplesAfter;
	/**
	 * the number of samples per second
	 */
	private float samplingFrequency;

	/**
	 * Constructor. Creates a source without segments
	 * @param source the actual source of samples
	 */
	protected MarkerSegmentedSampleSource(MultichannelSampleSource source) {
		super(source);
	}

	public MarkerSegmentedSampleSource(MultichannelSampleSource source, StyledTagSet tagSet, List<String> markerStyleNames,
			double secondsBefore, double secondsAfter, ChannelSpace channelSpace) {
		this(source, null, null, tagSet, markerStyleNames,
				new ArrayList<String>(), secondsBefore, secondsAfter, channelSpace);
	}

	/**
	 * Constructor. Creates the source of samples based on the given
	 * {@link MultichannelSampleSource source} of all channels.
	 * @param source the source of all samples
	 * @param tagSet the set of tags (including markers)
	 * @param markerStyleNames the name of the type of a marker
	 * @param secondsBefore the length (in seconds) before the marker that
	 * is to be included in the
	 * @param secondsAfter the length (in seconds) before the marker that
	 * is to be included in the
	 * @param channelSpace the set of channels
	 */
	public MarkerSegmentedSampleSource(MultichannelSampleSource source,
			Double startAveragingTime, Double stopAveragingTime,
			StyledTagSet tagSet, List<String> markerStyleNames,
			List<String> artifactStyleNames,
			double secondsBefore, double secondsAfter, ChannelSpace channelSpace) {

		super(source);

		if (channelSpace != null) {

			channelCount = channelSpace.size();
			channelIndices = channelSpace.getSelectedChannels();

		} else { // all channels

			channelCount = source.getChannelCount();
			channelIndices = new int[channelCount];
			for (int i=0; i<channelCount; i++) {
				channelIndices[i] = i;
			}

		}

		samplingFrequency = source.getSamplingFrequency();
		samplesBefore = (int) Math.ceil(samplingFrequency * secondsBefore);
		samplesAfter = (int) Math.ceil(samplingFrequency * secondsAfter);
		segmentLength = samplesBefore + samplesAfter;

		List<TagStyle> tagStyles = getTagStyles(markerStyleNames, tagSet);
		List<TagStyle> artifactStyles = getTagStyles(artifactStyleNames, tagSet);

		int minSampleCount = SampleSourceUtils.getMinSampleCount(source);

		int markerSample;
		int averagedCount = 0;

		int[] offsetArr = new int[tagSet.getTagCount()];

		for (Tag tag: tagSet.getTags()) {
			if (tagStyles.contains(tag.getStyle())) {

				markerSample = (int) Math.floor(samplingFrequency * tag.getPosition());

				if (markerSample < samplesBefore) {  // samplesBefore samples from markerSample inclusive
					// not enough samples before
					unusableSegmentCount++;
					continue;
				}
				if (minSampleCount < samplesAfter + markerSample) {  // samplesAfter samples from (markerSample+1) inclusive
					// not enough samples after
					unusableSegmentCount++;
					continue;
				}

				if (startAveragingTime != null && stopAveragingTime != null &&
						(tag.getPosition() < startAveragingTime || tag.getPosition() > stopAveragingTime))
					//we don't use samples from outside the <firstSample, lastSample> range
					continue;

				if (overlapsWithArtifactTag(tagSet, tag, artifactStyles, secondsBefore, secondsAfter))
					continue;

				// sample is ok
				offsetArr[averagedCount] = markerSample - samplesBefore;
				averagedCount++;

			}

		}

		offsets = Arrays.copyOf(offsetArr, averagedCount);

	}

	protected boolean overlapsWithArtifactTag(StyledTagSet tagSet, Tag tag, List<TagStyle> artifactStyles, double secondsBefore, double secondsAfter) {
		SignalSelection averagingSelection = new SignalSelection(SignalSelectionType.CHANNEL, tag.getPosition() - secondsBefore, secondsBefore + secondsAfter);
		for (Tag artTag: tagSet.getTags()) {
			if (artifactStyles.contains(artTag.getStyle())) {
				if (artTag.overlaps(averagingSelection))
					return true;
			}
		}
		return false;
	}

	protected List<TagStyle> getTagStyles(List<String> styleNames, StyledTagSet tagSet) {
		List<TagStyle> tagStyles = new ArrayList<TagStyle>();
		for (String styleName: styleNames) {
			TagStyle markerStyle = tagSet.getStyle(SignalSelectionType.CHANNEL, styleName);
			tagStyles.add(markerStyle);
		}
		return tagStyles;
	}

	/**
	 * Constructor. Creates the source of samples based on the given
	 * {@link MultichannelSampleSource source} of all channels an the given
	 * {@link MarkerSegmentedSampleSourceDescriptor descriptor}.
	 * @param source the source of samples for all channels
	 * @param descriptor the descriptor of this source
	 */
	public MarkerSegmentedSampleSource(MultichannelSampleSource source, MarkerSegmentedSampleSourceDescriptor descriptor) {
		this(source);

		segmentLength = descriptor.getSegmentLength();
		offsets = descriptor.getOffsets();

		channelCount = descriptor.getChannelCount();
		channelIndices = descriptor.getChannelIndices();

		unusableSegmentCount = descriptor.getUnusableSegmentCount();
		samplesBefore = descriptor.getSamplesBefore();
		samplesAfter = descriptor.getSamplesAfter();
	}

	/**
	 * Creates the {@link MarkerSegmentedSampleSourceDescriptor descriptor}
	 * of this source.
	 * @return the descriptor of this source
	 */
	@Override
	public MarkerSegmentedSampleSourceDescriptor createDescriptor() {

		MarkerSegmentedSampleSourceDescriptor descriptor = new MarkerSegmentedSampleSourceDescriptor();

		descriptor.setSegmentLength(segmentLength);
		descriptor.setOffsets(offsets);

		descriptor.setChannelCount(channelCount);
		descriptor.setChannelIndices(channelIndices);

		descriptor.setUnusableSegmentCount(unusableSegmentCount);
		descriptor.setSamplesBefore(samplesBefore);
		descriptor.setSamplesAfter(samplesAfter);

		return descriptor;

	}

	@Override
	public double getSegmentTime(int segment) {
		return ((double) offsets[segment]) / samplingFrequency;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		if (count == 0) {
			return;
		}

		int skippedSegments = signalOffset / segmentLength;
		int skippedSamples = signalOffset % segmentLength;

		int neededCount = count;
		int targetOffset = arrayOffset;

		if (skippedSamples > 0) {

			// get the rest of the first segment affected, if not at segment boundary

			int firstSegmentLeftover = Math.min(segmentLength-skippedSamples, neededCount);

			source.getSamples(channelIndices[channel], target, offsets[skippedSegments], firstSegmentLeftover, targetOffset);

			neededCount -= firstSegmentLeftover;
			targetOffset += firstSegmentLeftover;

			skippedSegments++;

		}

		int wholeSegments = neededCount / segmentLength;

		if (wholeSegments > 0) {

			for (int i=0; i<wholeSegments; i++) {
				source.getSamples(channelIndices[channel], target, offsets[skippedSegments+i], segmentLength, targetOffset);

				neededCount -= segmentLength;
				targetOffset += segmentLength;
			}

		}

		if (neededCount > 0) {

			source.getSamples(channelIndices[channel], target, offsets[skippedSegments+wholeSegments], neededCount, targetOffset);

		}

	}

	@Override
	public int getSegmentCount() {
		return offsets.length;
	}

	@Override
	public int getSegmentLength() {
		return segmentLength;
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		return source.getDocumentChannelIndex(channelIndices[channel]);
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

	@Override
	public int getSampleCount(int channel) {
		return (offsets.length * segmentLength);
	}

	@Override
	public void getSegmentSamples(int channel, double[] target, int segment) {
		source.getSamples(channelIndices[channel], target, offsets[segment], segmentLength, 0);
	}

	@Override
	public int getUnusableSegmentCount() {
		return unusableSegmentCount;
	}

	/**
	 * Returns the number of samples in the segment before the marker.
	 * @return the number of samples in the segment before the marker
	 */
	public int getSamplesBefore() {
		return samplesBefore;
	}

	/**
	 * Returns the number of samples in the segment after the marker.
	 * @return the number of samples in the segment after the marker
	 */
	public int getSamplesAfter() {
		return samplesAfter;
	}

}
