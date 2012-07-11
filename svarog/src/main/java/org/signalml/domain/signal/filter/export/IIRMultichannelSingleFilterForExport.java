package org.signalml.domain.signal.filter.export;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.filter.iir.ExportIIRSinglechannelSampleFilter;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.IIRDesigner;

public class IIRMultichannelSingleFilterForExport extends AbstractMultichannelSingleFilterForExport {

	private FilterCoefficients coefficients;

	public IIRMultichannelSingleFilterForExport(MultichannelSampleSource source, TimeDomainSampleFilter definition, boolean[] filterExclusionArray) throws BadFilterParametersException {
		super(source, definition, filterExclusionArray);
	}

	@Override
	protected void createEngine(int channelNumber, boolean[] filterExclusionArray) throws BadFilterParametersException {
		ChannelSelectorSampleSource input = new ChannelSelectorSampleSource(source, channelNumber);
		filterEngines[channelNumber] = new ExportIIRSinglechannelSampleFilter(input, getFilterCoefficients());
	}

	protected FilterCoefficients getFilterCoefficients() throws BadFilterParametersException {
		if (coefficients == null) {
			coefficients = IIRDesigner.designDigitalFilter((TimeDomainSampleFilter) definition);
		}
		return coefficients;
	}

}
