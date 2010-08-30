/* TimeDomainSampleFilterEngineTest.java created 2010-08-28
 *
 */
package org.signalml.domain.signal;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import java.lang.Math.*;

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
	public static final int TEST_SAMPLE_COUNT = 120;

        /**
         * the {@link TimeDomainSampleFilter definition} of the filter used for testing
         * {@link TimeDomainSampleFilter TimeDomainSampleFilter} class.
         */
	private TimeDomainSampleFilter definition;
        /**
         * the source of samples which will be filtered by the engine
         */
	private RoundBufferSampleSource source;
        /**
         * the engine used to filter the samples taken from the source
         */
	private TimeDomainSampleFilterEngine engine;

	/**
	 * Sets everything up for the tests.
	 */
	@Before
	public void setUp() {
            	source = new RoundBufferSampleSource( 1, TEST_SAMPLE_COUNT);
	}

	/**
	 * Cleans up after the test.
	 */
	@After
	public void tearDown(){
                source=null;
		definition=null;
	}

        /**
	 * Test method for {@link org.signalml.domain.signal.TimeDomainSampleFilterEngine#getSamples(double[], int, int, int)}.
         * Uses a filter that multiplies every sample by one and compares filtered
         * samples with unfiltered ones.
	 */
	@Test
	public void testGetSamplesAllPassFilter() {
		definition=new TimeDomainSampleFilter("sampleFilter.td.lowPass", "xxx",
                new double[] {1.0,0.0},
                new double[] {1.0,0.0} );
		engine= new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source,0),definition);

		double[] target1 = new double[TEST_SAMPLE_COUNT];
		double[] target2 = new double[TEST_SAMPLE_COUNT];

		int i;

		for(i=0;i<TEST_SAMPLE_COUNT;i++)
			source.addSamples(new double[]{Math.random()});

		source.getSamples(0,target1, 0, TEST_SAMPLE_COUNT, 0);
		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
		for(i=0;i<TEST_SAMPLE_COUNT;i++)
			assertEquals(target1[i],target2[i],0.00001);
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
		definition=new TimeDomainSampleFilter("sampleFilter.td.highPass", "this filter does nothing",
                    new double[] {1.0,  0.04940057,  0.33397875,  0.00449333},
                    new double[] {0.16001061, -0.48003182,  0.48003182, -0.16001061}
                );
		engine= new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source,0),definition);

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
		for(i=0;i<TEST_SAMPLE_COUNT;i++)
			source.addSamples(new double[]{1.0+(i%2)});
		
		source.getSamples(0,target1, 0, TEST_SAMPLE_COUNT, 0);
		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
                // first few samples are distorted by the rectangular window function
		for(i=60;i<TEST_SAMPLE_COUNT;i++)
			assertEquals(target1[i]-1.5,target2[i],0.00001);
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
		definition=new TimeDomainSampleFilter("sampleFilter.td.highPass", "this filter does nothing",
                    new double[] { 1.        , -1.39105118,  0.85664657, -0.18260807},
                    new double[] {0.03537341,  0.10612024,  0.10612024,  0.03537341}
                );
		engine= new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source,0),definition);

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
		for(i=0;i<TEST_SAMPLE_COUNT;i++)
			source.addSamples(new double[]{1.0+(i%2)});

		engine.getSamples(target2, 0, TEST_SAMPLE_COUNT, 0);
                // first few samples are distorted by the rectangular window function
		for(i=60;i<TEST_SAMPLE_COUNT;i++)
			assertEquals(1.5,target2[i],0.0001);
	}


}
