package org.signalml.domain.signal.filter.export;

import static org.signalml.SignalMLAssert.assertArrayEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.signalml.app.document.RawSignalDocument;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.filter.MultichannelSampleFilter;
import org.signalml.domain.signal.filter.iir.OfflineIIRSinglechannelSampleFilter;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.math.iirdesigner.ApproximationFunctionType;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.FilterType;
import org.signalml.math.iirdesigner.IIRDesigner;
import org.signalml.plugin.export.SignalMLException;

public class MultichannelSampleFilterForExportTest {

	private RawSignalDocument originalSamplesRawSignalDocument;
	private double[][] originalSamples;

	private int channelCount;
	private int sampleCount;

	private TimeDomainSampleFilter timeDomainFilter1;
	private TimeDomainSampleFilter timeDomainFilter2;

	public MultichannelSampleFilterForExportTest() throws SignalMLException, IOException {
		channelCount = 2;
		sampleCount = 100;

		originalSamples = new double[channelCount][sampleCount];

		for (int channel = 0; channel < channelCount; channel++)
			for (int i = 0; i < sampleCount; i++)
				originalSamples[channel][i] = Math.random();

		DoubleArraySampleSource sampleSource = new DoubleArraySampleSource(originalSamples, channelCount, sampleCount);

		RawSignalWriter rawSignalWriter = new RawSignalWriter();
		SignalExportDescriptor signalExportDescriptor = new SignalExportDescriptor();
		rawSignalWriter.writeSignal(new File("output.bin"), sampleSource, signalExportDescriptor, null);

		createFilters();
		createRawSignalDocument();
	}

	protected void createFilters() {
		 timeDomainFilter1 = new TimeDomainSampleFilter(FilterType.HIGHPASS,
					ApproximationFunctionType.BUTTERWORTH,
					new double[] {5.0, 0.0}, new double[] {1.5, 0.0}, 3.0, 10.00);
		 timeDomainFilter1.setSamplingFrequency(128.0);

		 timeDomainFilter2 = new TimeDomainSampleFilter(FilterType.LOWPASS,
					ApproximationFunctionType.BUTTERWORTH,
					new double[] {30.0, 0.0}, new double[] {60.0, 0.0}, 3.0, 10.00);
		 timeDomainFilter2.setSamplingFrequency(128.0);
	}

	public RawSignalDocument createRawSignalDocument() throws SignalMLException, IOException {
		//raw signal document
		RawSignalDescriptor descriptor = new RawSignalDescriptor();

		descriptor.setSamplingFrequency(128.0F);
		descriptor.setChannelCount(channelCount);
		descriptor.setSampleCount(sampleCount);
		descriptor.setChannelLabels(new String[] { "a", "b"});
		descriptor.setSampleType(RawSignalSampleType.FLOAT);
		descriptor.setByteOrder(RawSignalByteOrder.LITTLE_ENDIAN);
		descriptor.setCalibrationGain(1.0F);
		descriptor.setCalibrationOffset(0.0F);

		originalSamplesRawSignalDocument = new RawSignalDocument(descriptor);
		originalSamplesRawSignalDocument.setBackingFile(new File("output.bin"));
		originalSamplesRawSignalDocument.openDocument();

		return originalSamplesRawSignalDocument;
	}

	@Test
	public void testFilterSignalWithOneFilter() throws IOException, BadFilterParametersException, SignalMLException {
		//add one filter
		originalSamplesRawSignalDocument.getMontage().addSampleFilter(timeDomainFilter1);
		originalSamplesRawSignalDocument.getMontage().setFiltfiltEnabled(false);

		//test
		RawSignalWriter rawSignalWriter = new RawSignalWriter();
		rawSignalWriter.setMaximumBufferSize(10);
		MultichannelSampleFilterForExport filteredSampleSource = new MultichannelSampleFilterForExport(originalSamplesRawSignalDocument.getSampleSource(), originalSamplesRawSignalDocument.getMontage(), rawSignalWriter);

		//export
		double[][] filteredExportSamples = new double[channelCount][sampleCount];
		filteredSampleSource.getSamples(0, filteredExportSamples[0], 0, sampleCount, 0);
		filteredSampleSource.getSamples(1, filteredExportSamples[1], 0, sampleCount, 0);

		//normal
		FilterCoefficients coefficients = IIRDesigner.designDigitalFilter(timeDomainFilter1);

		ChannelSelectorSampleSource channel0SampleSource = new ChannelSelectorSampleSource(originalSamplesRawSignalDocument.getSampleSource(), 0);
		OfflineIIRSinglechannelSampleFilter offlineSampleFilter = new OfflineIIRSinglechannelSampleFilter(channel0SampleSource, coefficients);
		double[][] filteredSamples = new double[channelCount][sampleCount];
		offlineSampleFilter.getSamples(filteredSamples[0], 0, sampleCount, 0);

		ChannelSelectorSampleSource channel1SampleSource = new ChannelSelectorSampleSource(originalSamplesRawSignalDocument.getSampleSource(), 1);
		offlineSampleFilter = new OfflineIIRSinglechannelSampleFilter(channel1SampleSource, coefficients);
		offlineSampleFilter.getSamples(filteredSamples[1], 0, sampleCount, 0);

		//compare samples filtered all at once with the ones that were filtered using ExportFilteredSignalSampleSource
		assertArrayEquals(filteredSamples, filteredExportSamples, 1e-5);

		originalSamplesRawSignalDocument.closeDocument();
	}

	@Test
	public void testFilterSignalWithTwoFilters() throws IOException, BadFilterParametersException, SignalMLException {
		//add one filter
		originalSamplesRawSignalDocument.getMontage().addSampleFilter(timeDomainFilter1);
		originalSamplesRawSignalDocument.getMontage().addSampleFilter(timeDomainFilter2);
		originalSamplesRawSignalDocument.getMontage().setFiltfiltEnabled(false);

		//test
		RawSignalWriter rawSignalWriter = new RawSignalWriter();
		rawSignalWriter.setMaximumBufferSize(10);
		MultichannelSampleFilterForExport filteredSampleSource = new MultichannelSampleFilterForExport(originalSamplesRawSignalDocument.getSampleSource(), originalSamplesRawSignalDocument.getMontage(), rawSignalWriter);

		//export
		double[][] filteredExportSamples = new double[channelCount][sampleCount];
		filteredSampleSource.getSamples(0, filteredExportSamples[0], 0, sampleCount, 0);
		filteredSampleSource.getSamples(1, filteredExportSamples[1], 0, sampleCount, 0);

		//offline
		MultichannelSampleFilter filter = new MultichannelSampleFilter(originalSamplesRawSignalDocument.getSampleSource());
		Montage filteredMontage = new Montage(originalSamplesRawSignalDocument.getMontage());
		filter.setCurrentMontage(filteredMontage);

		double[][] filteredSamples = new double[channelCount][sampleCount];
		filter.getSamples(0, filteredSamples[0], 0, sampleCount, 0);
		filter.getSamples(1, filteredSamples[1], 0, sampleCount, 0);

		//compare samples filtered all at once with the ones that were filtered using ExportFilteredSignalSampleSource
		assertArrayEquals(filteredSamples, filteredExportSamples, 1e-5);

		originalSamplesRawSignalDocument.closeDocument();
	}

}
