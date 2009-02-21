/* SegmentedSampleSourceDescriptor.java created 2008-02-15
 * 
 */

package org.signalml.domain.signal.space;

import java.io.Serializable;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;

/** SegmentedSampleSourceDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SegmentedSampleSourceDescriptor extends Serializable {

	MultichannelSegmentedSampleSource createSegmentedSource( MultichannelSampleSource source );
	
}
