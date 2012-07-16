package org.signalml.domain.signal.filter.export;

import java.io.File;
import java.io.IOException;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.filter.iir.ExportIIRSinglechannelSampleFilter;
import org.signalml.domain.signal.filter.iir.GrowSignalSampleSource;
import org.signalml.domain.signal.filter.iir.TrimSignalSampleSource;
import org.signalml.domain.signal.raw.RawSignalSampleSource;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.raw.ReversedSampleSource;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.domain.signal.samplesource.SampleSourceEngine;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.IIRDesigner;

public class IIRMultichannelSingleFilterForExport extends AbstractMultichannelSingleFilterForExport {

	private FilterCoefficients coefficients;

	private RawSignalWriter rawSignalWriter = new RawSignalWriter();

	public IIRMultichannelSingleFilterForExport(MultichannelSampleSource inputSource, TimeDomainSampleFilter definition, boolean[] filterExclusionArray, boolean filtFiltEnabled) throws BadFilterParametersException, IOException {
		super(inputSource, definition, filterExclusionArray);

		File file1 = new File("export_11.bin.tmp");
		File file2 = new File("export_22.bin.tmp");

		int startIndex = 0;
		int endIndex = inputSource.getSampleCount(0);

		//step 1 - filtering rightwise
		filterEngines = new SampleSourceEngine[this.source.getChannelCount()];
		for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
			ChannelSelectorSampleSource input = new ChannelSelectorSampleSource(source, channelNumber);
			GrowSignalSampleSource grownSampleSource = new GrowSignalSampleSource(input, getFilterCoefficients());

			if (!filterExclusionArray[channelNumber]) {
				filterEngines[channelNumber] = new ExportIIRSinglechannelSampleFilter(grownSampleSource, getFilterCoefficients());

				int paddingSize = this.getSampleCount(channelNumber) - inputSource.getSampleCount(channelNumber);
				startIndex = paddingSize/2;
				endIndex = this.getSampleCount(channelNumber) - paddingSize/2;
			} else {
				filterEngines[channelNumber] = grownSampleSource;
			}
		}

		if (!filtFiltEnabled) {
			for (int channelNumber = 0; channelNumber < filterEngines.length; channelNumber++) {
				if (filterEngines[channelNumber] != null)
					filterEngines[channelNumber] = new TrimSignalSampleSource(filterEngines[channelNumber], startIndex, endIndex);
			}
		} else {
			rawSignalWriter.writeSignal(file1, this, MultichannelSampleFilterForExport.getSignalExportDescriptor(), null);

			//step 2 - filtering leftwise
			source = createFileSampleSource(file1);
			filterEngines = new SampleSourceEngine[this.source.getChannelCount()];
			for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
				if (!filterExclusionArray[channelNumber]) {
					ChannelSelectorSampleSource input = new ChannelSelectorSampleSource(source, channelNumber);
					ReversedSampleSource reversedSampleSource = new ReversedSampleSource(input);
					filterEngines[channelNumber] = new ExportIIRSinglechannelSampleFilter(reversedSampleSource, getFilterCoefficients());
				}
			}
			rawSignalWriter.writeSignal(file2, this, MultichannelSampleFilterForExport.getSignalExportDescriptor(), null);

			//step 3 - reverse samples
			source = createFileSampleSource(file2);
			filterEngines = new SampleSourceEngine[this.source.getChannelCount()]; //all of them are null
			for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
				SampleSource input = new ChannelSelectorSampleSource(source, channelNumber);
				if (!filterExclusionArray[channelNumber]) {
					input = new ReversedSampleSource(input);
				}
				filterEngines[channelNumber] = new TrimSignalSampleSource(input, startIndex, endIndex);
			}
		}
	}

	protected MultichannelSampleSource createFileSampleSource(File file) throws IOException {
		RawSignalSampleSource rawSignalSampleSource = new RawSignalSampleSource(file, source.getChannelCount(), source.getSamplingFrequency(), MultichannelSampleFilterForExport.getSignalExportDescriptor().getSampleType(), MultichannelSampleFilterForExport.getSignalExportDescriptor().getByteOrder());
		return rawSignalSampleSource;
	}

	protected FilterCoefficients getFilterCoefficients() throws BadFilterParametersException {
		if (coefficients == null) {
			coefficients = IIRDesigner.designDigitalFilter((TimeDomainSampleFilter) definition);
		}
		return coefficients;
	}

	@Override
	protected void createEngine(int channelNumber, boolean[] filterExclusionArray) throws BadFilterParametersException {
		//do nothing
	}

}
