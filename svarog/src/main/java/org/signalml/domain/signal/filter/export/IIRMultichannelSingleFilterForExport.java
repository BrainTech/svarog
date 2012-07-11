package org.signalml.domain.signal.filter.export;

import java.io.File;
import java.io.IOException;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.filter.SinglechannelSampleFilter;
import org.signalml.domain.signal.filter.iir.ExportIIRSinglechannelSampleFilter;
import org.signalml.domain.signal.raw.RawSignalSampleSource;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.raw.ReversedMultichannelSampleProcessor;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.IIRDesigner;

public class IIRMultichannelSingleFilterForExport extends AbstractMultichannelSingleFilterForExport {

	private FilterCoefficients coefficients;

	private RawSignalWriter rawSignalWriter = new RawSignalWriter();

	public IIRMultichannelSingleFilterForExport(MultichannelSampleSource inputSource, TimeDomainSampleFilter definition, boolean[] filterExclusionArray, boolean filtFiltEnabled) throws BadFilterParametersException, IOException {
		super(inputSource, definition, filterExclusionArray);

		if (filtFiltEnabled) {

			File file1 = new File("export_11.bin.tmp");
			File file2 = new File("export_22.bin.tmp");

			//step 1 - filtering rightwise
			filterEngines = new SinglechannelSampleFilter[this.source.getChannelCount()];
			for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
				if (!filterExclusionArray[channelNumber]) {
					ChannelSelectorSampleSource input = new ChannelSelectorSampleSource(source, channelNumber);
					filterEngines[channelNumber] = new ExportIIRSinglechannelSampleFilter(input, getFilterCoefficients());
				}
			}

			rawSignalWriter.writeSignal(file1, this, MultichannelSampleFilterForExport.getSignalExportDescriptor(), null);

			//step 2 - filtering leftwise
			MultichannelSampleSource step1OutputSampleSource = createFileSampleSource(file1, true);
			filterEngines = new SinglechannelSampleFilter[this.source.getChannelCount()];
			for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
				if (!filterExclusionArray[channelNumber]) {
					ChannelSelectorSampleSource input = new ChannelSelectorSampleSource(step1OutputSampleSource, channelNumber);
					filterEngines[channelNumber] = new ExportIIRSinglechannelSampleFilter(input, getFilterCoefficients());
				}
			}
			rawSignalWriter.writeSignal(file2, this, MultichannelSampleFilterForExport.getSignalExportDescriptor(), null);

			//step 3 - reverse samples
			filterEngines = new SinglechannelSampleFilter[this.source.getChannelCount()]; //all of them are null
			this.source = createFileSampleSource(file2, true);
		} else {
			createEngines(filterExclusionArray);
		}
	}

	protected MultichannelSampleSource createFileSampleSource(File file, boolean reversed) throws IOException {
		RawSignalSampleSource rawSignalSampleSource = new RawSignalSampleSource(file, source.getChannelCount(), source.getSamplingFrequency(), MultichannelSampleFilterForExport.getSignalExportDescriptor().getSampleType(), MultichannelSampleFilterForExport.getSignalExportDescriptor().getByteOrder());
		if (reversed)
			return new ReversedMultichannelSampleProcessor(rawSignalSampleSource);
		else
			return rawSignalSampleSource;
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
