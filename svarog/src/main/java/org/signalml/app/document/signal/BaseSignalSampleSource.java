package org.signalml.app.document.signal;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.signalml.domain.signal.samplesource.AbstractMultichannelSampleSource;
import org.signalml.domain.signal.samplesource.OriginalMultichannelSampleSource;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.change.listeners.PluginSignalChangeListener;

/**
 * Base class for sample sources for RAW and ASCII files.
 * It implements common features for these types of files:
 * calibration, assignable channel names and sampling frequency.
 *
 * @author piotr.rozanski@onet.pl
 */
public abstract class BaseSignalSampleSource extends AbstractMultichannelSampleSource implements OriginalMultichannelSampleSource {

	private static final Logger logger = Logger.getLogger(BaseSignalSampleSource.class);

	/**
	 * the file with the signal
	 */
	private final File file;

	/**
	 * the number of signal channels
	 */
	private final int channelCount;

	/**
	 * the number of samples per seconds (in one channel)
	 */
	private float samplingFrequency;

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
	 * the number of samples in the single channel
	 */
	protected int sampleCount;

	/**
	 * The timestamp of the first sample in the signal.
	 */
	private double firstSampleTimestamp;

	/**
	 * Constructor. Creates the source of samples for the multichannel raw
	 * signal based on the file with that signal.
	 * @param file the file with the signal
	 * @param channelCount number of channels in the signal
	 * @param samplingFrequency number of samples per second
	 * @throws IOException if there is an error while reading samples from
	 * file
	 */
	public BaseSignalSampleSource(File file, int channelCount, float samplingFrequency) throws IOException {
		this.file = file;
		this.channelCount = channelCount;
		this.samplingFrequency = samplingFrequency;
	}

	/**
	 * Closes the file with the signal.
	 */
	public abstract void close();

	/**
	 * Creates the copy of this sample source.
	 * @return the copy of this sample source.
	 * @throws SignalMLException if there is an error while reading from
	 * file
	 */
	@Override
	public OriginalMultichannelSampleSource duplicate() throws SignalMLException {

		BaseSignalSampleSource newSource;
		try {
			newSource = duplicateInternal();
		} catch (IOException ex) {
			throw new SignalMLException(ex);
		}

		newSource.calibrationGain = Arrays.copyOf(calibrationGain, calibrationGain.length);
		newSource.calibrationOffset = Arrays.copyOf(calibrationOffset, calibrationOffset.length);
		if (labels != null) {
			newSource.labels = Arrays.copyOf(labels, labels.length);
		}

		return newSource;

	}

	protected abstract BaseSignalSampleSource duplicateInternal() throws IOException;

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

	protected double performCalibration(int channelNumber, double sample) {

		if (calibrationGain != null && calibrationOffset != null) {
			Float gain = calibrationGain[channelNumber];
			Float offset = calibrationOffset[channelNumber];
			return sample * gain + offset;
		}
		else
			return sample;
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

	/**
	 * Return the timestamp of the first sample in the signal.
	 * @return the timestamp of the first sample in the signal
	 */
	public double getFirstSampleTimestamp() {
		return firstSampleTimestamp;
	}

	/**
	 * Sets the timestamp of the first sample in the signal.
	 * @param value the new value of the first sample in the signal
	 */
	public void setFirstSampleTimestamp(double value) {
		this.firstSampleTimestamp = value;
	}

	@Override
	public void addSignalChangeListener(PluginSignalChangeListener listener) {
		//this sample source doesn't support signalchangelisteners.
	}
}
