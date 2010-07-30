/* MarkerSegmentedSampleSourceDescriptor.java created 2008-02-15
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents the description for the
 * {@link MarkerSegmentedSampleSource source of samples} associated with the
 * marker.
 * Contains the basic information about the source, such as length, selected
 * channels, location of segments and so on.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("markersegsource")
public class MarkerSegmentedSampleSourceDescriptor implements SegmentedSampleSourceDescriptor {

	private static final long serialVersionUID = 1L;

        /**
         * the number of samples in the segment
         */
	private int segmentLength;
        /**
         * an array of the beginnings of segments
         */
	private int[] offsets;

        /**
         * the number of channels in the described
         * {@link MarkerSegmentedSampleSource source}
         */
	private int channelCount;
        /**
         * an array mapping indexes in the described
         * {@link MarkerSegmentedSampleSource source} to the indexes of
         * channels in the actual source
         */
	private int[] channelIndices;

        /**
         * the number of segments that can not be used (the required
         * neighbourhood of the marker is not in the signal)
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
         * Constructor. Creates an empty descriptor.
         */
	public MarkerSegmentedSampleSourceDescriptor() {
	}

        /**
         * Creates the
         * {@link MarkerSegmentedSampleSource segmented source of samples}
         * based on this descriptor.
         * Uses provided source of samples for the whole channel.
         * @param source the actual source of samples for the whole channel
         * @return the created source of samples
         */
	@Override
	public MultichannelSegmentedSampleSource createSegmentedSource(MultichannelSampleSource source) {
		return new MarkerSegmentedSampleSource(source, this);
	}

        /**
         * Returns the number of samples in the segment.
         * @return the number of samples in the segment
         */
	public int getSegmentLength() {
		return segmentLength;
	}

        /**
         * Sets the number of samples in the segment.
         * @param segmentLength the number of samples in the segment
         */
	public void setSegmentLength(int segmentLength) {
		this.segmentLength = segmentLength;
	}

        /**
         * Returns an array of the beginnings of segments.
         * @return an array of the beginnings of segments
         */
	public int[] getOffsets() {
		return offsets;
	}

        /**
         * Sets an array of the beginnings of segments.
         * @param offsets an array of the beginnings of segments
         */
	public void setOffsets(int[] offsets) {
		this.offsets = offsets;
	}

        /**
         * Returns the number of channels in the described
         * {@link MarkerSegmentedSampleSource source}.
         * @return the number of channels in the described source
         */
	public int getChannelCount() {
		return channelCount;
	}

        /**
         * Sets the number of channels in the described
         * {@link MarkerSegmentedSampleSource source}.
         * @param channelCount the number of channels in the described source
         */
	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

        /**
         * Returns an array mapping indexes in the described
         * {@link MarkerSegmentedSampleSource source} to the indexes of
         * channels in the actual source.
         * @return an array mapping indexes in the described
         * source to the indexes of channels in the actual source
         */
	public int[] getChannelIndices() {
		return channelIndices;
	}

        /**
         * Sets an array mapping indexes in the described
         * {@link MarkerSegmentedSampleSource source} to the indexes of
         * channels in the actual source.
         * @param channelIndices an array mapping indexes in the described
         * source to the indexes of channels in the actual source
         */
	public void setChannelIndices(int[] channelIndices) {
		this.channelIndices = channelIndices;
	}

        /**
         * Returns the number of segments that can not be used (the required
         * neighbourhood of the marker is not in the signal)
         * @return the number of segments that can not be used (the required
         * neighbourhood of the marker is not in the signal)
         */
	public int getUnusableSegmentCount() {
		return unusableSegmentCount;
	}

        /**
         * Sets the number of segments that can not be used (the required
         * neighbourhood of the marker is not in the signal)
         * @param unusableSegmentCount the number of segments that can not be
         * used (the required neighbourhood of the marker is not in the signal)
         */
	public void setUnusableSegmentCount(int unusableSegmentCount) {
		this.unusableSegmentCount = unusableSegmentCount;
	}

        /**
         * Returns the number of samples before the marker that should be
         * included in the segment
         * @return the number of samples before the marker that should be
         * included in the segment
         */
	public int getSamplesBefore() {
		return samplesBefore;
	}

        /**
         * Sets the number of samples before the marker that should be included
         * in the segment
         * @param samplesBefore the number of samples before the marker that
         * should be included in the segment
         */
	public void setSamplesBefore(int samplesBefore) {
		this.samplesBefore = samplesBefore;
	}

        /**
         * Returns the number of samples after the marker that should be
         * included in the segment
         * @return the number of samples after the marker that should be
         * included in the segment
         */
	public int getSamplesAfter() {
		return samplesAfter;
	}

        /**
         * Sets the number of samples after the marker that should be included
         * in the segment
         * @param samplesAfter  the number of samples after the marker that
         * should be included in the segment
         */
	public void setSamplesAfter(int samplesAfter) {
		this.samplesAfter = samplesAfter;
	}

}
