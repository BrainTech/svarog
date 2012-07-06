/* SampleFilterEngine.java created 2008-02-04
 *
 */

package org.signalml.domain.signal.filter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.signal.filter.fft.FFTSinglechannelSampleFilter;
import org.signalml.domain.signal.samplesource.SampleSource;

/**
 * This abstract class represents the engine of a sample filter.
 * Implements {@link SampleSource} by mapping functions from the actual source.
 * @see FFTSinglechannelSampleFilter
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SinglechannelSampleFilter implements SampleSource {

	/*
	 * the {@link SampleFilterDefinition definition} of the filter
	 */
	protected SampleFilterDefinition definition;

	/**
	 * the {@link SampleSource source} of samples
	 */
	protected SampleSource source;

	/**
	 * Constructor. Creates an engine of a filter for provided
	 * {@link SampleSource source} of samples.
	 * @param source the source of samples
	 */
	public SinglechannelSampleFilter(SampleSource source) {
		this.source = source;
	}

	/**
	 * Returs the (@link SampleFilterDefinition definition of the filter) used
	 * by the filtering engine.
	 * @return (@link SampleFilterDefinition the definition of the filter)
	 */
	public abstract SampleFilterDefinition getFilterDefinition();

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
