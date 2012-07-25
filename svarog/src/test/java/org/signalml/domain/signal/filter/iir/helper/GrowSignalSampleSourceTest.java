package org.signalml.domain.signal.filter.iir.helper;

import static org.junit.Assert.assertEquals;
import static org.signalml.SignalMLAssert.assertArrayEquals;

import org.junit.Test;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.filter.TestingSignals;
import org.signalml.domain.signal.filter.iir.helper.GrowSignalSampleSource;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.math.iirdesigner.ApproximationFunctionType;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.FilterType;
import org.signalml.math.iirdesigner.IIRDesigner;
import org.signalml.math.iirdesigner.InitialStateCalculator;

public class GrowSignalSampleSourceTest {

	@Test
	public void testGetSamples() {

		double[][] samples = new double[][]
				{{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}};

		DoubleArraySampleSource multichannelSampleSource = new DoubleArraySampleSource(samples, samples.length, samples[0].length);
		ChannelSelectorSampleSource channelSampleSource = new ChannelSelectorSampleSource(multichannelSampleSource, 0);

		FilterCoefficients filterCoefficients = new FilterCoefficients(new double[] {1.0, 0.3}, new double[] {1.0});
		GrowSignalSampleSource growSignalSampleSource = new GrowSignalSampleSource(channelSampleSource, filterCoefficients);

		double[] expectedGrownSignal = new double[] {
				-5, -4, -3, -2, -1,
				0,  1,  2,  3,  4,
				5,  6,  7,  8,  9,
				10, 11, 12, 13, 14,
				15, 16, 17, 18, 19,
				20};

		//whole signal
		double[] actualGrownSignal = new double[expectedGrownSignal.length];
		assertEquals(expectedGrownSignal.length, growSignalSampleSource.getSampleCount());
		growSignalSampleSource.getSamples(actualGrownSignal, 0, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//only left added
		int signalOffset = 1;
		int sampleCount = 3;
		actualGrownSignal = new double[sampleCount];
		expectedGrownSignal = new double[] {-4, -3, -2};
		growSignalSampleSource.getSamples(actualGrownSignal, signalOffset, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//left + center
		signalOffset = 3;
		sampleCount = 6;
		actualGrownSignal = new double[sampleCount];
		expectedGrownSignal = new double[] {-2, -1,  0,  1,  2,  3};
		growSignalSampleSource.getSamples(actualGrownSignal, signalOffset, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//left + center (small margin)
		signalOffset = 4;
		sampleCount = 6;
		actualGrownSignal = new double[sampleCount];
		expectedGrownSignal = new double[] {-1,  0,  1,  2,  3, 4};
		growSignalSampleSource.getSamples(actualGrownSignal, signalOffset, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//only center
		signalOffset = 5;
		sampleCount = 6;
		actualGrownSignal = new double[sampleCount];
		expectedGrownSignal = new double[] {0,  1,  2,  3, 4, 5};
		growSignalSampleSource.getSamples(actualGrownSignal, signalOffset, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//only center
		signalOffset = 6;
		sampleCount = 6;
		actualGrownSignal = new double[sampleCount];
		expectedGrownSignal = new double[] {1,  2,  3, 4, 5, 6};
		growSignalSampleSource.getSamples(actualGrownSignal, signalOffset, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//only center
		signalOffset = 7;
		sampleCount = 6;
		actualGrownSignal = new double[sampleCount];
		expectedGrownSignal = new double[] {2,  3, 4, 5, 6, 7};
		growSignalSampleSource.getSamples(actualGrownSignal, signalOffset, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//center + right
		signalOffset = 18;
		sampleCount = 4;
		actualGrownSignal = new double[sampleCount];
		expectedGrownSignal = new double[] {13, 14, 15, 16};
		growSignalSampleSource.getSamples(actualGrownSignal, signalOffset, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//center + right
		signalOffset = 19;
		sampleCount = 7;
		actualGrownSignal = new double[sampleCount];
		expectedGrownSignal = new double[] {14, 15, 16, 17, 18, 19, 20};
		growSignalSampleSource.getSamples(actualGrownSignal, signalOffset, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//only right
		signalOffset = 20;
		sampleCount = 5;
		actualGrownSignal = new double[sampleCount];
		expectedGrownSignal = new double[] {15, 16, 17, 18, 19};
		growSignalSampleSource.getSamples(actualGrownSignal, signalOffset, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//only right
		signalOffset = 21;
		sampleCount = 5;
		actualGrownSignal = new double[sampleCount];
		expectedGrownSignal = new double[] {16, 17, 18, 19, 20};
		growSignalSampleSource.getSamples(actualGrownSignal, signalOffset, actualGrownSignal.length, 0);
		assertArrayEquals(expectedGrownSignal, actualGrownSignal, 1e-6);

		//compare initialSTateCalc & growSignalSampleSource
		/*InitialStateCalculator initialStateCalculator = new InitialStateCalculator(filterCoefficients);
		double[] iscSignal = initialStateCalculator.growSignal(samples[0]);

		actualGrownSignal = new double[growSignalSampleSource.getSampleCount()];
		growSignalSampleSource.getSamples(actualGrownSignal, 0, actualGrownSignal.length, 0);
		assertArrayEquals(iscSignal, actualGrownSignal, 1e-6);*/
	}

	@Test
	public void compareGrowing() throws BadFilterParametersException {
		double[][] samples = new double[1][];
		samples[0] = TestingSignals.SHORT_SIGNAL;

		DoubleArraySampleSource multichannelSampleSource = new DoubleArraySampleSource(samples, samples.length, samples[0].length);
		ChannelSelectorSampleSource channelSampleSource = new ChannelSelectorSampleSource(multichannelSampleSource, 0);

		TimeDomainSampleFilter timeDomainFilter = new TimeDomainSampleFilter(FilterType.HIGHPASS,
				ApproximationFunctionType.BUTTERWORTH,
				new double[] {5.0, 0.0}, new double[] {1.5, 0.0}, 3.0, 10.00);
		timeDomainFilter.setSamplingFrequency(128.0);
		FilterCoefficients filterCoefficients = IIRDesigner.designDigitalFilter(timeDomainFilter);

		//grown
		GrowSignalSampleSource growSampleSource = new GrowSignalSampleSource(channelSampleSource, filterCoefficients);

		double[] grownSamples = new double[growSampleSource.getSampleCount()];
		growSampleSource.getSamples(grownSamples, 0, grownSamples.length, 0);

		//initial state
		InitialStateCalculator initialStateCalculator = new InitialStateCalculator(filterCoefficients);
		double[] grownSamples2 = initialStateCalculator.growSignal(samples[0]);

		assertArrayEquals(grownSamples, grownSamples2, 1e-4);
	}


}
