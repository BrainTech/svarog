/* SignalMLCodecSampleSource.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

import org.apache.log4j.Logger;
import org.signalml.codec.SignalMLCodecException;
import org.signalml.codec.SignalMLCodecReader;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

/**
 * This class represents the source of samples from the signal described
 * in the SignalML markup language.
 *
 * @see SignalMLCodecReader
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLCodecSampleSource extends AbstractMultichannelSampleSource implements OriginalMultichannelSampleSource, ResamplableSampleSource {

	protected static final Logger logger = Logger.getLogger(SignalMLCodecSampleSource.class);

        /**
         * the {@link SignalMLCodecReader reader} that gets all informations
         * about the signal from file
         */
	private SignalMLCodecReader reader = null;

        /**
         * the number of samples per second
         */
	private float samplingFrequency;

        /**
         * the number of channels
         */
	private int channelCount;

        /**
         * the calibration of the signal
         */
	private float calibration;

        /**
         * the number of samples in the single channel
         */
        private int sampleCount;

        /**
         * if this source is capable of returning a the number of samples
         * per second
         */
	private boolean samplingFrequencyCapable = false;
        /**
         * if this source is capable of returning the number of channels
         */
	private boolean channelCountCapable = false;
        /**
         * if this source is capable of returning a calibration
         */
	private boolean calibrationCapable = false;

        /**
         * true if frequency of sampling is equal for all channels, false
         * otherwise
         */
	private boolean uniformSampling = true;

        /**
         * an array containing for each channel the number of samples per second
         */
	private float[] channelSampling;

        /**
         * an array of labels of channels
         */
	private String[] labels;

        /**
         * informs whether the sampling frequency has changed
         */
	private boolean samplingFrequencyExternal = false;
        /**
         * informs whether the channel count has changed
         */
	private boolean channelCountExternal = false;
        /**
         * informs whether the calibration has changed
         */
	private boolean calibrationExternal = false;

        /**
         * the {@link MultichannelSignalResampler resampler} of the signal
         */
	private MultichannelSignalResampler resampler = null;

        /**
         * Constructor. Creates the source of samples based on a given
         * {@link SignalMLCodecReader reader}.
         * @param reader the reader that gets all informations about signal
         * from file
         * @throws SignalMLException if codec doesn't support max offset, so
         * is unusable
         */
	public SignalMLCodecSampleSource(SignalMLCodecReader reader) throws SignalMLException {
		super();
		this.reader = reader;

		try {
			sampleCount = reader.get_max_offset() + 1;
		} catch (SignalMLCodecException ex) {
			logger.error("Codec doesn't support max offset - unusable");
			throw ex;
		}

		try {
			channelCountCapable = reader.is_number_of_channels();
		} catch (SignalMLCodecException e) {
			logger.warn("WARNING: codec doesn't support is_number_of_channels, left false");
			logger.debug("Caught exception was", e);
			channelCountCapable = false;
		}

		if (channelCountCapable) {
			try {
				channelCount = reader.get_number_of_channels();
			} catch (SignalMLCodecException e) {
				logger.warn("WARNING: codec doesn't support channel count, assumed 1");
				logger.debug("Caught exception was", e);
				channelCount = 1;
				channelCountCapable = false;
			}
		}

		try {
			samplingFrequencyCapable = reader.is_sampling_frequency();
		} catch (SignalMLCodecException e) {
			logger.warn("WARNING: codec doesn't support is_sampling_frequency, left false");
			logger.debug("Caught exception was", e);
		}

		if (samplingFrequencyCapable) {
			try {
				samplingFrequency = reader.get_sampling_frequency();
			} catch (SignalMLCodecException e) {
				logger.warn("WARNING: codec doesn't support sampling frequency, left null");
				logger.debug("Caught exception was", e);
				samplingFrequencyCapable = false;
			}

			if (samplingFrequencyCapable) {

				try {
					uniformSampling = reader.is_uniform_sampling_frequency();
				} catch (SignalMLCodecException e) {
					logger.warn("WARNING: codec doesn't support uniform sampling frequency info, left true");
					logger.debug("Caught exception was", e);
				}

				if (!uniformSampling) {

					logger.warn("WARNING: signal sampling is not uniform. Naive upsampling will be used for select channels");

					resampler = new NaiveMultichannelSignalResampler();
					collectChannelSampling();

				}

			}

		}

		try {
			this.calibrationCapable = reader.is_calibration();
		} catch (SignalMLCodecException e) {
			logger.warn("WARNING: codec doesn't support is_callibration, assumed false");
			logger.debug("Caught exception was", e);
		}

		readLabels();

	}

        /**
         * Reads the frequency of sampling (number of samples per second) for
         * each channel from file.
         */
	private void collectChannelSampling() {

		channelSampling = new float[channelCount];

		for (int i=0; i<channelCount; i++) {

			try {
				channelSampling[i] = reader.get_sampling_frequency(i);
			} catch (SignalMLCodecException e) {
				logger.warn("WARNING: codec didn't return channel sampling for channel [" + i + "] - assumed default");
				logger.debug("Caught exception was", e);
				channelSampling[i] = samplingFrequency;
			}

		}

	}

        /**
         * Reads the labels of channels from file. If some labels are missing
         * the default labels are used.
         */
	private void readLabels() {

		labels = null;

		boolean hasNames = false;
		try {
			hasNames = reader.is_channel_names();
		} catch (SignalMLCodecException e) {
			logger.warn("WARNING: codec doesn't support is_channel_names, assumed false");
			logger.debug("Caught exception was", e);
		}
		if (hasNames) {
			try {
				labels = reader.get_channel_names();
			} catch (SignalMLCodecException e) {
				logger.warn("WARNING: codec doesn't support get_channel_names");
				logger.debug("Caught exception was", e);
			}
		}

		if (labels == null) {
			return;
		}

		int i;
		for (i=0; i<labels.length; i++) {

			labels[i] = Util.trimString(labels[i]);

			if (labels[i] == null || labels[i].length() == 0) {
				labels[i] = "L" + i;
			}

		}

		if (labels.length < channelCount) {
			logger.debug("some labels missing");
			String[] newLabels = new String[channelCount];
			for (i=0; i<labels.length; i++) {
				newLabels[i] = labels[i];
			}
			for (; i<channelCount; i++) {
				newLabels[i] = "L" + i;
			}
			labels = newLabels;
		}

	}

        /**
         * Returns the {@link SignalMLCodecReader reader} that gets all
         * informations about the signal from file
         * @return the reader that gets all informations about the signal
         * from file
         */
	public SignalMLCodecReader getReader() {
		return reader;
	}

	@Override
	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

        /**
         * Returns if this source is capable of returning a calibration
         * @return true if this source is capable of returning a
         * calibration, false otherwise
         */
	@Override
	public boolean isCalibrationCapable() {
		return calibrationCapable;
	}

        /**
         * Returns if this source is capable of returning a sampling
         * frequency
         * @return true if this source is capable of returning a
         * sampling frequency, false otherwise
         */
	@Override
	public boolean isSamplingFrequencyCapable() {
		return samplingFrequencyCapable;
	}

        /**
         * Returns if this source is capable of returning a channel count
         * @return true if this source is capable of returning a
         * channel count, false otherwise
         */
	@Override
	public boolean isChannelCountCapable() {
		return channelCountCapable;
	}

	@Override
	public float getCalibration() {
		return calibration;
	}

	@Override
	public String getLabel(int channel) {
		if (channel < 0 || channel >= channelCount) {
			throw new IndexOutOfBoundsException("Bad channel number [" + channel + "]");
		}
		if (labels == null) {
			labels = new String[channelCount];
			for (int i=0; i<channelCount; i++) {
				labels[i] = "L" + (i+1);
			}
		}
		return labels[channel];
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		return channel;
	}

	@Override
	public int getSampleCount(int channel) {
		return sampleCount;
	}

        /**
         * Returns the given number of samples for a given channel starting
         * from a given position in time.
         * If sampling frequency for the channel is different from default
         * the signal is {@link MultichannelSignalResampler resampled}.
         * @param channel the number of channel
         * @param target the array to which results will be written starting
         * from position <code>arrayOffset</code>
         * @param signalOffset the position (in time) in the signal starting
         * from which samples will be returned
         * @param count the number of samples to be returned
         * @param arrayOffset the offset in <code>target</code> array starting
         * from which samples will be written
         * @throws IndexOutOfBoundsException if channel of given index doesn't
         * exist
         * or some of the requested samples are not in the signal
         * or created result doesn't fit in the <code>target</code> array
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
			if (uniformSampling || channelSampling[channel] == samplingFrequency) {
				getRawSamples(channel, target, signalOffset, count, arrayOffset);
			} else {
				resampler.resample(this, channel, target, signalOffset, count, arrayOffset, samplingFrequency, channelSampling[channel]);
			}
		}
	}

	@Override
	public void getRawSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		synchronized (this) {
			int targetIndex = arrayOffset;
			int i = signalOffset;
			int limit = signalOffset + count;
			try {
				for (; i<limit; i++) {
					target[targetIndex] = reader.getChannelSample(i, channel);
					targetIndex++;
				}
			} catch (SignalMLCodecException ex) {
				logger.error("Failed to get sample, filling the rest with zero and exiting", ex);
				for (; i<limit; i++) {
					target[targetIndex] = 0.0F;
					targetIndex++;
				}
				return;
			}
		}
	}

        /**
         * Sets the calibration to the given value and tries to update
         * it in the file.
         * @param calibration the new value of calibration
         */
	@Override
	public void setCalibration(float calibration) {
		synchronized (this) {
			if (this.calibration != calibration) {
				float last = this.calibration;
				this.calibration = calibration;
				calibrationExternal = true;
				try {
					reader.set_calibration(calibration);
				} catch (SignalMLCodecException ex) {
					logger.error("Failed to propagate calibration to the codec", ex);
				}
				pcSupport.firePropertyChange(CALIBRATION_PROPERTY, new Float(last), new Float(calibration));
			}
		}
	}

	@Override
	public void setSamplingFrequency(float samplingFrequency) {
		synchronized (this) {
			if (this.samplingFrequency != samplingFrequency) {
				float last = this.samplingFrequency;
				this.samplingFrequency = samplingFrequency;
				samplingFrequencyExternal = true;
				try {
					reader.set_sampling_frequency(samplingFrequency);
				} catch (SignalMLCodecException ex) {
					logger.error("Failed to propagate sampling frequency to the codec", ex);
				}
				if (!uniformSampling) {
					collectChannelSampling();
				}
				pcSupport.firePropertyChange(SAMPLING_FREQUENCY_PROPERTY, new Float(last), new Float(samplingFrequency));
			}
		}
	}

	@Override
	public void setChannelCount(int channelCount) {
		synchronized (this) {
			if (this.channelCount != channelCount) {
				int last = this.channelCount;
				this.channelCount = channelCount;
				channelCountExternal = true;
				try {
					reader.set_number_of_channels(channelCount);
				} catch (SignalMLCodecException ex) {
					logger.error("Failed to propagate channel count to the codec", ex);
				}
				readLabels();
				if (!uniformSampling) {
					collectChannelSampling();
				}
				pcSupport.firePropertyChange(CHANNEL_COUNT_PROPERTY, last, channelCount);
			}
		}
	}

	@Override
	public OriginalMultichannelSampleSource duplicate() throws SignalMLException {

		SignalMLCodecReader newReader = reader.getCodec().createReader();
		newReader.open(reader.getCurrentFilename());

		SignalMLCodecSampleSource duplicate = new SignalMLCodecSampleSource(newReader);
		if (channelCountExternal) {
			duplicate.setChannelCount(channelCount);
		}
		if (samplingFrequencyExternal) {
			duplicate.setSamplingFrequency(samplingFrequency);
		}
		if (calibrationExternal) {
			duplicate.setCalibration(calibration);
		}

		return duplicate;

	}

        /**
         * Closes the file and removes the <code>reader</code> from this
         * source.
         */
	@Override
	public void destroy() {
		reader.close();
		reader = null;
	}

}
