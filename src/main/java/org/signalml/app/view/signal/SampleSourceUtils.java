/* SampleSourceUtils.java created 2008-01-30
 * 
 */

package org.signalml.app.view.signal;

import org.signalml.domain.signal.MultichannelSampleSource;

/** SampleSourceUtils
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SampleSourceUtils {

	public static int getMaxSampleCount( MultichannelSampleSource sampleSource ) {
		
		int maxSampleCount = 0;
		int sampleCount = 0;
		
		int channelCount = sampleSource.getChannelCount();
		
		for( int i=0; i<channelCount; i++ ) {
			sampleCount = sampleSource.getSampleCount(i);
			if( sampleCount > maxSampleCount ) {
				maxSampleCount = sampleCount;
			}
		}
		
		return maxSampleCount;
			
	}

	public static int getMinSampleCount( MultichannelSampleSource sampleSource ) {
		
		int minSampleCount = Integer.MAX_VALUE;
		int sampleCount = 0;
		
		int channelCount = sampleSource.getChannelCount();
		
		for( int i=0; i<channelCount; i++ ) {
			sampleCount = sampleSource.getSampleCount(i);
			if( sampleCount < minSampleCount ) {
				minSampleCount = sampleCount;
			}
		}
		
		return minSampleCount;
			
	}
	
}
