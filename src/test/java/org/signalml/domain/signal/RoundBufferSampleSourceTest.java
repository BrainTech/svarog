/* RoundBufferSampleSourceTest.java created 2010-09-07
 *
 */

package org.signalml.domain.signal;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This class performs unit tests on {@link RoundBufferSampleSource} class.
 *
 * @author Piotr Szachewicz
 */
public class RoundBufferSampleSourceTest {

	/**
	 * number of samples used for the tests.
	 */
	public static final int TEST_SAMPLE_COUNT = 4;

	/**
	 * the sample source used for the test
	 */
	private RoundBufferSampleSource source;

	/**
	 * sets everything up for the test
	 */
	@Before
	public void setUp() {
		source = new RoundBufferSampleSource(TEST_SAMPLE_COUNT);
	}

	/**
	 * cleans everything after the test
	 */
	@After
	public void tearDown() {
		source = null;
	}

	/**
	 * Test method for {@link RoundBufferSampleSource#addSamples(double[])}.
	 */
	@Test
	public void testAddSamples() {

		source.addSamples(new double[] {1.0});
		source.addSamples(new double[] {1.1});
		source.addSamples(new double[] {1.2});
		source.addSamples(new double[] {1.3});

		source.addSamples(new double[] {1.4});
		source.addSamples(new double[] {1.5});

		double[] theSamples=source.getSamples();

		assertEquals(theSamples[0],1.4, 0.00001);
		assertEquals(theSamples[1],1.5, 0.00001);
		assertEquals(theSamples[2],1.2, 0.00001);
		assertEquals(theSamples[3],1.3, 0.00001);

	}

	/**
	 * Test method for {@link RoundBufferSampleSource#getSamples(double[], int, int, int) }.
	 */
	@Test
	public void testGetSamples() {

		double target[];

		//what happens when getting samples from an empty buffer?
		target = new double[4];
		source.getSamples(target, 0, 4, 0);
		for (int i = 0; i < 4; i++)
			assertEquals(target[i], 0.0, 0.0000001);

		source.addSamples(new double[] {1.0});
		source.addSamples(new double[] {1.1});
		source.addSamples(new double[] {1.2});
		source.addSamples(new double[] {1.3});

		target = new double[3];
		source.getSamples(target, 1, 3, 0);
		assertEquals(target[0],1.1,0.0001);
		assertEquals(target[1],1.2,0.0001);
		assertEquals(target[2],1.3,0.0001);

		target = new double[5];
		source.getSamples(target, 0, 4, 1);
		assertEquals(target[1], 1.0, 0.0001);
		assertEquals(target[2], 1.1, 0.0001);
		assertEquals(target[3], 1.2, 0.0001);
		assertEquals(target[4], 1.3, 0.0001);

	}

	/**
	 * Test method for {@link RoundBufferSampleSource#getSamples(double[], int, int, int) }.
	 */
	@Test
	public void testGetSamplesGettingTheLastAddedSample() {

		double target[] = new double[1];

		double sample;
		for (int i = 0; i < 10; i++) {

			sample = Math.random();
			source.addSamples(new double[] {sample});
			source.getSamples(target, source.getSampleCount() - 1, 1, 0);
			assertEquals(sample,target[0], 0.000001);

		}

		sample = Math.random();
		source.addSamples(new double[] {sample});
		source.addSamples(new double[] {sample + 1});
		target = new double[2];
		source.getSamples(target, source.getSampleCount() - 2, 2, 0);
		assertEquals(target[0], sample, 0.000001);
		assertEquals(target[1], sample + 1, 0.00001);

	}

}