package org.signalml.domain.signal.filter.export;

import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.signal.filter.fft.FFTFilterEngineForExport;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;

/**
 * A sample processor that is able to filter a multichannel sample source with a single
 * FFT filter.
 *
 * @author Piotr Szachewicz
 */
public class FFTMultichannelSingleFilterForExport extends AbstractMultichannelSingleFilterForExport {

	public FFTMultichannelSingleFilterForExport(MultichannelSampleSource source, FFTSampleFilter definition, boolean[] filterExclusionArray) throws BadFilterParametersException {
		super(source, definition, filterExclusionArray);
		createEngines(filterExclusionArray);
	}

	@Override
	protected void createEngine(int channelNumber, boolean[] filterExclusionArray) throws BadFilterParametersException {
		ChannelSelectorSampleSource input = new ChannelSelectorSampleSource(source, channelNumber);
		filterEngines[channelNumber] = new FFTFilterEngineForExport(input, (FFTSampleFilter) definition);
	}

}
