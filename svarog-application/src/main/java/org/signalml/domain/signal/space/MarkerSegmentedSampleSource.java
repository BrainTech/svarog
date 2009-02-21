/* MarkerSegmentedSignalSource.java created 2008-01-27
 * 
 */

package org.signalml.domain.signal.space;

import java.util.Arrays;

import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.Tag;
import org.signalml.domain.tag.TagStyle;

/** MarkerSegmentedSignalSource
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MarkerSegmentedSampleSource extends MultichannelSampleProcessor implements MultichannelSegmentedSampleSource {

	private int segmentLength;
	private int[] offsets;
	
	private int channelCount;
	private int[] channelIndices;
	
	private int unusableSegmentCount;
	private int samplesBefore;
	private int samplesAfter;
	private float samplingFrequency;
		
	protected MarkerSegmentedSampleSource(MultichannelSampleSource source) {
		super(source);
	}
	
	public MarkerSegmentedSampleSource(MultichannelSampleSource source, StyledTagSet tagSet, String markerName, double secondsBefore, double secondsAfter, ChannelSpace channelSpace ) {
		super(source);

		if( channelSpace != null ) {
			
			channelCount = channelSpace.size();
			channelIndices = channelSpace.getSelectedChannels();
			
		}
		else { // all channels
			
			channelCount = source.getChannelCount();
			channelIndices = new int[channelCount];
			for( int i=0; i<channelCount; i++ ) {
				channelIndices[i] = i;
			}
			
		}
		
		samplingFrequency = source.getSamplingFrequency();
		samplesBefore = (int) Math.ceil( samplingFrequency * secondsBefore );
		samplesAfter = (int) Math.ceil( samplingFrequency * secondsAfter );
		segmentLength = samplesBefore + samplesAfter;
		
		TagStyle markerStyle = tagSet.getStyle( markerName );		
		int channelTagCount = tagSet.getChannelTagCount();			
		
		int minSampleCount = SampleSourceUtils.getMinSampleCount(source);
		int i;
		
		Tag tag;
		int markerSample;
		
		int averagedCount = 0;
				
		int[] offsetArr = new int[channelTagCount]; 
						
		for( i=0; i<channelTagCount; i++ ) {
			tag = tagSet.getChannelTagAt(i);
			if( tag.isMarker() && tag.getStyle().equals(markerStyle) ) {
		
				markerSample = (int) Math.floor( samplingFrequency * tag.getCenterPosition() );
				
				if( (markerSample-1) < samplesBefore ) { // samplesBefore samples from markerSample inclusive
					// not enough samples before
					unusableSegmentCount++;
					continue;
				}
				if( minSampleCount - (markerSample+1) < samplesAfter ) { // samplesAfter samples from (markerSample+1) inclusive
					// not enough samples after
					unusableSegmentCount++;
					continue;
				}
				
				// sample is ok
				
				offsetArr[averagedCount] = markerSample - (samplesBefore-1);
				averagedCount++;
				
			}
			
		}
		
		offsets = Arrays.copyOf( offsetArr, averagedCount );
					
	}

	public MarkerSegmentedSampleSource(MultichannelSampleSource source, MarkerSegmentedSampleSourceDescriptor descriptor) {
		this(source);
		
		segmentLength = descriptor.getSegmentLength();
		offsets = descriptor.getOffsets();
		
		channelCount = descriptor.getChannelCount();
		channelIndices = descriptor.getChannelIndices();
		
		unusableSegmentCount = descriptor.getUnusableSegmentCount();
		samplesBefore = descriptor.getSamplesBefore();
		samplesAfter = descriptor.getSamplesAfter();
	}
	
	@Override
	public MarkerSegmentedSampleSourceDescriptor createDescriptor() {
		
		MarkerSegmentedSampleSourceDescriptor descriptor = new MarkerSegmentedSampleSourceDescriptor();
		
		descriptor.setSegmentLength( segmentLength );
		descriptor.setOffsets( offsets );
		
		descriptor.setChannelCount( channelCount );
		descriptor.setChannelIndices( channelIndices );
		
		descriptor.setUnusableSegmentCount( unusableSegmentCount );
		descriptor.setSamplesBefore( samplesBefore );
		descriptor.setSamplesAfter( samplesAfter );
		
		return descriptor;
		
	}
	
	@Override
	public float getSegmentTime(int segment) {
		return ((float) offsets[segment]) / samplingFrequency;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		
		if( count == 0 ) {
			return;
		}
		
		int skippedSegments = signalOffset / segmentLength;
		int skippedSamples = signalOffset % segmentLength;
		
		int neededCount = count;
		int targetOffset = arrayOffset;		
		
		if( skippedSamples > 0 ) {
			
			// get the rest of the first segment affected, if not at segment boundary
			
			int firstSegmentLeftover = Math.min( segmentLength-skippedSamples, neededCount );
			
			source.getSamples(channelIndices[channel], target, offsets[skippedSegments], firstSegmentLeftover, targetOffset);
			
			neededCount -= firstSegmentLeftover;
			targetOffset += firstSegmentLeftover;
			
			skippedSegments++;
			
		} 

		int wholeSegments = neededCount / segmentLength;
		
		if( wholeSegments > 0 ) {
			
			for( int i=0; i<wholeSegments; i++ ) {
				source.getSamples(channelIndices[channel], target, offsets[skippedSegments+i], segmentLength, targetOffset);
				
				neededCount -= segmentLength;
				targetOffset += segmentLength;
			}
			
		}
		
		if( neededCount > 0 ) {
			
			source.getSamples(channelIndices[channel], target, offsets[skippedSegments+wholeSegments], neededCount, targetOffset);
			
		}
	
	}

	@Override
	public int getSegmentCount() {
		return offsets.length;
	}

	@Override
	public int getSegmentLength() {
		return segmentLength;
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		return source.getDocumentChannelIndex(channelIndices[channel]);
	}
	
	@Override
	public int getChannelCount() {
		return channelCount;
	}

	@Override
	public int getSampleCount(int channel) {
		return ( offsets.length * segmentLength );
	}
	
	@Override
	public void getSegmentSamples(int channel, double[] target, int segment) {
		source.getSamples(channelIndices[channel], target, offsets[segment], segmentLength, 0);
	}

	@Override
	public int getUnusableSegmentCount() {
		return unusableSegmentCount;
	}

	public int getSamplesBefore() {
		return samplesBefore;
	}

	public int getSamplesAfter() {
		return samplesAfter;
	}
	
}
