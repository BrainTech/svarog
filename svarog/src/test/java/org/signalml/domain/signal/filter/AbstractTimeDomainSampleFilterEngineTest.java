package org.signalml.domain.signal.filter;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.samplesource.RoundBufferMultichannelSampleSource;
import org.signalml.math.iirdesigner.FilterCoefficients;

/**
 * This is an abstract class for testing subclasses of
 * {@link AbstractTimeDomainSampleFilterEngine}.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractTimeDomainSampleFilterEngineTest {

	/**
	 * the number of samples used for the tests
	 */
	public static final int TEST_SAMPLE_COUNT = 20;

	/**
	 * the {@link FilterCoefficients coefficients} of the filter used for testing
	 * {@link TimeDomainSampleFilter TimeDomainSampleFilter} class.
	 */
	protected FilterCoefficients coefficients;

	/**
	 * the source of samples which will be filtered by the engine
	 */
	protected RoundBufferMultichannelSampleSource source;

	/**
	 * the engine used to filter the samples taken from the source
	 */
	private AbstractTimeDomainSampleFilterEngine engine;

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

	protected abstract AbstractTimeDomainSampleFilterEngine getEngine(MultichannelSampleSource source, FilterCoefficients coefficients);

	/**
	 * Test method for {@link org.signalml.domain.signal.filter.AbstractTimeDomainSampleFilterEngine#getSamples(double[], int, int, int)}.
	 * Uses a filter that multiplies every sample by one and compares filtered
	 * samples with unfiltered ones.
	 */
	@Test
	public void testGetSamplesAllPassFilter() {

		coefficients = new FilterCoefficients(new double[] {1.0, 0.0},
											  new double[] {1.0, 0.0});

		engine = getEngine(source, coefficients);

		double[] target1 = new double[TEST_SAMPLE_COUNT];
		double[] target2 = new double[TEST_SAMPLE_COUNT];

		int i;

		for (i = 0; i < TEST_SAMPLE_COUNT; i++)
			source.addSamples(new float[] {(float) Math.random()});

		source.getSamples(0, target1, 0, TEST_SAMPLE_COUNT, 0);

		updateCacheIfNecessary();
		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
		for (i = 0; i < TEST_SAMPLE_COUNT; i++)
			assertEquals(target1[i], target2[i], 0.00001);

	}

	protected void updateCacheIfNecessary() {
		if (engine instanceof OnlineTimeDomainSampleFilterEngine)
			((OnlineTimeDomainSampleFilterEngine)engine).updateCache(source.getSampleCount(0));
	}

	/**
	 * Test method for {@link org.signalml.domain.signal.filter.AbstractTimeDomainSampleFilterEngine#getSamples(double[], int, int, int)}.
	 * Uses a filter that applies gain to a signal.
	 */
	@Test
	public void testGetSamplesWithGain() {

		coefficients = new FilterCoefficients(new double[] {0.7},
											  new double[] {1.0});
		engine = getEngine(source, coefficients);

		double[] target1 = new double[TEST_SAMPLE_COUNT];
		double[] target2 = new double[TEST_SAMPLE_COUNT];

		int i;

		for (i = 0; i < TEST_SAMPLE_COUNT; i++)
			source.addSamples(new float[] {(float) Math.random()});

		source.getSamples(0, target1, 0, TEST_SAMPLE_COUNT, 0);
		updateCacheIfNecessary();
		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
		for (i = 0; i < TEST_SAMPLE_COUNT; i++)
			assertEquals(0.7 * target1[i], target2[i], 0.00001);

	}

	/**
	* Test method for {@link org.signalml.domain.signal.filter.AbstractTimeDomainSampleFilterEngine#getSamples(double[], int, int, int)}.
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

		engine = getEngine(source, coefficients);

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
			source.addSamples(new float[] {(float) (1.0 + (i % 2))});

		source.getSamples(0, target1, 0, TEST_SAMPLE_COUNT, 0);
		updateCacheIfNecessary();
		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
		// first few samples are distorted by the rectangular window function
		for (i = 60; i < TEST_SAMPLE_COUNT; i++)
			assertEquals(target1[i] - 1.5, target2[i], 0.00001);

	}

	/**
	 * Test method for {@link org.signalml.domain.signal.filter.AbstractTimeDomainSampleFilterEngine#getSamples(double[], int, int, int)}.
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
		engine = getEngine(source, coefficients);

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
			source.addSamples(new float[] {(float) (1.0 + (i % 2))});

		updateCacheIfNecessary();
		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
		// first few samples are distorted by the rectangular window function
		for (i = 60; i < TEST_SAMPLE_COUNT; i++)
			assertEquals(1.5, target2[i], 0.0001);

	}

}
