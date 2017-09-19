/* RawSignalSampleSource.java created 2008-01-29
 *
 */

package org.signalml.domain.signal.raw;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.apache.log4j.Logger;
import org.signalml.app.document.signal.BaseSignalSampleSource;

/**
 * This class represents the source of samples for the raw signal.
 * Reads samples from file and uses buffering if possible.
 * Contains information about the file with the signal, the number of channels, the sampling
 * frequency, the {@link RawSignalSampleType type of samples} and
 * the {@link RawSignalByteOrder byte order}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalSampleSource extends BaseSignalSampleSource {

	protected static final Logger logger = Logger.getLogger(RawSignalSampleSource.class);

	/**
	 * the file opened for random access reading
	 */
	private RandomAccessFile randomAccessFile;

	/**
	 * the {@link RawSignalSampleType type} of samples in the signal
	 */
	private RawSignalSampleType sampleType;

	/**
	 * the {@link RawSignalByteOrder order} of bytes in the file with signal
	 */
	private RawSignalByteOrder byteOrder;

	/**
	 * the buffer of read samples
	 */
	private byte[] byteBuffer;

	/**
	 * the index of the first buffered sample
	 */
	private int minBufferedSample;

	/**
	 * the index of the last buffered sample
	 */
	private int maxBufferedSample;

	/**
	 * the wrapper for the buffer of read samples
	 */
	private ByteBuffer bBuffer;

	/**
	 * the size of the sample in bytes
	 */
	private int sampleByteWidth;

	/**
	 * Constructor. Creates the source of samples for the multichannel raw
	 * signal based on the file with that signal.
	 * @param file the file with the signal
	 * @param channelCount number of channels in the signal
	 * @param samplingFrequency number of samples per second
	 * @param sampleType the {@link RawSignalSampleType type} of signal
	 * samples in the file
	 * @param byteOrder the {@link RawSignalByteOrder order} of bytes
	 * in the signal file
	 * @throws IOException if there is an error while reading samples from
	 * file
	 */
	public RawSignalSampleSource(File file, int channelCount, float samplingFrequency, RawSignalSampleType sampleType, RawSignalByteOrder byteOrder) throws IOException {
		super(file, channelCount, samplingFrequency);
		this.sampleType = sampleType;
		this.byteOrder = byteOrder;

		randomAccessFile = new RandomAccessFile(file, "r");
		sampleByteWidth = sampleType.getByteWidth();

		sampleCount = (int)(file.length() / (channelCount * sampleByteWidth));
	}

	/**
	 * Closes the file with the signal.
	 */
	@Override
	public void close() {
		if (randomAccessFile != null) {
			try {
				randomAccessFile.close();
			} catch (IOException ex) {
				// ignore
			} finally {
				randomAccessFile = null;
			}
		}
	}

	@Override
	protected BaseSignalSampleSource duplicateInternal() throws IOException {
		return new RawSignalSampleSource(getFile(), getChannelCount(), getSamplingFrequency(), sampleType, byteOrder);
	}

	/**
	 * Returns the {@link RawSignalSampleType type} of the signal sample
	 * @return the type of the signal sample
	 */
	public RawSignalSampleType getSampleType() {
		return sampleType;
	}

	/**
	 * Returns the {@link RawSignalByteOrder order} of bytes in the file
	 * with signal
	 * @return the order of bytes in the file with signal
	 */
	public RawSignalByteOrder getByteOrder() {
		return byteOrder;
	}

	/**
	 * Returns the given number of samples for a given channel starting
	 * from a given position in time.
	 * If it is possible uses buffer, if not (or only partially) reads the
	 * data from file (random access).
	 * @param channel the number of channel
	 * @param target the array to which results will be written starting
	 * from position <code>arrayOffset</code>
	 * @param signalOffset the position (in time) in the signal starting
	 * from which samples will be returned
	 * @param count the number of samples to be returned
	 * @param arrayOffset the offset in <code>target</code> array starting
	 * from which samples will be written
	 * @throws IndexOutOfBoundsException if bad channel number is given
	 * or samples of requested indexes are not in the signal
	 * or the requested part of the signal doesn't fit in the
	 * <code>target<\code> array
	 */
	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		synchronized (this) {

			int channelCount = getChannelCount();
			if (channel < 0 || channel >= channelCount) {
				throw new IndexOutOfBoundsException("Bad channel number [" + channel + "]");
			}
			if ((signalOffset < 0) || ((signalOffset + count) > sampleCount)) {
				throw new IndexOutOfBoundsException("Signal range [" + signalOffset + ":" + count + "] doesn't fit in the signal");
			}
			if ((arrayOffset < 0) || ((arrayOffset + count) > target.length)) {
				throw new IndexOutOfBoundsException("Target range [" + arrayOffset + ":" + count + "] doesn't fit in the target array");
			}

			int targetOffset;
			int sampleSize = channelCount * sampleByteWidth;

			// try to use existing mutiplexing buffer
			if (byteBuffer != null && minBufferedSample <= signalOffset && maxBufferedSample >= (signalOffset+count-1)) {

				targetOffset = (signalOffset-minBufferedSample) * channelCount;

			} else {

				byteBuffer = new byte[count * sampleSize];
				minBufferedSample = signalOffset;
				maxBufferedSample = signalOffset + count - 1;

				try {
					long seekOffset = (long) signalOffset * (long) sampleSize;
					randomAccessFile.seek(seekOffset);
					randomAccessFile.readFully(byteBuffer);
				} catch (IOException ex) {
					byteBuffer = null;
					logger.error("Failed to read samples, filling the array with zero and exiting", ex);
					for (int i=0; i<count; i++) {
						target[arrayOffset+i] = 0.0F;
					}
					return;
				}

				bBuffer = ByteBuffer.wrap(byteBuffer).order(byteOrder.getByteOrder());

				targetOffset = 0;

			}

			int sample = channel;
			int i;

			switch (sampleType) {

			case DOUBLE :
				DoubleBuffer doubleBuffer = bBuffer.asDoubleBuffer();
				for (i=0; i<count; i++) {
					target[arrayOffset+i] = performCalibration(channel, doubleBuffer.get(targetOffset+sample));
					sample += channelCount;
				}
				break;

			case FLOAT :
				FloatBuffer floatBuffer = bBuffer.asFloatBuffer();
				for (i=0; i<count; i++) {
					target[arrayOffset+i] = performCalibration(channel, floatBuffer.get(targetOffset+sample));
					sample += channelCount;
				}
				break;

			case INT :
				IntBuffer intBuffer = bBuffer.asIntBuffer();
				for (i=0; i<count; i++) {
					target[arrayOffset+i] = performCalibration(channel, intBuffer.get(targetOffset+sample));
					sample += channelCount;
				}
				break;

			case SHORT :
				ShortBuffer shortBuffer = bBuffer.asShortBuffer();
				for (i=0; i<count; i++) {
					target[arrayOffset+i] = performCalibration(channel, shortBuffer.get(targetOffset+sample));
					sample += channelCount;
				}
				break;

			}

		}
	}
}
