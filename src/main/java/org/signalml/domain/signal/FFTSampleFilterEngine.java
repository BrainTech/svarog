/* FFTSampleFilterEngine.java created 2008-02-04
 * 
 */

package org.signalml.domain.signal;

import java.util.Arrays;
import java.util.Iterator;

import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.FFTSampleFilter.Range;

import flanagan.complex.Complex;
import flanagan.math.FourierTransform;

/** FFTSampleFilterEngine
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class FFTSampleFilterEngine extends SampleFilterEngine {
	
	public static final double LOG2 = Math.log(2);
	
	private FFTSampleFilter definition;
	private FourierTransform fourierTransform;
	
	private double[] cache = null;
	private double[] filtered = null;
	private int minFilteredSample;
	private int minFilteredSampleAt;
	private int maxFilteredSample; // index after max sample!
	
	public FFTSampleFilterEngine( SampleSource source, FFTSampleFilter definition ) {
		super( source );
		this.definition = new FFTSampleFilter( definition );
		fourierTransform = new FourierTransform();
		
		definition.getWindowType().apply(fourierTransform, definition.getWindowParameter());		
	}
		
	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		synchronized( this ) {

			// check usability of previously filtered samples
			
			int leftOffsetToCopy;
			int i;

			double samplingFrequency = source.getSamplingFrequency();
			int intSamplingFrequency = (int) Math.ceil( samplingFrequency );
			
			if( 
					filtered != null
					&&
					minFilteredSample <= signalOffset - intSamplingFrequency
					&&
					maxFilteredSample >= signalOffset + count + intSamplingFrequency
				) {
				
				// previously filtered samples are usable
				leftOffsetToCopy = minFilteredSampleAt + (signalOffset - minFilteredSample);
								
			} else {

				fourierTransform.setDeltaT( 1.0 / samplingFrequency );
				
				// normalize count to power of 2 with offset
				int minCount = Math.max( 6 * intSamplingFrequency, count + 2 * intSamplingFrequency );
				int countPow2 = (int) Math.pow( 2, Math.ceil( Math.log(minCount) / LOG2 ) );
				
				// calculate padding
				int padding = (countPow2 - count)/2;
				int leftPadding = padding;
				leftOffsetToCopy = padding;
				int rightPadding = padding + ( count % 2 == 0 ? 0 : 1 );
				
				int avSampleCount = source.getSampleCount();		
				int rightAvSampleCount = avSampleCount - (signalOffset+count);
		
				int zeroLeftPadding = ( signalOffset < leftPadding ? (leftPadding-signalOffset) : 0 );
				int zeroRightPadding = ( rightAvSampleCount < rightPadding ? (rightPadding-rightAvSampleCount) : 0 );
				
				leftPadding -= zeroLeftPadding;
				rightPadding -= zeroRightPadding;
				
				// get raw data
				if( cache == null || cache.length < countPow2 ) {
					cache = new double[countPow2];
				}
				if( zeroLeftPadding > 0 ) {
					Arrays.fill( cache, 0, zeroLeftPadding, 0.0 );
				}
				source.getSamples(cache, signalOffset-leftPadding, leftPadding+count+rightPadding, zeroLeftPadding);
				if( zeroRightPadding > 0 ) {
					Arrays.fill( cache, zeroLeftPadding+leftPadding+count+rightPadding, cache.length, 0.0 );
				}
				
				// transform			
				fourierTransform.setData( cache );			
				fourierTransform.transform();
				
				// apply range coefficients
				Complex[] transformed = fourierTransform.getTransformedDataAsComplex();
				
				// we know an even number of points was used
				int segCount = (transformed.length/2) + 1;
				double hzPerSegment = samplingFrequency / transformed.length;
				
				Iterator<Range> it = definition.getRangeIterator();
				int lowSeg;
				int highSeg;
				float lowFrequency;
				float highFrequency;
				boolean end = false;
				double coefficient;
				
				while( !end && it.hasNext() ) {
					
					Range range = it.next();
					coefficient = range.getCoefficient();
					
					// optymization
					if( coefficient == 1 ) {
						continue;
					}
					
					lowFrequency = range.getLowFrequency();
					highFrequency = range.getHighFrequency();
					
					lowSeg = (int) Math.floor(lowFrequency / hzPerSegment);
					if( lowSeg >= segCount ) {
						break;
					}
					
					if( highFrequency <= lowFrequency ) {
						highSeg = segCount;
					} else {
						highSeg = (int) Math.floor( highFrequency / hzPerSegment );
						if( highSeg > segCount ) {
							highSeg = segCount;
							end = true;
						}
					}
					
					if( lowSeg == 0 ) {
						transformed[0].timesEquals(coefficient);
						lowSeg++;
					}
					
					if( highSeg == segCount ) {
						transformed[segCount-1].timesEquals(coefficient);
						highSeg--;
					}
					
					// max extent of i is from 1 to N/2-1
					for( i=lowSeg; i<highSeg; i++ ) {
						transformed[i].timesEquals(coefficient);
						transformed[transformed.length-i].timesEquals(coefficient);
					}
									
				}
				
				// inverse
				fourierTransform.setData(transformed);
				fourierTransform.inverse();
				
				filtered = fourierTransform.getTransformedDataAsAlternate();

				minFilteredSample = signalOffset - leftPadding;
				minFilteredSampleAt = leftOffsetToCopy - leftPadding;
				maxFilteredSample = signalOffset + count + rightPadding;
				
			}
			
			int filteredIdx = 2 * leftOffsetToCopy;
			
			// return data
			for( i=0; i<count; i++ ) {
				target[arrayOffset+i] =  filtered[filteredIdx];
				filteredIdx += 2;
			}
						
		}
	}

}
