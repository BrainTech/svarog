package org.signalml.domain.signal.filter.timedomain;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.IIRDesigner;

public class MultichannelExportTimeDomainSampleFilter extends MultichannelSampleProcessor {

	private TimeDomainSampleFilterExportEngine[] timeDomainFilterEngines;

	public MultichannelExportTimeDomainSampleFilter(MultichannelSampleSource source, TimeDomainSampleFilter definition, boolean[] filterExclusionArray) throws BadFilterParametersException {
		super(source);

		FilterCoefficients coefficients = IIRDesigner.designDigitalFilter(definition);

		timeDomainFilterEngines = new TimeDomainSampleFilterExportEngine[source.getChannelCount()];
		for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
			if (!filterExclusionArray[channelNumber]) {
				ChannelSelectorSampleSource input = new ChannelSelectorSampleSource(source, channelNumber);
				timeDomainFilterEngines[channelNumber] = new TimeDomainSampleFilterExportEngine(input, coefficients);
			}
		}
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		TimeDomainSampleFilterExportEngine engine = timeDomainFilterEngines[channel];

		if (engine == null)
			source.getSamples(channel, target, signalOffset, count, arrayOffset);
		else
			engine.getSamples(target, signalOffset, count, arrayOffset);
	}

}
