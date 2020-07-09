package org.signalml.domain.signal.filter.fft;

import org.junit.Test;
import org.signalml.BaseTestCase;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;

public class FFTFilterEngineForExportTest extends BaseTestCase {

	@Test
	public void testFiltering() {

		int channelCount = 2;
		int sampleCount = 100;

		double[][] originalSamples = new double[channelCount][sampleCount];

		for (int channel = 0; channel < channelCount; channel++)
			for (int i = 0; i < sampleCount; i++)
				originalSamples[channel][i] = Math.random();

		DoubleArraySampleSource sampleSource = new DoubleArraySampleSource(originalSamples, channelCount, sampleCount);
		ChannelSelectorSampleSource channelOneSampleSource = new ChannelSelectorSampleSource(sampleSource, 0);

		FFTSampleFilter fftFilter = new FFTSampleFilter(true);
		fftFilter.setRange(fftFilter.new Range(50.0F, 128.0F, 0.0));

		FFTFilterEngineForExport engine = new FFTFilterEngineForExport(channelOneSampleSource, fftFilter);

		double[] filteringResult = new double[sampleCount];
		engine.getSamples(filteringResult, 0, sampleCount, 0);
	}
}
