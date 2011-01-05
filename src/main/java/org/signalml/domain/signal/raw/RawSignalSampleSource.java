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
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.signalml.domain.signal.AbstractMultichannelSampleSource;
import org.signalml.domain.signal.OriginalMultichannelSampleSource;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;

/**
 * This class represents the source of samples for the raw signal.
 * Reads samples from file and uses buffering if possible.
 * Contains information about the file with the signal, the number of channels, the sampling
 * frequency, the {@link RawSignalSampleType type of samples} and
 * the {@link RawSignalByteOrder byte order}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalSampleSource extends AbstractMultichannelSampleSource implements OriginalMultichannelSampleSource {

	protected static final Logger logger = Logger.getLogger(RawSignalSampleSource.class);

        /**
         * the file with the signal
         */
	private File file;

        /**
         * the file opened for random access reading
         */
	private RandomAccessFile randomAccessFile;

        /**
         * the number of signal channels
         */
	private int channelCount;

        /**
         * the number of samples per seconds (in one channel)
         */
        private float samplingFrequency;

        /**
         * the {@link RawSignalSampleType type} of samples in the signal
         */
	private RawSignalSampleType sampleType;

        /**
         * the {@link RawSignalByteOrder order} of bytes in the file with signal
         */
        private RawSignalByteOrder byteOrder;

	/**
	 * The calibration gain for the signal - the value by which each sample
	 * value is multiplied.
	 */
	private float[] calibrationGain;

	/**
	 * The calibration offset for the signal - the value which is added
	 * to each sample value.
	 */
	private float[] calibrationOffset;

        /**
         * an array of labels of channels
         */
	private String[] labels;

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
         * the number of samples in the single channel
         */
	private int sampleCount;

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
		this.file = file;
		this.channelCount = channelCount;
		this.samplingFrequency = samplingFrequency;
		this.sampleType = sampleType;
		this.byteOrder = byteOrder;

		randomAccessFile = new RandomAccessFile(file, "r");
		sampleByteWidth = sampleType.getByteWidth();

		sampleCount = (int)(file.length() / (channelCount * sampleByteWidth));
	}

        /**
         * Closes the file with the signal.
         */
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

        /**
         * Creates the copy of this sample source.
         * @return the copy of this sample source.
         * @throws SignalMLException if there is an error while reading from
         * file
         */
	@Override
	public OriginalMultichannelSampleSource duplicate() throws SignalMLException {

		RawSignalSampleSource newSource;
		try {
			newSource = new RawSignalSampleSource(file, channelCount, samplingFrequency, sampleType, byteOrder);
		} catch (IOException ex) {
			throw new SignalMLException(ex);
		}

		newSource.calibrationGain = Arrays.copyOf(calibrationGain, calibrationGain.length);
		if (labels != null) {
			newSource.labels = Arrays.copyOf(labels, labels.length);
		}

		return newSource;

	}

        /**
         * Returns the file with the signal.
         * @return the file with the signal
         */
	public File getFile() {
		return file;
	}

        /**
         * Returns the number of samples in the single channel.
         * @return the number of samples in the single channel
         */
	public int getSampleCount() {
		return sampleCount;
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
         * Returns if the implementation is capable of returning a calibration
         * @return true because the implementation is capable of returning a
         * calibration
         */
	@Override
	public boolean isCalibrationCapable() {
		return true;
	}

        /**
         * Returns if the implementation is capable of returning a channel count
         * @return true because the implementation is capable of returning a channel
         * count
         */
	@Override
	public boolean isChannelCountCapable() {
		return true;
	}

        /**
         * Returns if the implementation is capable of returning a
         * sampling frequency
         * @return true because the implementation is capable of returning a
         * sampling frequency
         */
	@Override
	public boolean isSamplingFrequencyCapable() {
		return true;
	}

	@Override
	public float[] getCalibrationGain() {
		return calibrationGain;
	}

	@Override
	public void setCalibrationGain(float[] calibration) {
		if (!Arrays.equals(this.calibrationGain, calibration)) {
			float[] oldCalibration = this.calibrationGain;
			this.calibrationGain = calibration;

			pcSupport.firePropertyChange(CALIBRATION_PROPERTY, oldCalibration, calibration);
		}
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

	@Override
	public void setChannelCount(int channelCount) {
		throw new SanityCheckException("Changing channel count not allowed");
	}

	@Override
	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	@Override
	public void setSamplingFrequency(float samplingFrequency) {
		if (this.samplingFrequency != samplingFrequency) {
			float oldSamplingFrequency = this.samplingFrequency;
			this.samplingFrequency = samplingFrequency;

			pcSupport.firePropertyChange(SAMPLING_FREQUENCY_PROPERTY, oldSamplingFrequency, samplingFrequency);
		}
	}

        /**
         * Returns an array of labels of channels.
         * @return an array of labels of channels
         */
	public String[] getLabels() {
		return labels;
	}

        /**
         * Sets the labels of channels to given values
         * @param labels an array with labels to be set
         */
	public void setLabels(String[] labels) {
		if (this.labels != labels) {
			String[] oldLabels = this.labels;
			this.labels = labels;

			pcSupport.firePropertyChange(LABEL_PROPERTY, oldLabels, labels);
		}
	}

        /**
         * Returns the number of the channel. The same value as given
         * @param channel the number of a channel
         * @return the number of the channel, the same value as given
         */
	@Override
	public int getDocumentChannelIndex(int channel) {
		return channel;
	}

	@Override
	public String getLabel(int channel) {
		if (labels != null && channel < labels.length) {
			return labels[channel];
		}
		return "L" + (channel+1);
	}

	@Override
	public int getSampleCount(int channel) {
		return sampleCount;
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
					randomAccessFile.seek(signalOffset * sampleSize);
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
					target[arrayOffset+i] = (doubleBuffer.get(targetOffset+sample) * calibrationGain[channel] + calibrationOffset[channel]);
					sample += channelCount;
				}
				break;

			case FLOAT :
				FloatBuffer floatBuffer = bBuffer.asFloatBuffer();
				for (i=0; i<count; i++) {
					target[arrayOffset+i] = (floatBuffer.get(targetOffset+sample) * calibrationGain[channel] + calibrationOffset[channel]);
					sample += channelCount;
				}
				break;

			case INT :
				IntBuffer intBuffer = bBuffer.asIntBuffer();
				for (i=0; i<count; i++) {
					target[arrayOffset+i] = (intBuffer.get(targetOffset+sample) * calibrationGain[channel] + calibrationOffset[channel]);
					sample += channelCount;
				}
				break;

			case SHORT :
				ShortBuffer shortBuffer = bBuffer.asShortBuffer();
				for (i=0; i<count; i++) {
					target[arrayOffset+i] = (shortBuffer.get(targetOffset+sample) * calibrationGain[channel] + calibrationOffset[channel]);
					sample += channelCount;
				}
				break;

			}

		}

	}

        /**
         * Destroys this sample source. Closes the file with signal.
         */
	@Override
	public void destroy() {
		close();
	}

	@Override
	public boolean areIndividualChannelsCalibrationCapable() {
		return true;
	}

	@Override
	public void setCalibrationGain(float calibration) {
		for (int i = 0; i < calibrationGain.length; i++)
			calibrationGain[i] = calibration;
		pcSupport.firePropertyChange(CALIBRATION_PROPERTY, null, calibrationGain);

	}

	@Override
	public float getSingleCalibrationGain() {
		return calibrationGain[0];
	}

	@Override
	public float[] getCalibrationOffset() {
		return calibrationOffset;
	}

	@Override
	public void setCalibrationOffset(float calibrationOffset) {
		Arrays.fill(this.calibrationOffset, calibrationOffset);
		pcSupport.firePropertyChange(CALIBRATION_PROPERTY, null, calibrationOffset);
	}

	@Override
	public void setCalibrationOffset(float[] calibrationOffset) {
		if (!Arrays.equals(this.calibrationOffset, calibrationOffset)) {
			float[] oldCalibration = this.calibrationOffset;
			this.calibrationOffset = calibrationOffset;

			pcSupport.firePropertyChange(CALIBRATION_PROPERTY, oldCalibration, calibrationOffset);
		}

	}

	@Override
	public float getSingleCalibrationOffset() {
		return calibrationOffset[0];
	}

}
