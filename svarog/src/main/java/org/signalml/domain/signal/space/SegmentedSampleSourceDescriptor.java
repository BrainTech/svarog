/* SegmentedSampleSourceDescriptor.java created 2008-02-15
 *
 */

package org.signalml.domain.signal.space;

import java.io.Serializable;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSegmentedSampleSource;

/**
 * This is an interface for a descriptor of the
 * {@link MultichannelSegmentedSampleSource segmented source of samples}.
 * Allows to create a segmented source of samples from the given source
 * based on this descriptor.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SegmentedSampleSourceDescriptor extends Serializable {

	/**
	 * Creates the
	 * {@link MultichannelSegmentedSampleSource segmented source of samples}
	 * based on this descriptor.
	 * Uses provided source of samples for the whole channel.
	 * @param source the actual source of samples for the whole channel.
	 * @return the created source of samples
	 */
	MultichannelSegmentedSampleSource createSegmentedSource(MultichannelSampleSource source);

}
