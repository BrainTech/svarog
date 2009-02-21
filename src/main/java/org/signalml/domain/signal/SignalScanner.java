/* SignalScanner.java created 2008-01-30
 * 
 */

package org.signalml.domain.signal;

import org.signalml.app.view.signal.SignalScanResult;

/** SignalScanner
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalScanner {

	private static final int BUFFER_SIZE = 8192; 
	
	public SignalScanResult scanSignal( MultichannelSampleSource sampleSource, SignalWriterMonitor monitor ) {

		int channelCount = sampleSource.getChannelCount();
		int[] sampleCounts = new int[channelCount];
		int[] processedCounts = new int[channelCount];
		
		double[] data = new double[BUFFER_SIZE]; 

		double minSampleValue = Double.MAX_VALUE;
		double maxSampleValue = Double.MIN_VALUE;
		int i;
		int e;

		boolean moreSamples = false;
		
		for( i=0; i<channelCount; i++ ) {
			sampleCounts[i] = sampleSource.getSampleCount(i);
			if( sampleCounts[i] > 0 ) {
				moreSamples = true;
			}
		}
		
		int toGet;
		int totalProcessed = 0;
		
		while( moreSamples ) {
			
			moreSamples = false;
			
			for( i=0; i<channelCount; i++ ) {
				
				if( sampleCounts[i] > processedCounts[i] ) {
				
					if( monitor != null && monitor.isRequestingAbort() ) {
						return null;
					}
					
					toGet = sampleCounts[i] - processedCounts[i];
					if( toGet > BUFFER_SIZE ) {
						toGet = BUFFER_SIZE;
						moreSamples = true;
					}
					
					sampleSource.getSamples(i, data, processedCounts[i], toGet, 0);
					
					for( e=0; e<toGet; e++ ) {
						
						if( data[e] < minSampleValue ) {
							minSampleValue = data[e];
						}
						if( data[e] > maxSampleValue ) {
							maxSampleValue = data[e];
						}
						
					}
					
					processedCounts[i] += toGet;
					if( processedCounts[i] > totalProcessed ) {
						totalProcessed = processedCounts[i];
						if( monitor != null ) {
							monitor.setProcessedSampleCount(totalProcessed);
						}
					}
					
				}
								
			}
						
		}
		
		SignalScanResult result = new SignalScanResult();
		result.setMinSignalValue(minSampleValue);
		result.setMaxSignalValue(maxSampleValue);
		
		return result;
		
	}
		
}
