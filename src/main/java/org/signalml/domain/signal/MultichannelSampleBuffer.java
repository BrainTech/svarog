/* SampleBuffer.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;

import org.apache.log4j.Logger;

/** SampleBuffer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MultichannelSampleBuffer extends MultichannelSampleProcessor {

	public static final int INITIAL_BUFFER_SIZE = (128*60*3); // buffer 3 minutes of signal @ 128Hz

	protected static final Logger logger = Logger.getLogger(MultichannelSampleBuffer.class);

	private int bufferLength;

	// in-signal index of the lowest sample in the buffer (0 to signal length-1)
	private int minSample[];

	// in-signal index of the lowest sample NOT in the buffer (0 to signal length-1)
	private int maxSample[];

	// the index into the buffer where sample minSample is located (0 to bufferLength-1)
	private int boundary[];

	private double[][] buffer;

	public MultichannelSampleBuffer(MultichannelSampleSource source, int bufferLength) {
		super(source);
		this.bufferLength = bufferLength;
		reinitializeBuffers();
	}

	public int getBufferLength() {
		return bufferLength;
	}

	public void setBufferLength(int bufferLength) {
		this.bufferLength = bufferLength;
		reinitializeBuffers();
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		if (count > bufferLength) {
			logger.warn("Unable to use buffer - buffer too small)");
			source.getSamples(channel, target, signalOffset, count, arrayOffset);
			return;
		}

		int maxSignalOffset = signalOffset + (count - 1);
		int bufferOffset;

		if (minSample[channel] == 0 && maxSample[channel] == 0) {

			// buffer fresh, rebuffer;
			bufferOffset = -1;

		} else if ((signalOffset >= minSample[channel]) && (maxSignalOffset < maxSample[channel])) {

			// the whole requested range is buffered
			bufferOffset = (boundary[channel] + (signalOffset-minSample[channel])) % bufferLength;

		} else if ((signalOffset > (maxSample[channel])) || (maxSignalOffset < (minSample[channel]-1)))  {

			// the requested range is not adjacent to the buffered range - rebuffer whole
			bufferOffset = -1;

		} else {

			// partial buffer use is possible, but something is missing
			int cacheLength = (maxSample[channel]-minSample[channel]);
			int missingLength;
			int lostCnt;

			if (signalOffset < minSample[channel]) {

				// the beginning of the signal needs to be buffered
				missingLength = minSample[channel] - signalOffset;
				if (boundary[channel] - missingLength >= 0) {
					// this can be read in one bit
					source.getSamples(channel, buffer[channel], signalOffset, missingLength, boundary[channel]-missingLength);
					boundary[channel] -= missingLength;
				} else {
					int remainder = missingLength - boundary[channel]; // the number of floats to put at the end
					// read the bit at the beginning of buffer (count of places == boundary)
					if (missingLength > remainder) {
						source.getSamples(channel, buffer[channel], signalOffset+remainder, missingLength-remainder, 0);
					}
					boundary[channel] = bufferLength - remainder;
					source.getSamples(channel, buffer[channel], signalOffset, remainder, boundary[channel]);
				}

				minSample[channel] = signalOffset;
				lostCnt = (cacheLength + missingLength) - bufferLength;
				if (lostCnt > 0) {
					maxSample[channel] -= lostCnt;
				}

				cacheLength = (maxSample[channel]-minSample[channel]);

			}

			if (maxSignalOffset >= maxSample[channel]) {

				int endBoundary = (boundary[channel] + cacheLength) % bufferLength; // the index of the first place following the data

				// the end of the signal needs to be buffered
				missingLength = 1 + maxSignalOffset - maxSample[channel];
				if (endBoundary + missingLength < bufferLength) {
					// this can be read in one bit
					source.getSamples(channel, buffer[channel], maxSample[channel], missingLength, endBoundary);
				} else {
					int remainder = missingLength - (bufferLength-endBoundary);
					if (missingLength > remainder) {
						source.getSamples(channel, buffer[channel], maxSample[channel], missingLength-remainder, endBoundary);
					}
					source.getSamples(channel, buffer[channel], maxSample[channel] + (missingLength-remainder), remainder, 0);
				}

				maxSample[channel] = maxSignalOffset + 1;
				lostCnt = (cacheLength + missingLength) - bufferLength;
				if (lostCnt > 0) {
					minSample[channel] += lostCnt;
					boundary[channel] = (boundary[channel] + lostCnt) % bufferLength;
				}

			}

			bufferOffset = (boundary[channel] + (signalOffset-minSample[channel])) % bufferLength;

		}

		if (bufferOffset < 0) {
			// needs to rebuffer
			boundary[channel] = 0;
			minSample[channel] = signalOffset;
			maxSample[channel] = maxSignalOffset+1;
			source.getSamples(channel, buffer[channel], signalOffset, count, 0);
			bufferOffset = 0;
		}

		// hopefully everything is now buffered
		int copyEnd = (bufferOffset + count);
		if (copyEnd > bufferLength) {
			// copy in two parts
			int second = copyEnd - bufferLength;
			int first = count - second;
			arrCopy(buffer[channel], target, bufferOffset, arrayOffset, first);
			arrCopy(buffer[channel], target, 0, arrayOffset+first, second);
		} else {
			// copy in one part
			arrCopy(buffer[channel], target, bufferOffset, arrayOffset, count);
		}

	}

	public void clear() {
		reinitializeBuffers();
	}

	public int getBoundary(int channel) {
		return boundary[channel];
	}

	public int getMaxSample(int channel) {
		return maxSample[channel];
	}

	public int getMinSample(int channel) {
		return minSample[channel];
	}

	public double[] getBufferCopy(int channel) {
		return Arrays.copyOf(buffer[channel], bufferLength);
	}

	public double[] getBufferCopy(int channel, int offset, int length) {
		return Arrays.copyOfRange(buffer[channel], offset, offset+length);
	}

	private void arrCopy(double[] src, double[] dst, int srcIdx, int dstIdx, int count) {
		int i,e;
		int srcEnd = srcIdx + count;
		e = dstIdx;
		for (i=srcIdx; i<srcEnd; i++) {
			dst[e] = src[i];
			e++;
		}
	}

	private void reinitializeBuffers() {

		int cnt = source.getChannelCount();
		minSample = new int[cnt];
		maxSample = new int[cnt];
		boundary = new int[cnt];

		buffer = new double[cnt][bufferLength];

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		reinitializeBuffers();
		super.propertyChange(evt);
	}



}
