package org.signalml.domain.signal.filter.export;

import java.io.File;
import java.io.IOException;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.filter.iir.ExportIIRSinglechannelSampleFilter;
import org.signalml.domain.signal.filter.iir.helper.GrowSignalSampleSource;
import org.signalml.domain.signal.filter.iir.helper.TrimSignalSampleSource;
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

/**
 * This class is able to filter a {@link MultichannelSampleSource} with a single IIR filter.
 *
 * @author Piotr Szachewicz
 */
public class IIRMultichannelSingleFilterForExport extends AbstractMultichannelSingleFilterForExport implements SignalWriterMonitor {

	/**
	 * Files that are used to store temporary filtering results.
	 */
	private File temporaryFile1 = new File("exportsub1.bin.tmp");
	private File temporaryFile2 = new File("exportsub2.bin.tmp");

	/**
	 * A {@link RawSignalWriter} for writing the temporary results to files.
	 */
	private RawSignalWriter rawSignalWriter = new RawSignalWriter();

	/**
	 * The coefficients of the filter that will be used to filter the original sample source.
	 */
	private FilterCoefficients coefficients;

	/**
	 * True if the filtering was done and the filtered samples could be
	 * obtained through the getSamples method.
	 */
	private boolean filteringDone = false;

	/**
	 * The number of temporary files that were written.
	 */
	private int temporaryFilesWritten = 0;

	/**
	 * This object monitors the progress made while filtering the data.
	 */
	private SignalWriterMonitor signalWriterMonitor;

	private boolean[] filterExclusionArray;
	private boolean filtFiltEnabled;

	public IIRMultichannelSingleFilterForExport(MultichannelSampleSource inputSource, TimeDomainSampleFilter definition, boolean[] filterExclusionArray, boolean filtFiltEnabled, SignalWriterMonitor signalWriterMonitor) throws BadFilterParametersException, IOException {
		super(inputSource, definition, filterExclusionArray);
		this.signalWriterMonitor = signalWriterMonitor;
		this.filterExclusionArray = filterExclusionArray;
		this.filtFiltEnabled = filtFiltEnabled;

		prepareFilteredData(inputSource);
	}

	protected void prepareFilteredData(MultichannelSampleSource inputSource) throws BadFilterParametersException, IOException {
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
			rawSignalWriter.writeSignal(temporaryFile1, this, MultichannelSampleFilterForExport.getSignalExportDescriptor(), this);
			temporaryFilesWritten++;

			//step 2 - filtering leftwise
			source = createFileSampleSource(temporaryFile1);
			filterEngines = new SampleSourceEngine[this.source.getChannelCount()];
			for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
				if (!filterExclusionArray[channelNumber]) {
					ChannelSelectorSampleSource input = new ChannelSelectorSampleSource(source, channelNumber);
					ReversedSampleSource reversedSampleSource = new ReversedSampleSource(input);
					filterEngines[channelNumber] = new ExportIIRSinglechannelSampleFilter(reversedSampleSource, getFilterCoefficients());
				}
			}
			rawSignalWriter.writeSignal(temporaryFile2, this, MultichannelSampleFilterForExport.getSignalExportDescriptor(), this);
			temporaryFilesWritten++;

			//step 3 - reverse samples
			source = createFileSampleSource(temporaryFile2);
			filterEngines = new SampleSourceEngine[this.source.getChannelCount()]; //all of them are null
			for (int channelNumber = 0; channelNumber < source.getChannelCount(); channelNumber++) {
				SampleSource input = new ChannelSelectorSampleSource(source, channelNumber);
				if (!filterExclusionArray[channelNumber]) {
					input = new ReversedSampleSource(input);
				}
				filterEngines[channelNumber] = new TrimSignalSampleSource(input, startIndex, endIndex);
			}
		}
		filteringDone = true;
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
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		super.getSamples(channel, target, signalOffset, count, arrayOffset);

		if (signalOffset + count == this.getSampleCount(channel) && filteringDone) {
			//after writing the last samples delete these files
			temporaryFile1.delete();
			temporaryFile2.delete();
		}
	}

	@Override
	protected void createEngine(int channelNumber, boolean[] filterExclusionArray) throws BadFilterParametersException {
		//do nothing
	}

	@Override
	public void setProcessedSampleCount(int sampleCount) {
		float realSampleCount = (float)(source.getSampleCount(0) * temporaryFilesWritten + sampleCount);
		realSampleCount = realSampleCount / 2;
		if (signalWriterMonitor != null)
			signalWriterMonitor.setProcessedSampleCount((int)realSampleCount);
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isRequestingAbort() {
		return false;
	}

}
