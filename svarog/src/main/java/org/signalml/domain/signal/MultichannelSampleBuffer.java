/* SampleBuffer.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * This class represents a multichannel source of signal samples with the
 * ability to buffer samples (and return buffered if possible).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MultichannelSampleBuffer extends MultichannelSampleProcessor {

	public static final int INITIAL_BUFFER_SIZE = 128*60*3; // buffer 3 minutes of signal @ 128Hz

	protected static final Logger logger = Logger.getLogger(MultichannelSampleBuffer.class);

        /**
         * the length of the buffer (number of samples)
         */
	private int bufferLength;

        /**
         * an array containing for each channel the in-signal index of the
         * first sample in the buffer (in the range from 0 to signal length-1)
         */
	private int minSample[];

	/**
         * an array containing for each channel the in-signal index of the
         * last sample in the buffer (in the range from 0 to signal length-1)
         */
	private int maxSample[];

        /**
         * an array containing for each <code>channel</code> the index in the
         * buffer where sample <code>minSample</code> is located
         * (in the range from 0 to <code>bufferLength-1</code>)
         * @see #minSample
         */
	private int boundary[];

	private double[][] buffer;

        /**
         * Constructor. Creates a buffer of a given length for a given source.
         * @param source the actual {@link MultichannelSampleSource source}
         * of samples
         * @param bufferLength the length of a buffer (a number of samples)
         */
	public MultichannelSampleBuffer(MultichannelSampleSource source, int bufferLength) {
		super(source);
		this.bufferLength = bufferLength;
		reinitializeBuffers();
	}

        /**
         * Returns the length of a buffer.
         * @return the length of a buffer (number of samples)
         */
	public int getBufferLength() {
		return bufferLength;
	}

        /**
         * Sets the length of a buffer.
         * @param bufferLength the length of a buffer
         */
	public void setBufferLength(int bufferLength) {
		this.bufferLength = bufferLength;
		reinitializeBuffers();
	}

	private static int _buffer_too_small_count = 0;

        /**
         * Returns the given number of samples for a given channel starting
         * from a given position in time.
         * Uses buffering if possible and updates the buffer.
         * @param channel the number of channel
         * @param target the array to which results will be written starting
         * from position <code>arrayOffset</code>
         * @param signalOffset the position (in time) in the signal starting
         * from which samples will be returned
         * @param count the number of samples to be returned
         * @param arrayOffset the offset in <code>target</code> array starting
         * from which samples will be written
         */
	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		if (count > bufferLength) {
			logger.warn(String.format("Unable to use buffer - buffer too small: count=%d bufferLength=%d",
						  count, bufferLength));
			if(this._buffer_too_small_count++ == 0)
				logger.debug("Buffer too small here, writing traceback once", new Throwable());
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

        /**
         * Clears the buffer (actually creates a new one).
         */
	public void clear() {
		reinitializeBuffers();
	}

        /**
         * Returns the index in the buffer where sample <code>minSample</code>
         * for a given channel is located.
         * (in the range from 0 to <code>bufferLength-1</code>)
         * @param channel the index of a channel
         * @return the index in the buffer where sample <code>minSample</code>
         * for a given channel is located
         */
	public int getBoundary(int channel) {
		return boundary[channel];
	}

        /**
         * Returns for a given channel the in-signal index of the
         * last sample in the buffer (in the range from 0 to signal length-1)
         * @param channel the index of a channel
         * @return the in-signal index of the last sample in the buffer
         * (in the range from 0 to signal length-1)
         */
	public int getMaxSample(int channel) {
		return maxSample[channel];
	}

        /**
         * Returns for a given channel the in-signal index of the
         * first sample in the buffer (in the range from 0 to signal length-1)
         * @param channel the index of a channel
         * @return the in-signal index of the first sample in the buffer
         * (in the range from 0 to signal length-1)
         */
	public int getMinSample(int channel) {
		return minSample[channel];
	}

        /**
         * Returns a copy of a buffer for a given channel.
         * @param channel the index of a channel
         * @return a copy of a buffer for a given channel
         */
	public double[] getBufferCopy(int channel) {
		return Arrays.copyOf(buffer[channel], bufferLength);
	}

        /**
         * Returns a copy of a part of a buffer for a given channel.
         * @param channel the index of a channel
         * @param offset the position in the buffer from which coping should
         * start
         * @param length the number of elements to be copied
         * @return the created copy
         */
	public double[] getBufferCopy(int channel, int offset, int length) {
		return Arrays.copyOfRange(buffer[channel], offset, offset+length);
	}

        /**
         * Copies <code>count</code> elements from one array to another.
         * In the source array starts from <code>srcInx</code>, in the
         * destination array from <code>dstInx</code>.
         * @param src the source array
         * @param dst the destination array
         * @param srcIdx the index in the source array from which coping will
         * start
         * @param dstIdx the index in the destination array from starting from
         * which elements will be written to the destination array
         * @param count the number of elements to be copied
         */
	private void arrCopy(double[] src, double[] dst, int srcIdx, int dstIdx, int count) {
		int i,e;
		int srcEnd = srcIdx + count;
		e = dstIdx;
		for (i=srcIdx; i<srcEnd; i++) {
			dst[e] = src[i];
			e++;
		}
	}

        /**
         * Creates a new buffer and replaces the old one.
         */
	private void reinitializeBuffers() {

		int cnt = source.getChannelCount();
		minSample = new int[cnt];
		maxSample = new int[cnt];
		boundary = new int[cnt];

		buffer = new double[cnt][bufferLength];

	}

        /**
         * Fires listeners that the property has changed and reinitialises
         * the buffer
         * @param evt an event describing the change
         */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		reinitializeBuffers();
		super.propertyChange(evt);
	}



}
