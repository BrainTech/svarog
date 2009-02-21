/* MultichannelSegmentedSampleSource.java created 2008-01-26
 * 
 */

package org.signalml.domain.signal;

import org.signalml.domain.signal.space.SegmentedSampleSourceDescriptor;

/** MultichannelSegmentedSampleSource
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MultichannelSegmentedSampleSource extends MultichannelSampleSource {
	
	int getSegmentCount();
	int getSegmentLength();
	
	float getSegmentTime( int segment );
	
	void getSegmentSamples( int channel, double[] target, int segment );

	int getUnusableSegmentCount();

	public SegmentedSampleSourceDescriptor createDescriptor();	
}
