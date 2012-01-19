/* FFTSampleFilterEngine.java created 2008-02-04
 * 
 */

package org.signalml.domain.signal;

import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.math.complex.Complex;

import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.FFTSampleFilter.Range;
import org.signalml.math.fft.FourierTransform;

/**
 * This class represents a FFT filter of samples.
 * Allows to return the filtered samples based on the given 
 * {@link MultichannelSampleSource source}.
 * Uses {@link FourierTransform} to compute filtered samples.
 * If it is possible, buffers filtered samples.
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class FFTSampleFilterEngine extends SampleFilterEngine {

        /**
         * the object performing the Fourier transform
         */	
	private FourierTransform fourierTransform;
	
	private double[] cache = null;
        /**
         * the buffer of already filtered samples
         */
	private double[] filtered = null;
        /**
         * the index (in the source) of the first sample in the buffer
         */
	private int minFilteredSample;
        /**
         * index in the {@link #filtered} array (actually <code>index/2</code>,
         * because samples are on every second position)
         * of the first sample in the buffer
         */
	private int minFilteredSampleAt;
        /**
         * the first index (in the source) after the last sample in the buffer
         */
	private int maxFilteredSample; // index after max sample!
	
        /**
         * Constructor. Creates an engine of a filter for provided
         * {@link SampleSource source} of samples.
         * @param source the source of samples
         * @param definition the {@link FFTSampleFilter definition} of the
         * filter
         */
	public FFTSampleFilterEngine(SampleSource source, FFTSampleFilter definition) {
		super(source);
		this.definition = new FFTSampleFilter(definition);
		fourierTransform = new FourierTransform(definition.getWindowType(), definition.getWindowParameter());
	}

	@Override
	public FFTSampleFilter getFilterDefinition(){
		return (FFTSampleFilter)definition;
	}

        /**
         * Returns the given number of the filtered samples starting from
         * the given position in time.
         * If it is possible uses the {@link #filtered buffer} of already
         * filtered samples.
         * @param target the array to which results will be written starting
         * from position <code>arrayOffset</code>
         * @param signalOffset the position (in time) in the signal starting
         * from which samples will be returned
         * @param count the number of samples to be returned
         * @param arrayOffset the offset in <code>target</code> array starting
         * from which samples will be written
         */
	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		synchronized(this) {

			// check usability of previously filtered samples

			int leftOffsetToCopy;
			int i;

			double samplingFrequency = source.getSamplingFrequency();
			int intSamplingFrequency = (int) Math.ceil(samplingFrequency);

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

				// normalize count to power of 2 with offset
				int minCount = Math.max(6 * intSamplingFrequency, count + 2 * intSamplingFrequency);
				int countPow2 = FourierTransform.getPowerOfTwoSize(minCount);

				// calculate padding
				int padding = (countPow2 - count)/2;
				int leftPadding = padding;
				leftOffsetToCopy = padding;
				int rightPadding = padding + (count % 2 == 0 ? 0 : 1);

				int avSampleCount = source.getSampleCount();
				int rightAvSampleCount = avSampleCount - (signalOffset+count);

				int zeroLeftPadding = (signalOffset < leftPadding ? (leftPadding-signalOffset) : 0);
				int zeroRightPadding = (rightAvSampleCount < rightPadding ? (rightPadding-rightAvSampleCount) : 0);

				leftPadding -= zeroLeftPadding;
				rightPadding -= zeroRightPadding;

				// get raw data
				if(cache == null || cache.length < countPow2) {
					cache = new double[countPow2];
				}
				if(zeroLeftPadding > 0) {
					Arrays.fill(cache, 0, zeroLeftPadding, 0.0);
				}
				source.getSamples(cache, signalOffset-leftPadding, leftPadding+count+rightPadding, zeroLeftPadding);
				if(zeroRightPadding > 0) {
					Arrays.fill(cache, zeroLeftPadding+leftPadding+count+rightPadding, cache.length, 0.0);
				}

				// transform
				Complex[] transformed = fourierTransform.forwardFFT(cache);

				// we know an even number of points was used
				int segCount = (transformed.length/2) + 1;
				double hzPerSegment = samplingFrequency / transformed.length;

				Iterator<Range> it = ((FFTSampleFilter)definition).getRangeIterator();
				int lowSeg;
				int highSeg;
				float lowFrequency;
				float highFrequency;
				boolean end = false;
				double coefficient;

				while(!end && it.hasNext()) {

					Range range = it.next();
					coefficient = range.getCoefficient();

					// optymization
					if(coefficient == 1) {
						continue;
					}

					lowFrequency = range.getLowFrequency();
					highFrequency = range.getHighFrequency();

					lowSeg = (int) Math.floor(lowFrequency / hzPerSegment);
					if(lowSeg >= segCount) {
						break;
					}

					if(highFrequency <= lowFrequency) {
						highSeg = segCount;
					} else {
						highSeg = (int) Math.floor(highFrequency / hzPerSegment);
						if(highSeg > segCount) {
							highSeg = segCount;
							end = true;
						}
					}

					if(lowSeg == 0) {
						transformed[0] = transformed[0].multiply(coefficient);
						lowSeg++;
					}

					if(highSeg == segCount) {
						transformed[segCount - 1] = transformed[segCount - 1].multiply(coefficient);
						highSeg--;
					}

					// max extent of i is from 1 to N/2-1
					for(i = lowSeg; i < highSeg; i++) {
						transformed[i] = transformed[i].multiply(coefficient);
						transformed[transformed.length - i] = transformed[transformed.length - i].multiply(coefficient);
					}

				}

				// inverse
				filtered = fourierTransform.inverseFFT(transformed);

				//filtered = fourierTransform.getTransformedDataAsAlternate();

				minFilteredSample = signalOffset - leftPadding;
				minFilteredSampleAt = leftOffsetToCopy - leftPadding;
				maxFilteredSample = signalOffset + count + rightPadding;

			}

			int filteredIdx = leftOffsetToCopy;

			// return data
			for(i = 0; i < count; i++) {
				target[arrayOffset + i] =  filtered[filteredIdx];
				filteredIdx++;
			}

		}
	}

}
