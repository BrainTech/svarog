package org.signalml.domain.signal.filter.export;

import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.filter.SinglechannelSampleFilterEngine;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;

public abstract class AbstractMultichannelSingleFilterForExport extends MultichannelSampleProcessor {

	protected SinglechannelSampleFilterEngine[] filterEngines;
	protected SampleFilterDefinition definition;

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
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		SinglechannelSampleFilterEngine engine = filterEngines[channel];

		if (engine == null)
			source.getSamples(channel, target, signalOffset, count, arrayOffset);
		else
			engine.getSamples(target, signalOffset, count, arrayOffset);
	}

	@Override
	public int getSampleCount(int channel) {

		SinglechannelSampleFilterEngine engine = filterEngines[channel];

		if (engine == null)
			return source.getSampleCount(channel);
		else
			return engine.getSampleCount();
	}

}