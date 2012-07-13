package org.signalml.domain.signal.filter.export;

import static org.signalml.SignalMLAssert.assertArrayEquals;

import java.io.IOException;

import org.junit.Test;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.filter.TestingSignals;
import org.signalml.domain.signal.filter.iir.OfflineIIRSinglechannelSampleFilter;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.math.iirdesigner.ApproximationFunctionType;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterType;
import org.signalml.math.iirdesigner.IIRDesigner;

public class IIRMultichannelSingleFilterForExportTest {

	@Test
	public void testGetSamples() throws BadFilterParametersException, IOException {

		 TimeDomainSampleFilter timeDomainFilter = new TimeDomainSampleFilter(FilterType.HIGHPASS,
					ApproximationFunctionType.BUTTERWORTH,
					new double[] {5.0, 0.0}, new double[] {1.5, 0.0}, 3.0, 10.00);
		 timeDomainFilter.setSamplingFrequency(128.0);

		 double[][] signal = new double[1][];
		 signal[0] = TestingSignals.SHORT_SIGNAL;

		 DoubleArraySampleSource sampleSource = new DoubleArraySampleSource(signal, 1, signal[0].length);
		 IIRMultichannelSingleFilterForExport filterForExport = new IIRMultichannelSingleFilterForExport(sampleSource, timeDomainFilter, new boolean[] { false}, false);

		 //export
		 double[] exportResults = new double[filterForExport.getSampleCount(0)];
		 filterForExport.getSamples(0, exportResults, 0, exportResults.length, 0);

		 //offline
		 ChannelSelectorSampleSource channel0SampleSource = new ChannelSelectorSampleSource(sampleSource, 0);
		 OfflineIIRSinglechannelSampleFilter offlineFilter = new OfflineIIRSinglechannelSampleFilter(channel0SampleSource, IIRDesigner.designDigitalFilter(timeDomainFilter));

		 double[] offlineResults = new double[offlineFilter.getSampleCount()];
		 offlineFilter.getSamples(offlineResults, 0, offlineResults.length, 0);

		 assertArrayEquals(offlineResults, exportResults, 1e-4);

	}
}
