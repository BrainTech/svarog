package org.signalml.domain.signal.filter.export;

import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.filter.SinglechannelSampleFilterEngine;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.samplesource.SampleSourceEngine;
import org.signalml.math.iirdesigner.BadFilterParametersException;

/**
 * A sample source which is able to filter a multichannel sample source with a single
 * filter.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractMultichannelSingleFilterForExport extends MultichannelSampleProcessor {

	/**
	 * Engines for filtering each channel of the sample source.
	 */
	protected SampleSourceEngine[] filterEngines;

	/**
	 * The filter that should be used to filter the data.
	 */
	protected SampleFilterDefinition definition;

	/**
	 * Constructor.
	 * @param source the sample source to be filtered.
	 * @param definition the definition of the filter to be used.
	 * @param filterExclusionArray determines which channels should not be filtered.
	 * @throws BadFilterParametersException
	 */
	public AbstractMultichannelSingleFilterForExport(MultichannelSampleSource source, SampleFilterDefinition definition, boolean[] filterExclusionArray) throws BadFilterParametersException {
		super(source);
		this.definition = definition;
	}

	protected void createEngines(boolean[] filterExclusionArray) throws BadFilterParametersException {
		filterEngines = new SinglechannelSampleFilterEngine[this.source.getChannelCount()];
		for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
			if (!filterExclusionArray[channelNumber]) {
				createEngine(channelNumber, filterExclusionArray);
			}
		}
	}

	protected abstract void createEngine(int channelNumber, boolean[] filterExclusionArray) throws BadFilterParametersException;

	@Override
	public long getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		SampleSourceEngine engine = filterEngines[channel];

		if (engine == null)
			return source.getSamples(channel, target, signalOffset, count, arrayOffset);
		else
			return engine.getSamples(target, signalOffset, count, arrayOffset);
	}

	@Override
	public int getSampleCount(int channel) {

		SampleSourceEngine engine = filterEngines[channel];

		if (engine == null)
			return source.getSampleCount(channel);
		else
			return engine.getSampleCount();
	}

}