package org.signalml.domain.signal.filter.iir.helper;

import org.junit.Test;
import org.signalml.BaseTestCase;
import static org.signalml.SignalMLAssert.assertArrayEquals;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;

public class TrimSignalSampleSourceTest extends BaseTestCase {

	@Test
	public void testGetSamples() {

		double[][] samples = new double[][]
				{{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}};
		DoubleArraySampleSource multichannelSampleSource = new DoubleArraySampleSource(samples, samples.length, samples[0].length);
		ChannelSelectorSampleSource channelSelectorSampleSource = new ChannelSelectorSampleSource(multichannelSampleSource, 0);

		TrimSignalSampleSource trimmedSampleSource = new TrimSignalSampleSource(channelSelectorSampleSource, 3, 10);

		//test1
		double[] expected = new double[]{4, 5, 6, 7, 8, 9, 10};
		double[] actual = new double[expected.length];
		trimmedSampleSource.getSamples(actual, 0, trimmedSampleSource.getSampleCount(), 0);
		assertArrayEquals(expected, actual, 1e-5);

		//test2
		expected = new double[] {5, 6, 7, 8};
		actual = new double[expected.length];
		trimmedSampleSource.getSamples(actual, 1, 4, 0);
		assertArrayEquals(expected, actual, 1e-5);
	}

}
