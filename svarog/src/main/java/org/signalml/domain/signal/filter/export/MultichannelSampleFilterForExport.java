package org.signalml.domain.signal.filter.export;

import java.io.File;
import java.io.IOException;

import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.filter.MultichannelSampleFilter;
import org.signalml.domain.signal.raw.RawSignalSampleSource;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;

public class MultichannelSampleFilterForExport extends MultichannelSampleFilter {

	private RawSignalSampleSource resultSampleSource;

	private RawSignalWriter rawSignalWriter = new RawSignalWriter();

	public MultichannelSampleFilterForExport(MultichannelSampleSource source, Montage montage) throws BadFilterParametersException, IOException { //assume its ASSEMBLED sampleSource
		super(source);
		this.currentMontage = montage;

		filterData();
	}

	MultichannelSampleFilterForExport(MultichannelSampleSource source, Montage montage, RawSignalWriter rawSignalWriter) throws BadFilterParametersException, IOException { //assume its ASSEMBLED sampleSource
		super(source);
		this.currentMontage = montage;
		this.rawSignalWriter = rawSignalWriter;

		filterData();
	}

	public RawSignalWriter getRawSignalWriter() {
		return rawSignalWriter;
	}

	protected void filterData() throws BadFilterParametersException, IOException {

		MultichannelSampleSource inputSource = source;

		File inputFile = new File("export2.bin.tmp");
		File outputFile = new File("export1.bin.tmp");

		for (int i = 0; i < currentMontage.getSampleFilterCount(); i++) {
			if (!currentMontage.isFilterEnabled(i))
				continue;

			SampleFilterDefinition sampleFilter = currentMontage.getSampleFilterAt(i);

			if (i > 0) {
				//swapFiles
				File tmp = inputFile;
				inputFile = outputFile;
				outputFile = tmp;

				inputSource = createRawSignalSampleSource(inputFile, source);
			}

			AbstractMultichannelSingleFilterForExport filter;
			if (sampleFilter instanceof FFTSampleFilter)
				filter = new FFTMultichannelSingleFilterForExport(inputSource, (FFTSampleFilter) sampleFilter, currentMontage.getFilterExclusionArray(i));
			else
				filter = new IIRMultichannelSingleFilterForExport(inputSource, (TimeDomainSampleFilter) sampleFilter, currentMontage.getFilterExclusionArray(i), currentMontage.isFiltfiltEnabled());

			//switch files
			rawSignalWriter.writeSignal(outputFile, filter, getSignalExportDescriptor(), null);

		}

		if (currentMontage.getSampleFilterCount() > 0) {
			inputFile.delete();
			resultSampleSource = createRawSignalSampleSource(outputFile, source);
		}
	}

	protected static RawSignalSampleSource createRawSignalSampleSource(File file, MultichannelSampleSource source) throws IOException {
		RawSignalSampleSource rawSignalSampleSource = new RawSignalSampleSource(file, source.getChannelCount(), source.getSamplingFrequency(), getSignalExportDescriptor().getSampleType(), getSignalExportDescriptor().getByteOrder());

		int channelCount = source.getChannelCount();
		float[] gain = new float[channelCount];
		float[] offset = new float[channelCount];

		for (int i = 0; i < channelCount; i++) {
			gain[i] = 1.0F;
			offset[i] = 0.0F;
		}
		rawSignalSampleSource.setCalibrationGain(gain);
		rawSignalSampleSource.setCalibrationOffset(offset);

		return rawSignalSampleSource;
	}

	protected static SignalExportDescriptor getSignalExportDescriptor() {
		return new SignalExportDescriptor();
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		if (currentMontage.getSampleFilterCount() > 0) {
			resultSampleSource.getSamples(channel, target, signalOffset, count, arrayOffset);
		} else {
			source.getSamples(channel, target, signalOffset, count, arrayOffset);
		}
	}

}
