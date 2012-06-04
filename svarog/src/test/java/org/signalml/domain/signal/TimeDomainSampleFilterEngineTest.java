/* TimeDomainSampleFilterEngineTest.java created 2010-08-28
 *
 */
package org.signalml.domain.signal;

import static org.junit.Assert.assertEquals;
import static org.signalml.SignalMLAssert.assertArrayEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.samplesource.ChannelSelectorSampleSource;
import org.signalml.domain.signal.samplesource.RoundBufferMultichannelSampleSource;
import org.signalml.math.iirdesigner.FilterCoefficients;

/**
 * This class performs unit tests on the {@link TimeDomainSampleFilterEngine TimeDomainSampleFilterEngine} class.
 *
 * @author Piotr Szachewicz
 *
 */
public class TimeDomainSampleFilterEngineTest {

	/**
	 * the number of samples used for the tests
	 */
	public static final int TEST_SAMPLE_COUNT = 10;

	/**
	 * the {@link FilterCoefficients coefficients} of the filter used for testing
	 * {@link TimeDomainSampleFilter TimeDomainSampleFilter} class.
	 */
	private FilterCoefficients coefficients;

	/**
	 * the source of samples which will be filtered by the engine
	 */
	private RoundBufferMultichannelSampleSource source;

	/**
	 * the engine used to filter the samples taken from the source
	 */
	private TimeDomainSampleFilterEngine engine;

	/**
	 * A few samples which can be used as an input data.
	 */
	private double[] shortSignal = new double[] {
		-0.84293027, -0.92374498, -0.88684131, -0.84391936, -0.87729384,
		-0.76973675, -0.77477867, -0.77924067, -0.68329653, -0.68361526,
		-0.58494503, -0.73579347, -0.72676736, -0.61426226, -0.7348077 ,
		-0.63514914, -0.61154161, -0.5558349 , -0.53932159, -0.51812691,
		-0.47357742, -0.45951363, -0.48207101, -0.40786034, -0.36886221,
		-0.32913236, -0.22319939, -0.30716421, -0.29695674, -0.30282762,
		-0.1731335 , -0.29801377, -0.04960099, -0.10052872, -0.05416476
	};

	/**
	 * Sets everything up for the tests.
	 */
	@Before
	public void setUp() {
		source = new RoundBufferMultichannelSampleSource(1, TEST_SAMPLE_COUNT);
	}

	/**
	 * Cleans up after the test.
	 */
	@After
	public void tearDown() {
		source = null;
		coefficients = null;
	}

	/**
	 * Test method for {@link TimeDomainSampleFilterEngine#getUnfilteredSamplesCache(int)}.
	 */
	@Test
	public void testGetUnfilteredSamplesCache() {

		coefficients = new FilterCoefficients(new double[] {1.0,0.0,0.0,0.0}, new double[] {1.0,0.0,0.0,0.0});
		engine = new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source,0), coefficients);

		//testing empty then full
		double[] samples = new double[TEST_SAMPLE_COUNT];
		for (int i = 0; i < TEST_SAMPLE_COUNT; i++) {
			samples[i] = Math.random();
		}

		for (int i = 0; i < TEST_SAMPLE_COUNT; i++)
			source.addSamples(new double[] {samples[i]});

		double[] uCache = engine.getUnfilteredSamplesCache(source.getSampleCount(0));
		//System.out.println("uCache.length="+uCache.length);
		for (int i = 0; i < TEST_SAMPLE_COUNT; i++) {
			if (samples.length - i - 1 >= 0)
				assertEquals(uCache[uCache.length - i - 1], samples[samples.length - i - 1], 0.000001);
			else
				assertEquals(uCache[uCache.length - i], 0.0, 0.00000001);
		}

		//adding 2 samples
		double[] newSamples = new double[] {Math.random(), Math.random()};
		source.addSamples(new double[] {newSamples[0]});
		source.addSamples(new double[] {newSamples[1]});

		uCache = engine.getUnfilteredSamplesCache(2);

		assertEquals(uCache[0], samples[samples.length-3], 0.00001);
		assertEquals(uCache[1], samples[samples.length-2], 0.00001);
		assertEquals(uCache[2], samples[samples.length-1], 0.00001);
		assertEquals(uCache[3], newSamples[0], 0.00001);
		assertEquals(uCache[4], newSamples[1], 0.00001);

		//one more sample added
		double[] newNewSamples = new double[] {Math.random()};
		source.addSamples(newNewSamples);
		uCache = engine.getUnfilteredSamplesCache(1);

		assertEquals(uCache[0], samples[samples.length - 1], 0.00001);
		assertEquals(uCache[1], newSamples[0], 0.00001);
		assertEquals(uCache[2], newSamples[1], 0.00001);
		assertEquals(uCache[3], newNewSamples[0], 0.00001);
	}

	/**
	 * Test method for {@link TimeDomainSampleFilterEngine#getFilteredSamplesCache(int)}.
	 */
	@Test
	public void testGetFilteredSamplesCache() {

		coefficients = new FilterCoefficients(new double[] {1.0, 0.0, 0.0, 0.0},
											  new double[] {1.0, 0.0, 0.0, 0.0});

		engine = new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source, 0), coefficients);

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
	 * Test method for {@link TimeDomainSampleFilterEngine#calculateNewFilteredSamples(double[], double[], int)}.
	 */
	@Test
	public void testCalculateNewFilteredSamples() {

		coefficients = new FilterCoefficients(new double[] {1.0, 0.0, 0.0},
											  new double[] {1.0, 0.0, 0.0});

		engine = new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source, 0), coefficients);

		//adding whole at once
		double[] samples = new double[TEST_SAMPLE_COUNT];
		for (int i = 0; i < TEST_SAMPLE_COUNT; i++) {
			samples[i] = Math.random();
			source.addSamples(new double[] {samples[i]});
		}

		double[] uCache = engine.getUnfilteredSamplesCache(source.getSampleCount(0));
		double[] fCache = engine.getFilteredSamplesCache(source.getSampleCount(0));
		double[] newFiltered = engine.calculateNewFilteredSamples(uCache, fCache, TEST_SAMPLE_COUNT);

		for (int i = 0; i < newFiltered.length; i++)
			assertEquals(newFiltered[i], samples[i], 0.0001);

		//two more samples
		double[] newSamples1 = new double[] {Math.random(), Math.random()};
		source.addSamples(new double[] {newSamples1[0]});
		source.addSamples(new double[] {newSamples1[1]});
		uCache = engine.getUnfilteredSamplesCache(2);
		fCache = engine.getFilteredSamplesCache(2);
		newFiltered = engine.calculateNewFilteredSamples(uCache, fCache, 2);
		assertEquals(newFiltered[0], newSamples1[0], 0.00001);
		assertEquals(newFiltered[1], newSamples1[1], 0.000001);

	}

	/**
	 * Test method for {@link org.signalml.domain.signal.TimeDomainSampleFilterEngine#getSamples(double[], int, int, int)}.
	 * Uses a filter that multiplies every sample by one and compares filtered
	 * samples with unfiltered ones.
	 */
	@Test
	public void testGetSamplesAllPassFilter() {

		coefficients = new FilterCoefficients(new double[] {1.0, 0.0},
											  new double[] {1.0, 0.0});
		engine = new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source, 0), coefficients);

		double[] target1 = new double[TEST_SAMPLE_COUNT];
		double[] target2 = new double[TEST_SAMPLE_COUNT];

		int i;

		for (i = 0; i < TEST_SAMPLE_COUNT; i++)
			source.addSamples(new double[] {Math.random()});

		source.getSamples(0, target1, 0, TEST_SAMPLE_COUNT, 0);
		engine.updateCache(source.getSampleCount(0));
		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
		for (i = 0; i < TEST_SAMPLE_COUNT; i++)
			assertEquals(target1[i], target2[i], 0.00001);

	}

	/**
	 * Test method for {@link org.signalml.domain.signal.TimeDomainSampleFilterEngine#getSamples(double[], int, int, int)}.
	 * Uses a filter that applies gain to a signal.
	 */
	@Test
	public void testGetSamplesWithGain() {

		coefficients = new FilterCoefficients(new double[] {0.7},
											  new double[] {1.0});
		engine = new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source, 0), coefficients);

		double[] target1 = new double[TEST_SAMPLE_COUNT];
		double[] target2 = new double[TEST_SAMPLE_COUNT];

		int i;

		for (i = 0; i < TEST_SAMPLE_COUNT; i++)
			source.addSamples(new double[] {Math.random()});

		source.getSamples(0, target1, 0, TEST_SAMPLE_COUNT, 0);
		engine.updateCache(source.getSampleCount(0));
		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
		for (i = 0; i < TEST_SAMPLE_COUNT; i++)
			assertEquals(0.7 * target1[i], target2[i], 0.00001);

	}

	/**
	* Test method for {@link org.signalml.domain.signal.TimeDomainSampleFilterEngine#getSamples(double[], int, int, int)}.
	* Uses high pass filter to filter out a constant from the signal and pass only high
	* frequency component.
	*/
	@Test
	public void testGetSamplesHighPassFilter() {

		/*this filter was generated in python and is a highpass filter:
		 b,a=signal.iirdesign(wp=0.6,ws=0.2,gstop=30, gpass=3,ftype='butter')*/
		coefficients = new FilterCoefficients(new double[] {1.0,  0.04940057,  0.33397875,  0.00449333},
											  new double[] {0.16001061, -0.48003182,  0.48003182, -0.16001061}
											 );

		engine = new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source, 0), coefficients);

		double[] target1 = new double[TEST_SAMPLE_COUNT];
		double[] target2 = new double[TEST_SAMPLE_COUNT];

		int i;

		/* generating a test signal: 1, 2, 1, 2, 1, 2, 1, 2, ....
		 * which is a sum of two signals: a s1(t)=1.5 and an high frequency
		 * component with an amplitude=0.5 and frequency=2*sampling frequency:
		 * 1+0, 1+1, 1+0, 1+1, ...
		 *
		 * After high pass filtering the constant signal should be filtered out
		 * and we should only have the high frequency component:
		 * -0.5, 0.5, -0.5, 0.5, ...
		 */
		for (i = 0; i < TEST_SAMPLE_COUNT; i++)
			source.addSamples(new double[] {1.0 + (i % 2)});

		source.getSamples(0, target1, 0, TEST_SAMPLE_COUNT, 0);
		engine.updateCache(source.getSampleCount(0));
		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
		// first few samples are distorted by the rectangular window function
		for (i = 60; i < TEST_SAMPLE_COUNT; i++)
			assertEquals(target1[i] - 1.5, target2[i], 0.00001);

	}

	/**
	 * Test method for {@link org.signalml.domain.signal.TimeDomainSampleFilterEngine#getSamples(double[], int, int, int)}.
	 * Uses low pass filter to filter out a high frequency component from the
	 * signal and pass only constant component.
	 */
	@Test
	public void testGetSamplesLowPassFilter() {

		/*this filter was generated in python and is a lowpass filter:
		 b,a=signal.iirdesign(wp=0.2,ws=0.6,gstop=30, gpass=3,ftype='butter')*/
		coefficients = new FilterCoefficients(new double[] {1.0, -1.39105118, 0.85664657, -0.18260807},
											  new double[] {0.03537341, 0.10612024, 0.10612024, 0.03537341}
											 );
		engine = new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source, 0), coefficients);

		double[] target2 = new double[TEST_SAMPLE_COUNT];

		int i;

		/* generating a test signal: 1, 2, 1, 2, 1, 2, 1, 2, ....
		 * which is a sum of two signals: a s1(t)=1.5 and an high frequency
		 * component with an amplitude=0.5 and frequency=2*sampling frequency:
		 * 1+0, 1+1, 1+0, 1+1, ...
		 *
		 * After low pass filtering the high frequency component should be filtered out
		 * and we should only have the constant component:
		 * 1.5,1.5,1.5,1.5,...
		 */
		for (i = 0; i < TEST_SAMPLE_COUNT; i++)
			source.addSamples(new double[] {1.0 + (i % 2)});

		engine.updateCache(source.getSampleCount(0));
		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
		// first few samples are distorted by the rectangular window function
		for (i = 60; i < TEST_SAMPLE_COUNT; i++)
			assertEquals(1.5, target2[i], 0.0001);

	}

	/**
	 * Test method for {@link TimeDomainSampleFilterEngine#filter(double[], double[], double[])}.
	 */
	@Test
	public void testFilterUsingCache() {
		//test1
		double[] bCoefficients = new double[] {0.6, 0.22};
		double[] aCoefficients = new double[] {1, 0.3,  0.4};
		double[] input = new double[] {1, 2, 3, 4, 5, 6, 7, 8};
		double[] filtered = TimeDomainSampleFilterEngine.filterUsingCache(bCoefficients, aCoefficients, input);

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

		filtered = TimeDomainSampleFilterEngine.filterUsingCache(bCoefficients, aCoefficients, shortSignal);
		assertArrayEquals(expected, filtered, 1e-4);
	}

	/**
	 * Test method for {@link TimeDomainSampleFilterEngine#filter(double[], double[], double[], double[]) }
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
		double[] actualFilteringResult = TimeDomainSampleFilterEngine.filter(bCoefficients, aCoefficients, shortSignal, initialState);

		assertArrayEquals(expectedFilteringResult, actualFilteringResult, 1e-4);

	}

	/**
	 * Test method for {@link TimeDomainSampleFilterEngine#filterOffline(int, int) }
	 * (with filtfilt enabled).
	 */
	@Test
	public void testFiltfiltFilter() {

		double[] signal = new double[] {
			-0.84293027, -0.92374498, -0.88684131, -0.84391936, -0.87729384,
			-0.76973675, -0.77477867, -0.77924067, -0.68329653, -0.68361526,
			-0.58494503, -0.73579347, -0.72676736, -0.61426226, -0.7348077 ,
			-0.63514914, -0.61154161, -0.5558349 , -0.53932159, -0.51812691,
			-0.47357742, -0.45951363, -0.48207101, -0.40786034, -0.36886221,
			-0.32913236, -0.22319939, -0.30716421, -0.29695674, -0.30282762,
			-0.1731335 , -0.29801377, -0.04960099, -0.10052872, -0.05416476,
			-0.10317313, -0.03252693,  0.08046697,  0.00968778,  0.06494295,
			0.17597787,  0.18829966,  0.20641445,  0.21274386,  0.30567242,
			0.27605571,  0.33610845,  0.31463937,  0.4237753 ,  0.41912496,
			0.44247043,  0.3916358 ,  0.42717395,  0.48115014,  0.45252122,
			0.55205897,  0.55208584,  0.66123419,  0.64181315,  0.61917795,
			0.67897751,  0.64475711,  0.72066436,  0.63571739,  0.7383402 ,
			0.79417899,  0.83192426,  0.77532409,  0.91190394,  0.80264458,
			0.79057825,  0.77675767,  0.89988457,  0.87274363,  0.88678747,
			0.91419218,  0.96141022,  1.01311011,  0.92736388,  0.91391746,
			0.95243786,  1.03433971,  1.01932166,  0.99894765,  0.99653726,
			0.92062706,  0.998096  ,  1.02234326,  0.97227623,  1.00515578,
			0.9319138 ,  1.08820863,  1.01866496,  1.02534453,  1.00618392,
			0.93251842,  0.91639443,  0.94900511,  0.91605305,  0.8429076 ,
			0.89504872,  0.86893234,  0.88900493,  0.91816885,  0.84181857,
			0.86106255,  0.79748276,  0.84929295,  0.74823918,  0.74976467,
			0.77188246,  0.80976024,  0.67571691,  0.66339871,  0.57172218,
			0.65688144,  0.64465204,  0.64092486,  0.5693963 ,  0.47568057,
			0.51252357,  0.48183309,  0.41538183,  0.30359699,  0.33846401,
			0.28923601,  0.35550002,  0.38384619,  0.23790202,  0.29952727,
			0.28788653,  0.15992514,  0.15815129,  0.07504026,  0.04761541,
			0.00324624, -0.06015726,  0.02733409, -0.0738976 , -0.08329257,
			-0.0741636 , -0.1902963 , -0.22090859, -0.21896887, -0.30650592,
			-0.23625535, -0.36714351, -0.24848854, -0.36644736, -0.33915488,
			-0.32429195, -0.40328664, -0.47266171, -0.44345919, -0.63605137,
			-0.47284301, -0.55770832, -0.62602078, -0.67200793, -0.64251889,
			-0.69300218, -0.71162561, -0.6715638 , -0.70928254, -0.69294485,
			-0.86424337, -0.85437643, -0.76778759, -0.85093356, -0.95956528,
			-0.84909291, -0.85227192, -0.93660553, -0.94901385, -0.92753206,
			-0.97013603, -0.9075739 , -1.00017777, -0.98782556, -0.87511115,
			-0.8862636 , -0.93658539, -0.96675933, -0.97582854, -1.0147617 ,
			-0.95277905, -1.01121291, -0.926366  , -0.95798097, -0.95136035,
			-0.9205005 , -0.91926882, -0.95904834, -1.01419572, -1.0597947 ,
			-0.90336143, -0.96378908, -0.95913645, -0.88863453, -0.91757912
		};

		double[] bCoefficients = new double[]
		{0.00041655,  0.00124964,  0.00124964,  0.00041655};
		double[] aCoefficients = new double[]
		{1.        , -2.6861574 ,  2.41965511, -0.73016535};
		double[] expectedFilteringResult = new double[] {
			-0.85837307, -0.84660377, -0.83419701, -0.82114521, -0.80744042,
			-0.79307477, -0.77804047, -0.76232922, -0.74593152, -0.72883603,
			-0.71102913, -0.69249514, -0.67321725, -0.65317913, -0.63236668,
			-0.61076955, -0.58838222, -0.56520453, -0.5412417 , -0.51650405,
			-0.49100664, -0.46476889, -0.43781438, -0.41017055, -0.38186839,
			-0.35294206, -0.32342837, -0.29336654, -0.26279849, -0.2317695 ,
			-0.20032906, -0.16853138, -0.13643538, -0.10410417, -0.07160419,
			-0.03900445, -0.00637572,  0.0262102 ,  0.05868152,  0.09096774,
			0.12300094,  0.15471732,  0.18605861,  0.21697326,  0.24741728,
			0.27735473,  0.30675781,  0.33560669,  0.36388905,  0.39159934,
			0.41873781,  0.44530914,  0.47132077,  0.49678116,  0.52169845,
			0.54607951,  0.56992965,  0.59325269,  0.61605131,  0.63832723,
			0.66008115,  0.6813123 ,  0.70201793,  0.72219288,  0.74182928,
			0.76091659,  0.77944202,  0.79739098,  0.81474747,  0.8314939 ,
			0.84761065,  0.86307525,  0.87786175,  0.89194062,  0.90527919,
			0.91784251,  0.92959441,  0.94049867,  0.95052006,  0.95962507,
			0.96778239,  0.97496323,  0.98114197,  0.98629667,  0.99040927,
			0.9934655 ,  0.99545442,  0.99636825,  0.99620234,  0.99495534,
			0.99262943,  0.98923062,  0.98476912,  0.97925955,  0.9727207 ,
			0.9651747 ,  0.95664559,  0.94715774,  0.93673428,  0.92539598,
			0.91316029,  0.90004109,  0.8860488 ,  0.87119119,  0.85547429,
			0.83890345,  0.82148403,  0.80322218,  0.78412531,  0.76420261,
			0.74346553,  0.72192827,  0.6996082 ,  0.67652593,  0.65270508,
			0.62817188,  0.60295511,  0.57708622,  0.5505995 ,  0.52353167,
			0.49592115,  0.467807  ,  0.43922782,  0.41022082,  0.38082097,
			0.35106071,  0.32097044,  0.29057976,  0.25991911,  0.22902117,
			0.19792178,  0.16666026,  0.13527892,  0.10382199,  0.07233406,
			0.04085854,  0.00943616, -0.02189596, -0.05310483, -0.08416176,
			-0.11504236, -0.14572657, -0.17619875, -0.20644772, -0.23646657,
			-0.2662519 , -0.29580277, -0.325119  , -0.35419935, -0.38303953,
			-0.41163047, -0.43995691, -0.46799669, -0.4957207 , -0.52309342,
			-0.55007374, -0.57661583, -0.60266991, -0.62818309, -0.65310035,
			-0.67736532, -0.70092085, -0.72370949, -0.74567371, -0.76675649,
			-0.78690215, -0.80605778, -0.82417478, -0.84121036, -0.85712879,
			-0.87190241, -0.88551247, -0.89794963, -0.90921416, -0.91931615,
			-0.92827547, -0.93612129, -0.94289139, -0.94863106, -0.95339181,
			-0.95722978, -0.96020422, -0.96237636, -0.963809  , -0.96456637,
			-0.96471421, -0.96431959, -0.9634504 , -0.96217465, -0.96055965,
			-0.95867114, -0.9565728 , -0.95432581, -0.95198896, -0.94961884,
			-0.94726984, -0.94499362, -0.94283788, -0.94084486, -0.9390497
		};

		source = new RoundBufferMultichannelSampleSource(1, signal.length);
		for (int i = 0; i < signal.length; i++)
			source.addSamples(new double[] {signal[i]});

		FilterCoefficients coefficients = new FilterCoefficients(bCoefficients, aCoefficients);
		TimeDomainSampleFilterEngine engine = new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source, 0), coefficients);
		engine.updateCache(0);
		engine.setFiltfiltEnabled(true);

		double[] actualFilteringResult = new double[expectedFilteringResult.length];
		engine.filterOffline(0, actualFilteringResult.length);
		actualFilteringResult = engine.filtered.getSamples();

		assertArrayEquals(expectedFilteringResult, actualFilteringResult, 1e-1);

	}

}
