package org.signalml.domain.signal.filter.timedomain;

import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.filter.SampleFilterEngine;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.IIRDesigner;

public class MultichannelExportTimeDomainSampleFilter extends MultichannelSampleProcessor {

	private SampleFilterEngine[] timeDomainFilterEngines;
	private SampleFilterDefinition definition;

	private FilterCoefficients coefficients;

	public MultichannelExportTimeDomainSampleFilter(MultichannelSampleSource source, SampleFilterDefinition definition, boolean[] filterExclusionArray) throws BadFilterParametersException {
		super(source);

		this.definition = definition;

		timeDomainFilterEngines = new SampleFilterEngine[source.getChannelCount()];
		for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
			createEngine(channelNumber, filterExclusionArray);
		}
	}

	protected void createEngine(int channelNumber, boolean[] filterExclusionArray) throws BadFilterParametersException {
		if (!filterExclusionArray[channelNumber]) {
			ChannelSelectorSampleSource input = new ChannelSelectorSampleSource(source, channelNumber);

			if (definition instanceof TimeDomainSampleFilter) {
				timeDomainFilterEngines[channelNumber] = new TimeDomainSampleFilterExportEngine(input, getFilterCoefficients());
			} else {
				timeDomainFilterEngines[channelNumber] = new FFTFilterOverlapAddEngine(input, (FFTSampleFilter) definition);
			}
		}
	}

	protected FilterCoefficients getFilterCoefficients() throws BadFilterParametersException {
		if (coefficients == null) {
			coefficients = IIRDesigner.designDigitalFilter((TimeDomainSampleFilter) definition);
		}
		return coefficients;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		SampleFilterEngine engine = timeDomainFilterEngines[channel];

		if (engine == null)
			source.getSamples(channel, target, signalOffset, count, arrayOffset);
		else
			engine.getSamples(target, signalOffset, count, arrayOffset);
	}

}
