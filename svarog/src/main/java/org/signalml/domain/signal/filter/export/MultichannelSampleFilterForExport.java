package org.signalml.domain.signal.filter.export;

import java.io.File;
import java.io.IOException;

import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.filter.MultichannelSampleFilter;
import org.signalml.domain.signal.raw.RawSignalSampleSource;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;

public class MultichannelSampleFilterForExport extends MultichannelSampleFilter implements SignalWriterMonitor {

	private File inputFile = new File("export2.bin.tmp");
	private File outputFile = new File("export1.bin.tmp");

	private RawSignalSampleSource resultSampleSource;
	private RawSignalWriter rawSignalWriter = new RawSignalWriter();
	private SignalWriterMonitor signalWriterMonitor;

	private int filteringState = 0;
	private boolean isFiltFiltEnabled;

	public MultichannelSampleFilterForExport(MultichannelSampleSource source, Montage montage) throws BadFilterParametersException, IOException { //assume its ASSEMBLED sampleSource
		super(source);
		this.currentMontage = montage;
		this.isFiltFiltEnabled = montage.isFiltfiltEnabled();
	}

	MultichannelSampleFilterForExport(MultichannelSampleSource source, Montage montage, RawSignalWriter rawSignalWriter) throws BadFilterParametersException, IOException { //assume its ASSEMBLED sampleSource
		super(source);
		this.currentMontage = montage;
		this.rawSignalWriter = rawSignalWriter;
		this.isFiltFiltEnabled = montage.isFiltfiltEnabled();
	}

	public RawSignalWriter getRawSignalWriter() {
		return rawSignalWriter;
	}

	public void setSignalWriterMonitor(SignalWriterMonitor signalWriterMonitor) {
		this.signalWriterMonitor = signalWriterMonitor;
	}

	public void prepareFilteredData() throws BadFilterParametersException, IOException {

		MultichannelSampleSource inputSource = source;

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
				filter = new FFTMultichannelSingleFilterForExport(inputSource, (FFTSampleFilter) sampleFilter,
						currentMontage.getFilteringExclusionArray(i));
			else
				filter = new IIRMultichannelSingleFilterForExport(inputSource, (TimeDomainSampleFilter) sampleFilter,
						currentMontage.getFilteringExclusionArray(i), isFiltFiltEnabled,
						this);

			filteringState++;

			//switch files
			rawSignalWriter.writeSignal(outputFile, filter, getSignalExportDescriptor(), this);
			filteringState++;

		}

		if (currentMontage.getSampleFilterCount() > 0) {
			inputFile.delete();
			resultSampleSource = createRawSignalSampleSource(outputFile, source);
		}
	}

	protected static RawSignalSampleSource createRawSignalSampleSource(File file, MultichannelSampleSource source) throws IOException {
		return new RawSignalSampleSource(file, source.getChannelCount(), source.getSamplingFrequency(), getSignalExportDescriptor().getSampleType(), getSignalExportDescriptor().getByteOrder());
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

		if (signalOffset + count == this.getSampleCount(channel)) {
			//after writing the last samples delete these files
			inputFile.delete();
			outputFile.delete();
		}
	}

	@Override
	public void setProcessedSampleCount(int sampleCount) {
		float realSampleCount = (float)(source.getSampleCount(0) * filteringState + sampleCount);
		realSampleCount = realSampleCount / (2 * currentMontage.getSampleFilterCount());
		if (signalWriterMonitor != null	)
			signalWriterMonitor.setProcessedSampleCount((int)realSampleCount);
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRequestingAbort() {
		// TODO Auto-generated method stub
		return false;
	}

}
