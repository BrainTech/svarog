package org.signalml.domain.signal.filter.export;

import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.filter.SinglechannelSampleFilter;
import org.signalml.domain.signal.filter.fft.FFTFilterEngineForExport;
import org.signalml.domain.signal.filter.iir.ExportIIRSinglechannelSampleFilter;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.IIRDesigner;

public class MultichannelSingleFilterForExport extends MultichannelSampleProcessor {

	private SinglechannelSampleFilter[] timeDomainFilterEngines;
	private SampleFilterDefinition definition;

	private FilterCoefficients coefficients;

	public MultichannelSingleFilterForExport(MultichannelSampleSource source, SampleFilterDefinition definition, boolean[] filterExclusionArray) throws BadFilterParametersException {
		super(source);

		this.definition = definition;

		timeDomainFilterEngines = new SinglechannelSampleFilter[source.getChannelCount()];
		for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
			createEngine(channelNumber, filterExclusionArray);
		}
	}

	protected void createEngine(int channelNumber, boolean[] filterExclusionArray) throws BadFilterParametersException {
		if (!filterExclusionArray[channelNumber]) {
			ChannelSelectorSampleSource input = new ChannelSelectorSampleSource(source, channelNumber);

			if (definition instanceof TimeDomainSampleFilter) {
				timeDomainFilterEngines[channelNumber] = new ExportIIRSinglechannelSampleFilter(input, getFilterCoefficients());
			} else {
				timeDomainFilterEngines[channelNumber] = new FFTFilterEngineForExport(input, (FFTSampleFilter) definition);
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

		SinglechannelSampleFilter engine = timeDomainFilterEngines[channel];

		if (engine == null)
			source.getSamples(channel, target, signalOffset, count, arrayOffset);
		else
			engine.getSamples(target, signalOffset, count, arrayOffset);
	}

}
