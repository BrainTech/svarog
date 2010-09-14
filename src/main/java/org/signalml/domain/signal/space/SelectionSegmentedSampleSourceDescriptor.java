/* SelectionSegmentedSampleSourceDescriptor.java created 2008-02-15
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.domain.signal.SignalSelection;

/**
 * This class represents a descriptor of a
 * {@link SelectionSegmentedSampleSource segmented source of samples}.
 * Allows to create a {@link MultichannelSegmentedSampleSource segmented source}
 * of samples from the given {@link MultichannelSampleSource source}
 * based on this descriptor.
 * Contains the index of the first sample, selected channels and the length
 * of the segment.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("selectionsegsource")
public class SelectionSegmentedSampleSourceDescriptor implements SegmentedSampleSourceDescriptor {

	private static final long serialVersionUID = 1L;

        /**
         * the index of the first selected sample
         */
	private int firstSample;
        /**
         * the number of samples in the selection
         */
	private int segmentLength;
        /**
         * the number of segments in the described
         * {@link SelectionSegmentedSampleSource source},
         * if the {@link SignalSelection selection} is a page selection - the
         * number of pages in the selection
         * if the selection is a block selection - the number of blocks
         * if the selection is a channel selection - <code>1</code>
         */
	private int segmentCount;

        /**
         * the number of channels in the described
         * {@link SelectionSegmentedSampleSource source}
         */
	private int channelCount;
        /**
         * an array mapping indexes in the described
         * {@link SelectionSegmentedSampleSource source} to the indexes of
         * channels in the actual source
         */
	private int[] channelIndices;

        /**
         * the number of segments that can not be used (the required
         * segment is not in the signal)
         */
	private int unusableSegmentCount;

        /**
         * Constructor. Creates an empty descriptor.
         */
	public SelectionSegmentedSampleSourceDescriptor() {
	}

        /**
         * Creates the
         * {@link SelectionSegmentedSampleSource segmented source of samples}
         * based on this descriptor.
         * Uses provided source of samples for the whole channel.
         * @param source the actual source of samples for the whole channel
         * @return the created source of samples
         */
	@Override
	public MultichannelSegmentedSampleSource createSegmentedSource(MultichannelSampleSource source) {
		return new SelectionSegmentedSampleSource(source,this);
	}

        /**
         * Returns the index of the first selected sample.
         * @return the index of the first selected sample
         */
	public int getFirstSample() {
		return firstSample;
	}

        /**
         * Sets the index of the first selected sample.
         * @param firstSample the index of the first selected sample
         */
	public void setFirstSample(int firstSample) {
		this.firstSample = firstSample;
	}

        /**
         * Returns the number of samples in the selection.
         * @return the number of samples in the selection
         */
	public int getSegmentLength() {
		return segmentLength;
	}

        /**
         * Sets the number of samples in the selection.
         * @param segmentLength the number of samples in the selection
         */
	public void setSegmentLength(int segmentLength) {
		this.segmentLength = segmentLength;
	}

        /**
         * Returns the number of segments in the described
         * {@link SelectionSegmentedSampleSource source}:
         * if the {@link SignalSelection selection} is a page selection - the
         * number of pages in the selection
         * if the selection is a block selection - the number of blocks
         * if the selection is a channel selection - <code>1</code>
         * @return the number of segments in the described source
         */
	public int getSegmentCount() {
		return segmentCount;
	}

        /**
         * Sets the number of segments in the described
         * {@link SelectionSegmentedSampleSource source}:
         * if the {@link SignalSelection selection} is a page selection - the
         * number of pages in the selection
         * if the selection is a block selection - the number of blocks
         * if the selection is a channel selection - <code>1</code>
         * @param segmentCount the number of segments in the described source
         */
	public void setSegmentCount(int segmentCount) {
		this.segmentCount = segmentCount;
	}

        /**
         * Returns the number of channels in the described
         * {@link SelectionSegmentedSampleSource source}.
         * @return the number of channels in the described source
         */
	public int getChannelCount() {
		return channelCount;
	}

        /**
         * Sets the number of channels in the described
         * {@link SelectionSegmentedSampleSource source}
         * @param channelCount  the number of channels in the described source
         */
	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

        /**
         * Returns the array mapping indexes in the described
         * {@link SelectionSegmentedSampleSource source} to the indexes of
         * channels in the actual source
         * @return the array mapping indexes in the describe source to the
         * indexes of channels in the actual source
         */
	public int[] getChannelIndices() {
		return channelIndices;
	}

        /**
         * Sets the array mapping indexes in the described
         * {@link SelectionSegmentedSampleSource source} to the indexes of
         * channels in the actual source
         * @param channelIndices the array mapping indexes in the described
         * source to the indexes of channels in the actual source
         */
	public void setChannelIndices(int[] channelIndices) {
		this.channelIndices = channelIndices;
	}

        /**
         * Returns the number of segments that can not be used (the required
         * segment is not in the signal)
         * @return the number of segments that can not be used (the required
         * segment is not in the signal)
         */
	public int getUnusableSegmentCount() {
		return unusableSegmentCount;
	}

        /**
         * Sets the number of segments that can not be used (the required
         * segment is not in the signal)
         * @param unusableSegmentCount the number of segments that can not be
         * used (the required segment is not in the signal)
         */
	public void setUnusableSegmentCount(int unusableSegmentCount) {
		this.unusableSegmentCount = unusableSegmentCount;
	}

}
