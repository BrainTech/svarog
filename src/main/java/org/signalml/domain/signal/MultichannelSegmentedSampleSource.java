/* MultichannelSegmentedSampleSource.java created 2008-01-26
 *
 */

package org.signalml.domain.signal;

import org.signalml.domain.signal.space.SegmentedSampleSourceDescriptor;

/**
 * This interface represents the {@link MultichannelSampleSource source} of
 * samples for some segments from the signal.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MultichannelSegmentedSampleSource extends MultichannelSampleSource {

         /**
         * Returns the number of segments in this source
         * @return the number of segments
         */
	int getSegmentCount();

        /**
         * Returns the length of the segment (number of samples)
         * @return the length of the segment (number of samples)
         */
	int getSegmentLength();

        /**
         * Returns the point in time (in seconds) where the segment begins.
         * @param segment the index of the segment
         * @return the point in time where the segment begins
         */
	float getSegmentTime(int segment);

        /**
         * Returns the samples for the given segment and channel.
         * @param channel the index of the channel in this source
         * @param target an array in which the result is written
         * @param segment the index of the segment
         */
	void getSegmentSamples(int channel, double[] target, int segment);

        /**
         * Returns the number of segments that can not be used (the required
         * neighbourhood of the marker is not in the signal).
         * @return the number of segments that can not be used (the required
         * neighbourhood of the marker is not in the signal).
         */
	int getUnusableSegmentCount();

        /**
         * Creates the {@link SegmentedSampleSourceDescriptor descriptor}
         * of this source.
         * @return the descriptor of this source
         */
	public SegmentedSampleSourceDescriptor createDescriptor();
}
