/* MarkerSegmentedSignalSource.java created 2008-01-27
 *
 */

package org.signalml.domain.signal.space;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.montage.system.IChannelFunction;
import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.SignalProcessingChain;
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
	 * Number of segments which were rejected because artifact tags were found in them.
	 */
	private int artifactRejectedSegmentsCount;
	/**
	 * the first sample relative to the marker that should be included in
	 * the segment
	 */
	private int startSample;
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
			double startTime, double segmentLength, ChannelSpace channelSpace) {
		this(source, null, null, tagSet, markerStyleNames,
				new ArrayList<String>(), startTime, segmentLength, channelSpace);
	}

	/**
	 * Constructor. Creates the source of samples based on the given
	 * {@link MultichannelSampleSource source} of all channels.
	 * @param source the source of all samples
	 * @param tagSet the set of tags (including markers)
	 * @param markerStyleNames the name of the type of a marker
	 * @param startTime the segment start time relative to the marker position
	 * (e.g. if the startTime is equal to 2, each segments begins with a sample
	 * that is 2 seconds after the selected markers).
	 * @param length the length of each segment
	 * @param channelSpace the set of channels
	 */
	public MarkerSegmentedSampleSource(MultichannelSampleSource source,
			Double startMarkerSelection, Double endMarkerSelection,
			StyledTagSet tagSet, List<String> markerStyleNames,
			List<String> artifactStyleNames,
			double startTime, double length, ChannelSpace channelSpace) {

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
		startSample = convertTimeToSamples(startTime);
		this.segmentLength = convertTimeToSamples(length);

		List<TagStyle> tagStyles = getTagStyles(markerStyleNames, tagSet);
		List<TagStyle> artifactStyles = getTagStyles(artifactStyleNames, tagSet);

		int minSampleCount = SampleSourceUtils.getMinSampleCount(source);
		int startMarkerSelectionInSamples = startMarkerSelection == null ? 0 : convertTimeToSamples(startMarkerSelection);
		int endMarkerSelectionInSamples = endMarkerSelection == null ? minSampleCount : convertTimeToSamples(endMarkerSelection);

		int markerSample;
		int averagedCount = 0;

		int[] offsetArr = new int[tagSet.getTagCount()];

		for (Tag tag: tagSet.getTags()) {
			if (tagStyles.contains(tag.getStyle())) {

				markerSample = (int) Math.floor(samplingFrequency * tag.getPosition());

				if (markerSample + startSample < startMarkerSelectionInSamples) {
					// not enough samples before
					unusableSegmentCount++;
					continue;
				}
				if (endMarkerSelectionInSamples < markerSample + startSample + segmentLength) {
					// not enough samples after
					unusableSegmentCount++;
					continue;
				}

				if (overlapsWithArtifactTag(tagSet, tag, artifactStyles, startTime, length)) {
					artifactRejectedSegmentsCount++;
					continue;
				}

				// sample is ok
				offsetArr[averagedCount] = markerSample + startSample;
				averagedCount++;

			}

		}

		offsets = Arrays.copyOf(offsetArr, averagedCount);

	}

	protected boolean overlapsWithArtifactTag(StyledTagSet tagSet, Tag tag, List<TagStyle> artifactStyles, double startTime, double segmentLength) {
		SignalSelection averagingSelection = new SignalSelection(SignalSelectionType.CHANNEL, tag.getPosition() + startTime, segmentLength);
		for (Tag artTag: tagSet.getTags()) {
			if (artifactStyles.contains(artTag.getStyle())) {
				if (artTag.overlaps(averagingSelection))
					return true;
			}
		}
		return false;
	}

	protected List<TagStyle> getTagStyles(List<String> styleNames, StyledTagSet tagSet) {
		List<TagStyle> tagStyles = new ArrayList<>();
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
		startSample = descriptor.getStartTime();
		segmentLength = descriptor.getSegmentLength();
	}

	/**
	 * Creates the {@link MarkerSegmentedSampleSourceDescriptor descriptor}
	 * of this source.
	 * @return the descriptor of this source
	 */
	@Override
	public MarkerSegmentedSampleSourceDescriptor createDescriptor() {

		MarkerSegmentedSampleSourceDescriptor descriptor = new MarkerSegmentedSampleSourceDescriptor();

		descriptor.setStartTime(startSample);
		descriptor.setSegmentLength(segmentLength);
		descriptor.setOffsets(offsets);

		descriptor.setChannelCount(channelCount);
		descriptor.setChannelIndices(channelIndices);

		descriptor.setUnusableSegmentCount(unusableSegmentCount);

		return descriptor;

	}

	@Override
	public double getSegmentTime(int segment) {
		return ((double) offsets[segment]) / samplingFrequency;
	}

	@Override
	public long getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		if (count == 0) {
			return 0;
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

		return 0;
	}

	@Override
	public int getSegmentCount() {
		return offsets.length;
	}

	/**
	 * Returns the segment length in samples
	 */
	public int getSegmentLengthInSamples() {
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

	public int getArtifactRejectedSegmentsCount() {
		return artifactRejectedSegmentsCount;
	}

	@Override
	public String getLabel(int channel) {
		return super.getLabel(channelIndices[channel]);
	}

	public IChannelFunction getChannelFunction(int channel) {
		if (getSource() instanceof SignalProcessingChain) {
			SignalProcessingChain chain = (SignalProcessingChain) getSource();
			if (chain.getMontage() == null)
				return null;
			else
				return chain.getMontage().getCurrentMontage().getSourceChannelFunctionAt(channelIndices[channel]);
		} else {
			return null;
		}

	}

	protected int convertTimeToSamples(double time) {
		return (int) Math.ceil(samplingFrequency * time);
	}

}
