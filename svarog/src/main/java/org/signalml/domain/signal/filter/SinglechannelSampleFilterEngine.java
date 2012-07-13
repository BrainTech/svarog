/* SampleFilterEngine.java created 2008-02-04
 *
 */

package org.signalml.domain.signal.filter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.signal.filter.fft.FFTSinglechannelSampleFilter;
import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.domain.signal.samplesource.SampleSourceEngine;

/**
 * This abstract class represents the engine of a sample filter.
 * Implements {@link SampleSource} by mapping functions from the actual source.
 * @see FFTSinglechannelSampleFilter
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SinglechannelSampleFilterEngine extends SampleSourceEngine {

	/*
	 * the {@link SampleFilterDefinition definition} of the filter
	 */
	protected SampleFilterDefinition definition;

	/**
	 * Constructor. Creates an engine of a filter for provided
	 * {@link SampleSource source} of samples.
	 * @param source the source of samples
	 */
	public SinglechannelSampleFilterEngine(SampleSource source) {
		super(source);
	}

	/**
	 * Returs the (@link SampleFilterDefinition definition of the filter) used
	 * by the filtering engine.
	 * @return (@link SampleFilterDefinition the definition of the filter)
	 */
	public abstract SampleFilterDefinition getFilterDefinition();

}
