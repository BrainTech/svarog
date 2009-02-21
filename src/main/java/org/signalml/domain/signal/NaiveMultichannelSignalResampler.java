/* NaiveMultichannelSignalUpsampler.java created 2008-01-30
 * 
 */

package org.signalml.domain.signal;

/** NaiveMultichannelSignalUpsampler
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NaiveMultichannelSignalResampler implements MultichannelSignalResampler {

	private double[] tempBuffer = null;
	
	@Override
	public void resample(ResamplableSampleSource sampleSource, int channel, double[] target, int externalSignalOffset, int externalCount, int arrayOffset, float externalFrequency, float internalFrequency) {

		float factor = internalFrequency / externalFrequency;
		
		int minInternalSample = (int) Math.floor( externalSignalOffset * factor );
		int maxInternalSample = (int) Math.ceil( (externalSignalOffset + externalCount -1) * factor );
		
		int internalCount = 1 + maxInternalSample - minInternalSample;
		if( tempBuffer == null || tempBuffer.length < internalCount ) {
			tempBuffer = new double[internalCount];
		}
		
		sampleSource.getRawSamples(channel, tempBuffer, minInternalSample, internalCount, 0);
		
		int i;
		for( i=0; i<externalCount; i++ ) {			
			target[arrayOffset+i] = tempBuffer[ Math.round(factor * (externalSignalOffset+i)) - minInternalSample ];			
		}
				
	}

}
