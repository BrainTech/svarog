/* SampleFilterEngine.java created 2008-02-04
 *
 */

package org.signalml.domain.signal;

/**
 * This abstract class represents the engine of a sample filter.
 * Implements {@link SampleSource} by mapping functions from the actual source.
 * @see FFTSampleFilterEngine
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SampleFilterEngine implements SampleSource {

        /**
         * the {@link SampleSource source} of samples
         */
	protected SampleSource source;

        /**
         * Constructor. Creates an engine of a filter for provided
         * {@link SampleSource source} of samples.
         * @param source the source of samples
         */
	public SampleFilterEngine(SampleSource source) {
		this.source = source;
	}

	@Override
	public float getCalibration() {
		return source.getCalibration();
	}

	@Override
	public String getLabel() {
		return source.getLabel();
	}

	@Override
	public int getSampleCount() {
		return source.getSampleCount();
	}

	@Override
	public float getSamplingFrequency() {
		return source.getSamplingFrequency();
	}

        /**
         * Returns if the actual {@link SampleSource source} of samples
         * is capable of returning its calibration
         * @return true if the actual sample source is capable of
         * returning its calibration, false otherwise
         */
	@Override
	public boolean isCalibrationCapable() {
		return source.isCalibrationCapable();
	}

        /**
         * Returns if the actual {@link SampleSource source} of samples
         * is capable of returning its channel count
         * @return true if the actual sample source is capable of
         * returning its channel count, false otherwise
         */
	@Override
	public boolean isChannelCountCapable() {
		return source.isChannelCountCapable();
	}

        /**
         * Returns if the actual {@link SampleSource source} of samples
         * is capable of returning its sampling frequency
         * @return true if the actual sample source is capable of
         * returning its sampling frequency, false otherwise
         */
	@Override
	public boolean isSamplingFrequencyCapable() {
		return source.isSamplingFrequencyCapable();
	}

}
