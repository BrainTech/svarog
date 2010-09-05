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
	public static final int TEST_SAMPLE_COUNT = 10;

        /**
         * the {@link TimeDomainSampleFilter definition} of the filter used for testing
         * {@link TimeDomainSampleFilter TimeDomainSampleFilter} class.
         */
	private TimeDomainSampleFilter definition;
        /**
         * the source of samples which will be filtered by the engine
         */
	private RoundBufferMultichannelSampleSource source;
        /**
         * the engine used to filter the samples taken from the source
         */
	private TimeDomainSampleFilterEngine engine;

	/**
	 * Sets everything up for the tests.
	 */
	@Before
	public void setUp() {
            	source = new RoundBufferMultichannelSampleSource( 1, TEST_SAMPLE_COUNT);
	}

	/**
	 * Cleans up after the test.
	 */
	@After
	public void tearDown(){
                source=null;
		definition=null;
	}

        @Test
        public void testGetUnfilteredSamplesCache(){
            definition=new TimeDomainSampleFilter("sampleFilter.td.lowPass", "xxx",
                new double[] {1.0,0.0,0.0,0.0},
                new double[] {1.0,0.0,0.0,0.0} );
            engine= new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source,0),definition);

            //testing empty then full
            double[] samples=new double[TEST_SAMPLE_COUNT];
            for(int i=0;i<TEST_SAMPLE_COUNT;i++){
                samples[i]=Math.random();
            }
            
            for(int i=0;i<TEST_SAMPLE_COUNT;i++)
                source.addSamples(new double[]{samples[i]});
            
            double[] uCache=engine.getUnfilteredSamplesCache(source.getSampleCount(0));
            //System.out.println("uCache.length="+uCache.length);
            for(int i=0;i<TEST_SAMPLE_COUNT;i++){
                if(samples.length-i-1>=0)
                    assertEquals(uCache[uCache.length-i-1],samples[samples.length-i-1],0.000001);
                else
                    assertEquals(uCache[uCache.length-i],0.0,0.00000001);
            }

           //adding 2 samples
           double[] newSamples=new double[]{Math.random(),Math.random()};
           source.addSamples(new double[]{newSamples[0]});
           source.addSamples(new double[]{newSamples[1]});

           uCache=engine.getUnfilteredSamplesCache(2);
           
           assertEquals(uCache[0],samples[samples.length-3],0.00001);
           assertEquals(uCache[1],samples[samples.length-2],0.00001);
           assertEquals(uCache[2],samples[samples.length-1],0.00001);
           assertEquals(uCache[3],newSamples[0],0.00001);
           assertEquals(uCache[4],newSamples[1],0.00001);

           //one more sample added
            double[] newNewSamples=new double[]{Math.random()};
            source.addSamples(newNewSamples);
            uCache=engine.getUnfilteredSamplesCache(1);

            assertEquals(uCache[0],samples[samples.length-1],0.00001);
            assertEquals(uCache[1],newSamples[0],0.00001);
            assertEquals(uCache[2],newSamples[1],0.00001);
            assertEquals(uCache[3],newNewSamples[0],0.00001);
        }

        @Test
        public void testGetFilteredSamplesCache(){
            definition=new TimeDomainSampleFilter("sampleFilter.td.lowPass", "xxx",
                new double[] {1.0,0.0,0.0,0.0},
                new double[] {1.0,0.0,0.0,0.0} );
            engine= new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source,0),definition);

            double[] fCache=engine.getFilteredSamplesCache(source.getSampleCount(0));
            assertEquals(fCache.length,source.getSampleCount(0)+3,0.00001);
            for(int i=0; i<fCache.length;i++)
                assertEquals(fCache[i],0.0,0.000001);

            //adding one sample
            double[] samples=new double[]{Math.random()};
            engine.filtered.addSamples(samples);
            double x[]=new double[engine.filtered.getSampleCount()];
            engine.filtered.getSamples(x, 0, engine.filtered.getSampleCount(),0);
            System.out.println("X:::");
            for(int i=0;i<x.length;i++)
                System.out.println(x[i]);

            fCache=engine.getFilteredSamplesCache(2);

            System.out.println("new sampple="+samples[0]);
            for(int i=0;i<fCache.length;i++)
                System.out.println(i+". "+fCache[i]);

            assertEquals(fCache.length,3+2,0.000001);
            assertEquals(fCache[0],0.0,0.000001);
            assertEquals(fCache[1],0.0,0.000001);
            assertEquals(fCache[2],samples[0],0.000001);
            assertEquals(fCache[3],0.0,0.000001);
            assertEquals(fCache[4],0.0,0.000001);

            //added 2 filtered
            double[] newFilteredSamples=new double[2];
            newFilteredSamples[0]=Math.random();
            newFilteredSamples[1]=Math.random();
            engine.filtered.addSamples(new double[]{newFilteredSamples[0]});
            engine.filtered.addSamples(new double[]{newFilteredSamples[1]});

            fCache=engine.getFilteredSamplesCache(1);
            assertEquals(fCache.length,3+1,0.000001);
            assertEquals(fCache[0],samples[0],0.000001);
            assertEquals(fCache[1],newFilteredSamples[0],0.000001);
            assertEquals(fCache[2],newFilteredSamples[1],0.000001);
            assertEquals(fCache[3],0.0,0.000001);

            //added 1 filtered
            double[] new3=new double[1];
            new3[0]=Math.random();
            engine.filtered.addSamples(new3);

            fCache=engine.getFilteredSamplesCache(3);
            assertEquals(fCache.length,3+3,0.000001);
            assertEquals(fCache[0],newFilteredSamples[0],0.000001);
            assertEquals(fCache[1],newFilteredSamples[1],0.000001);
            assertEquals(fCache[2],new3[0],0.000001);
            assertEquals(fCache[3],0.0,0.000001);
            assertEquals(fCache[4],0.0,0.000001);
            assertEquals(fCache[5],0.0,0.000001);

        }

        @Test
        public void testCalculateNewFilteredSamples(){
            definition=new TimeDomainSampleFilter("sampleFilter.td.lowPass", "xxx",
                new double[] {1.0,0.0,0.0},
                new double[] {1.0,0.0,0.0} );
            engine= new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source,0),definition);

            //adding whole at once
            double[] samples=new double[TEST_SAMPLE_COUNT];
            for(int i=0;i<TEST_SAMPLE_COUNT;i++){
                samples[i]=Math.random();
                source.addSamples(new double[]{samples[i]});
            }

            double[] uCache=engine.getUnfilteredSamplesCache(source.getSampleCount(0));
            double[] fCache=engine.getFilteredSamplesCache(source.getSampleCount(0));
            double[] newFiltered=engine.calculateNewFilteredSamples(uCache,fCache, TEST_SAMPLE_COUNT);

            for(int i=0;i<newFiltered.length;i++)
                assertEquals(newFiltered[i],samples[i],0.0001);

            //two more samples
            double[] newSamples1=new double[]{Math.random(),Math.random()};
            source.addSamples(new double[]{newSamples1[0]});
            source.addSamples(new double[]{newSamples1[1]});
            uCache=engine.getUnfilteredSamplesCache(2);
            fCache=engine.getFilteredSamplesCache(2);
            newFiltered=engine.calculateNewFilteredSamples(uCache,fCache,2);
            assertEquals(newFiltered[0],newSamples1[0],0.00001);
            assertEquals(newFiltered[1],newSamples1[1],0.000001);

            //checking engine.filtered buffer
            /*double[] buf=new double[engine.filtered.getSampleCount()];
            engine.filtered.getSamples(buf, 0,engine.filtered.getSampleCount(),0);
            assertEquals(buf[0],newSamples1[0],0.0000001);
            assertEquals(buf[0],newSamples1[1],0.0000001);
            for(int i=0;i<buf.length-2;i++)
                assertEquals(buf[i],samples[i+2],0.000001);
            assertEquals(buf[buf.length-2],newSamples1[0],0.0000001);
            assertEquals(buf[buf.length-1],newSamples1[1],0.0000001);
*/

        }

        @Test public void testUpdateCache(){
            definition=new TimeDomainSampleFilter("sampleFilter.td.lowPass", "xxx",
                new double[] {1.0,0.0,0.0},
                new double[] {1.0,0.0,0.0} );
            engine= new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source,0),definition);

            double[] samples=new double[TEST_SAMPLE_COUNT];
            for(int i=0;i<TEST_SAMPLE_COUNT;i++)
                samples[i]=Math.random();

            source.addSamples(samples);
            engine.updateCache(TEST_SAMPLE_COUNT);
            double[] filtered=engine.filtered.getSamples();
            for(int i=0;i<TEST_SAMPLE_COUNT;i++){
                System.out.println(i+". "+samples[i]+" : "+filtered[i]);
                //assertEquals(samples[i],filtered[i],0.000001);
            }

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
