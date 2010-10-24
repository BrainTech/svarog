/* SegmentedSampleSource.java created 2008-01-26
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;

/**
 * This class represents the {@link MultichannelSampleSource source} of samples
 * for the {@link SignalSelection selected} part of the signal.
 * Contains the index of the first sample, selected channels and the length
 * of the segment.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SelectionSegmentedSampleSource extends MultichannelSampleProcessor implements MultichannelSegmentedSampleSource {

        /**
         * the index of the first selected sample
         */
	private int firstSample;
        /**
         * the number of samples in the selection
         */
	private int segmentLength;
        /**
         * the number of segments in this source:
         * <ul>
         * <li>if the {@link SignalSelection selection} is a page selection - the
         * number of pages in the selection</li>
         * <li>if the selection is a block selection - the number of blocks</li>
         * <li>if the selection is a channel selection - <code>1</code></li>
         * </ul>
         */
	private int segmentCount;

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
         * segment is not in the signal).
         */
	private int unusableSegmentCount;
        /**
         * the point in time (seconds) where the selection starts
         */
	private double firstPosition;

        /**
         * Constructor. Creates a source without the selection
         * @param source the actual source of samples
         */
	public SelectionSegmentedSampleSource(MultichannelSampleSource source) {
		super(source);
	}

        /**
         * Constructor. Creates the source of samples based on a given
         * {@link MultichannelSampleSource source} of the signal,
         * {@link ChannelSpace subset} of channels and
         * {@link SignalSelection selection}.
         * @param source the source for the whole signal
         * @param selection the selection of the part of the signal
         * @param channelSpace the subset of channels
         * @param pageSize the size of a page (in seconds)
         * @param blockSize the size of a block (in seconds)
         */
	public SelectionSegmentedSampleSource(MultichannelSampleSource source, SignalSelection selection, ChannelSpace channelSpace, float pageSize, float blockSize) {
		super(source);

		float samplingFrequency = source.getSamplingFrequency();

		SignalSelectionType selectionType = selection.getType();

		// determine channel count & indices
		if (channelSpace != null) {

			channelCount = channelSpace.size();
			channelIndices = channelSpace.getSelectedChannels();

		}
		else if (selectionType.isChannel()) {

			channelCount = 1;
			channelIndices = new int[] { selection.getChannel() };

		} else { // all channels

			channelCount = source.getChannelCount();
			channelIndices = new int[channelCount];
			for (int i=0; i<channelCount; i++) {
				channelIndices[i] = i;
			}

		}

		// determine segmentation

		firstPosition = selection.getPosition();
		firstSample = (int)(firstPosition * samplingFrequency);

		if (selectionType.isChannel()) {

			segmentCount = 1;
			segmentLength = (int)(selection.getLength() * samplingFrequency);

		} else {

			float segmentSize;

			if (selectionType.isPage()) {
				segmentSize = pageSize;
				segmentLength = (int)(pageSize * samplingFrequency);
			} else if (selectionType.isBlock()) {
				segmentSize = blockSize;
				segmentLength = (int)(blockSize * samplingFrequency);
			} else {
				throw new SanityCheckException("Unsupported type [" + selectionType + "]");
			}

			segmentCount = selection.getSegmentLength(segmentSize);
			if (segmentCount * segmentSize < selection.getLength()) {
				unusableSegmentCount = 1;
			} else {
				unusableSegmentCount = 0;
			}

		}

	}

	@Override
	public double getSegmentTime(int segment) {
		return firstPosition + (segmentLength*segment);
	}

         /**
         * Constructor. Creates the source of samples based on the given
         * {@link MultichannelSampleSource source} of all channels an the given
         * {@link SelectionSegmentedSampleSourceDescriptor descriptor}.
         * @param source the source of samples for all channels
         * @param descriptor the descriptor of this source
         */
	public SelectionSegmentedSampleSource(MultichannelSampleSource source, SelectionSegmentedSampleSourceDescriptor descriptor) {
		this(source);

		firstSample = descriptor.getFirstSample();
		segmentLength = descriptor.getSegmentLength();
		segmentCount = descriptor.getSegmentCount();

		channelCount = descriptor.getChannelCount();
		channelIndices = descriptor.getChannelIndices();

		unusableSegmentCount = descriptor.getUnusableSegmentCount();

	}

	@Override
	public SegmentedSampleSourceDescriptor createDescriptor() {

		SelectionSegmentedSampleSourceDescriptor descriptor = new SelectionSegmentedSampleSourceDescriptor();

		descriptor.setFirstSample(firstSample);
		descriptor.setSegmentLength(segmentLength);
		descriptor.setSegmentCount(segmentCount);

		descriptor.setChannelCount(channelCount);
		descriptor.setChannelIndices(channelIndices);

		descriptor.setUnusableSegmentCount(unusableSegmentCount);

		return descriptor;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		source.getSamples(channelIndices[channel], target, firstSample+signalOffset, count, arrayOffset);
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
	public int getSegmentCount() {
		return segmentCount;
	}

	@Override
	public int getSegmentLength() {
		return segmentLength;
	}

	@Override
	public int getSampleCount(int channel) {
		return (segmentCount * segmentLength);
	}

	@Override
	public String getLabel(int channel) {
		return source.getLabel(channelIndices[channel]);
	}

	@Override
	public int getUnusableSegmentCount() {
		return unusableSegmentCount;
	}

	@Override
	public void getSegmentSamples(int channel, double[] target, int segment) {
		source.getSamples(channelIndices[channel], target, firstSample+(segment*segmentLength), segmentLength, 0);
	}

}
