package org.signalml.domain.signal.filter.iir;

import static org.junit.Assert.assertEquals;
import static org.signalml.SignalMLAssert.assertArrayEquals;

import org.junit.Test;
import org.signalml.domain.signal.filter.TestingSignals;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.FilterCoefficients;

/**
 * This class performs unit tests on the {@link OnlineIIRSinglechannelSampleFilter} class.
 *
 * @author Piotr Szachewicz
 */
public class OnlineIIRSinglechannelSampleFilterTest extends AbstractIIRSinglechannelSampleFilterTest {

	private OnlineIIRSinglechannelSampleFilter engine;

	@Override
	protected OnlineIIRSinglechannelSampleFilter getEngine(MultichannelSampleSource source, FilterCoefficients coefficients) {
		return new OnlineIIRSinglechannelSampleFilter(new ChannelSelectorSampleSource(source,0), coefficients);
	}

	/**
	 * Test method for {@link AbstractIIRSinglechannelSampleFilter#getUnfilteredSamplesCache(int)}.
	 */
	@Test
	public void testGetUnfilteredSamplesCache() {

		coefficients = new FilterCoefficients(new double[] {1.0,0.0,0.0,0.0}, new double[] {1.0,0.0,0.0,0.0});
		engine = getEngine(source, coefficients);

		//testing empty then full
		float[] samples = new float[TEST_SAMPLE_COUNT];
		for (int i = 0; i < TEST_SAMPLE_COUNT; i++) {
			samples[i] = (float) Math.random();
		}

		for (int i = 0; i < TEST_SAMPLE_COUNT; i++)
			source.addSamples(new float[] {samples[i]});

		double[] uCache = engine.getUnfilteredSamplesCache(source.getSampleCount(0));
		for (int i = 0; i < TEST_SAMPLE_COUNT; i++) {
			if (samples.length - i - 1 >= 0)
				assertEquals(uCache[uCache.length - i - 1], samples[samples.length - i - 1], 0.000001);
			else
				assertEquals(uCache[uCache.length - i], 0.0, 0.00000001);
		}

		//adding 2 samples
		float[] newSamples = new float[] {(float) Math.random(), (float) Math.random()};
		source.addSamples(new float[] {newSamples[0]});
		source.addSamples(new float[] {newSamples[1]});

		uCache = engine.getUnfilteredSamplesCache(2);

		assertEquals(uCache[0], samples[samples.length-3], 0.00001);
		assertEquals(uCache[1], samples[samples.length-2], 0.00001);
		assertEquals(uCache[2], samples[samples.length-1], 0.00001);
		assertEquals(uCache[3], newSamples[0], 0.00001);
		assertEquals(uCache[4], newSamples[1], 0.00001);

		//one more sample added
		float[] newNewSamples = new float[] {(float) Math.random()};
		source.addSamples(newNewSamples);
		uCache = engine.getUnfilteredSamplesCache(1);

		assertEquals(uCache[0], samples[samples.length - 1], 0.00001);
		assertEquals(uCache[1], newSamples[0], 0.00001);
		assertEquals(uCache[2], newSamples[1], 0.00001);
		assertEquals(uCache[3], newNewSamples[0], 0.00001);
	}

	/**
	 * Test method for {@link AbstractIIRSinglechannelSampleFilter#getFilteredSamplesCache(int)}.
	 */
	@Test
	public void testGetFilteredSamplesCache() {

		coefficients = new FilterCoefficients(new double[] {1.0, 0.0, 0.0, 0.0},
											  new double[] {1.0, 0.0, 0.0, 0.0});

		engine = getEngine(source, coefficients);

		double[] fCache = engine.getFilteredSamplesCache(source.getSampleCount(0));
		assertEquals(fCache.length, source.getSampleCount(0) + 3, 0.00001);

		for (int i = 0; i < fCache.length; i++)
			assertEquals(fCache[i], 0.0, 0.000001);

		//adding one sample
		double[] samples = new double[] {Math.random()};
		engine.filtered.addSamples(samples);
		double x[] = new double[engine.filtered.getSampleCount()];
		engine.filtered.getSamples(x, 0, engine.filtered.getSampleCount(), 0);

		fCache = engine.getFilteredSamplesCache(2);

		assertEquals(fCache.length, 3 + 2, 0.000001);
		assertEquals(fCache[0], 0.0, 0.000001);
		assertEquals(fCache[1], 0.0, 0.000001);
		assertEquals(fCache[2], samples[0], 0.000001);
		assertEquals(fCache[3], 0.0, 0.000001);
		assertEquals(fCache[4], 0.0, 0.000001);

		//added 2 filtered
		double[] newFilteredSamples = new double[2];
		newFilteredSamples[0] = Math.random();
		newFilteredSamples[1] = Math.random();
		engine.filtered.addSamples(new double[] {newFilteredSamples[0]});
		engine.filtered.addSamples(new double[] {newFilteredSamples[1]});

		fCache = engine.getFilteredSamplesCache(1);
		assertEquals(fCache.length, 3 + 1, 0.000001);
		assertEquals(fCache[0], samples[0], 0.000001);
		assertEquals(fCache[1], newFilteredSamples[0],0.000001);
		assertEquals(fCache[2], newFilteredSamples[1],0.000001);
		assertEquals(fCache[3], 0.0, 0.000001);

		//added 1 filtered
		double[] new3 = new double[1];
		new3[0] = Math.random();
		engine.filtered.addSamples(new3);

		fCache = engine.getFilteredSamplesCache(3);
		assertEquals(fCache.length, 3 + 3, 0.000001);
		assertEquals(fCache[0], newFilteredSamples[0], 0.000001);
		assertEquals(fCache[1], newFilteredSamples[1], 0.000001);
		assertEquals(fCache[2], new3[0], 0.000001);
		assertEquals(fCache[3], 0.0, 0.000001);
		assertEquals(fCache[4], 0.0, 0.000001);
		assertEquals(fCache[5], 0.0, 0.000001);

	}

	/**
	 * Test method for {@link AbstractIIRSinglechannelSampleFilter#calculateNewFilteredSamples(double[], double[], int)}.
	 */
	@Test
	public void testCalculateNewFilteredSamples() {

		coefficients = new FilterCoefficients(new double[] {1.0, 0.0, 0.0},
											  new double[] {1.0, 0.0, 0.0});

		engine = getEngine(source, coefficients);

		//adding whole at once
		double[] samples = new double[TEST_SAMPLE_COUNT];
		for (int i = 0; i < TEST_SAMPLE_COUNT; i++) {
			samples[i] = Math.random();
			source.addSamples(new float[] {(float) samples[i]});
		}

		double[] uCache = engine.getUnfilteredSamplesCache(source.getSampleCount(0));
		double[] fCache = engine.getFilteredSamplesCache(source.getSampleCount(0));
		double[] newFiltered = engine.calculateNewFilteredSamples(uCache, fCache, TEST_SAMPLE_COUNT);

		for (int i = 0; i < newFiltered.length; i++)
			assertEquals(newFiltered[i], samples[i], 0.0001);

		//two more samples
		double[] newSamples1 = new double[] {Math.random(), Math.random()};
		source.addSamples(new float[] {(float) newSamples1[0]});
		source.addSamples(new float[] {(float) newSamples1[1]});
		uCache = engine.getUnfilteredSamplesCache(2);
		fCache = engine.getFilteredSamplesCache(2);
		newFiltered = engine.calculateNewFilteredSamples(uCache, fCache, 2);
		assertEquals(newFiltered[0], newSamples1[0], 0.00001);
		assertEquals(newFiltered[1], newSamples1[1], 0.000001);

	}

	/**
	 * Test method for {@link AbstractIIRSinglechannelSampleFilter#filter(double[], double[], double[])}.
	 */
	@Test
	public void testFilterUsingCache() {
		//test1
		double[] bCoefficients = new double[] {0.6, 0.22};
		double[] aCoefficients = new double[] {1, 0.3,  0.4};
		double[] input = new double[] {1, 2, 3, 4, 5, 6, 7, 8};
		double[] filtered = OnlineIIRSinglechannelSampleFilter.filterUsingCache(bCoefficients, aCoefficients, input);

		assertArrayEquals(new double[] {0.6, 1.24, 1.628, 2.0756, 2.60612, 3.087924,
										3.5511748, 4.03947796
									   }, filtered, 0.0001);

		//test 2
		bCoefficients = new double[]
		{0.00041655,  0.00124964,  0.00124964,  0.00041655};
		aCoefficients = new double[]
		{1.        , -2.6861574 ,  2.41965511, -0.73016535};
		double[] expected = new double[] {
			-3.51122604e-04,  -2.38131593e-03,  -8.12411562e-03,
			-1.92823059e-02,  -3.67895555e-02,  -6.09388992e-02,
			-9.14851744e-02,  -1.27775036e-01,  -1.68903987e-01,
			-2.13765619e-01,  -2.61092007e-01,  -3.09599360e-01,
			-3.58202192e-01,  -4.06031337e-01,  -4.52285424e-01,
			-4.96254027e-01,  -5.37334264e-01,  -5.74939493e-01,
			-6.08509899e-01,  -6.37580060e-01,  -6.61807439e-01,
			-6.80963281e-01,  -6.94950331e-01,  -7.03822359e-01,
			-7.07710463e-01,  -7.06751512e-01,  -7.01072375e-01,
			-6.90813313e-01,  -6.76248964e-01,  -6.57854003e-01,
			-6.36167663e-01,  -6.11682852e-01,  -5.84847851e-01,
			-5.55987815e-01,  -5.25304624e-01
		};

		filtered = OnlineIIRSinglechannelSampleFilter.filterUsingCache(bCoefficients, aCoefficients, TestingSignals.SHORT_SIGNAL);
		assertArrayEquals(expected, filtered, 1e-4);
	}

	/**
	 * Test method for {@link AbstractIIRSinglechannelSampleFilter#filter(double[], double[], double[], double[]) }
	 */
	@Test
	public void testFilterWithInitialState() {

		double[] expectedFilteringResult = new double[] {
			-0.95002814, -0.94980805, -0.94927655, -0.94834121, -0.94690188,
			-0.94483862, -0.94198763, -0.93815852, -0.93319191, -0.92692217,
			-0.9191435 , -0.90969293, -0.89861251, -0.88612545, -0.87245471,
			-0.85782158, -0.84244496, -0.82643911, -0.8098189 , -0.79256666,
			-0.77466379, -0.75608793, -0.73683938, -0.71697098, -0.6965261 ,
			-0.67547935, -0.6537334 , -0.63115479, -0.60770691, -0.58352705,
			-0.5588013 , -0.53366318, -0.50820369, -0.48239998, -0.45612131
		};
		double[] bCoefficients = new double[]
		{0.00041655,  0.00124964,  0.00124964,  0.00041655};
		double[] aCoefficients = new double[]
		{1.        , -2.6861574 ,  2.41965511, -0.73016535};

		double[] initialState = new double[] {
			-0.9496770216294348, 1.603555227411396, -0.6941059685801035
		};
		double[] actualFilteringResult = AbstractIIRSinglechannelSampleFilter.filter(bCoefficients, aCoefficients, TestingSignals.SHORT_SIGNAL, initialState);

		assertArrayEquals(expectedFilteringResult, actualFilteringResult, 1e-4);

	}

}
