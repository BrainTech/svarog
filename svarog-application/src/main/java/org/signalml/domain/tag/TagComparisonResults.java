/* TagComparisonResults.java created 2007-11-14
 * 
 */

package org.signalml.domain.tag;

import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.signal.MultichannelSampleSource;

/** TagComparisonResults
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagComparisonResults {

	private TagComparisonResult pageTagResult;
	private TagComparisonResult blockTagResult;
	private TagComparisonResult[] channelTagResults;

	private float totalSignalTime;
	private float[] totalChannelTimes;
	private String[] channelNames;
	
	public TagComparisonResults(TagComparisonResult pageTagResult, TagComparisonResult blockTagResult, TagComparisonResult[] channelTagResults) {
		
		this.pageTagResult = pageTagResult;
		this.blockTagResult = blockTagResult;
		this.channelTagResults = channelTagResults;
		this.totalChannelTimes = new float[channelTagResults.length];
		this.channelNames = new String[channelTagResults.length];
				
	}
	
	public int getChannelCount() {
		return channelTagResults.length;
	}
	
	public TagComparisonResult getChannelResult(int index) {
		return channelTagResults[index];
	}

	public TagComparisonResult getPageTagResult() {
		return pageTagResult;
	}

	public TagComparisonResult getBlockTagResult() {
		return blockTagResult;
	}

	public TagComparisonResult[] getChannelTagResults() {
		return channelTagResults;
	}	
		
	public float getTotalSignalTime() {
		return totalSignalTime;
	}

	public void setTotalSignalTime(float totalSignalTime) {
		this.totalSignalTime = totalSignalTime;
	}

	public float[] getTotalChannelTimes() {
		return totalChannelTimes;
	}
	
	public float getTotalChannelTime(int index) {
		return totalChannelTimes[index];
	}

	public void setTotalChannelTimes(float[] totalChannelTimes) {
		if( totalChannelTimes.length < channelTagResults.length ) {
			throw new IndexOutOfBoundsException("Array too short");
		}
		for( int i=0; i<this.totalChannelTimes.length; i++ ) {
			this.totalChannelTimes[i] = totalChannelTimes[i];
		}
	}

	public String[] getChannelNames() {
		return channelNames;
	}

	public void setChannelNames(String[] channelNames) {
		if( channelNames.length < channelTagResults.length ) {
			throw new IndexOutOfBoundsException("Array too short");
		}
		for( int i=0; i<this.channelNames.length; i++ ) {
			this.channelNames[i] = channelNames[i];
		}
	}

	public void getParametersFromSampleSource( MultichannelSampleSource source, SourceMontage montage ) {

		int channelCount = source.getChannelCount();
		if( channelCount < channelTagResults.length ) {
			throw new IllegalArgumentException( "Source not compatible - not enough channels" );
		}
		if( channelCount > channelTagResults.length ) {
			channelCount = channelTagResults.length;
		}
		
		float samplingFrequency = source.getSamplingFrequency();

		totalSignalTime = 0F;
		for( int i=0; i<channelCount; i++ ) {
			totalChannelTimes[i] = source.getSampleCount(i) / samplingFrequency;
			if( totalChannelTimes[i] > totalSignalTime ) {
				totalSignalTime = totalChannelTimes[i];
			}
			channelNames[i] = montage.getSourceChannelLabelAt(i);
		}
		
	}
		
}
