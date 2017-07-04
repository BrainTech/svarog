package org.signalml.domain.signal.samplesource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.signalml.BaseTestCase;
import org.signalml.domain.signal.samplesource.RoundBufferMultichannelSampleSource;

/**
 * @author Mariusz Podsiadło
 *
 */
public class RoundBufferMultichannelSampleSourceTest extends BaseTestCase {

	public static final int TEST_CHANNEL_COUNT = 4;
	public static final int TEST_SAMPLE_COUNT = 10;
	protected RoundBufferMultichannelSampleSource theSource;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		theSource = new RoundBufferMultichannelSampleSource(TEST_CHANNEL_COUNT, TEST_SAMPLE_COUNT);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		theSource = null;
	}

	/**
	 * Test method for {@link org.signalml.domain.signal.samplesource.RoundBufferMultichannelSampleSource#incrNextInsertPos()}.
	 */
	@Test
	public void testIncrNextInsertPos() {
		assertEquals(0, theSource.getNextInsertPos());
		theSource.incrNextInsertPos();
		assertEquals(1, theSource.getNextInsertPos());
		theSource.setNextInsertPos(TEST_SAMPLE_COUNT - 2);
		theSource.incrNextInsertPos();
		assertEquals(TEST_SAMPLE_COUNT - 1, theSource.getNextInsertPos());
		theSource.incrNextInsertPos();
		assertEquals(0, theSource.getNextInsertPos());
	}

	/**
	 * Test method for {@link org.signalml.domain.signal.samplesource.RoundBufferMultichannelSampleSource#addSamples(double[])}.
	 */
	@Test
	public void testAddSamples() {
		assertEquals(0, theSource.getNextInsertPos());
		float[] samples = null;
		samples = new float[] {1.0F, 2.0F, 3.0F, 4.0F};
		theSource.addSamples(samples);
		assertEquals(1, theSource.getNextInsertPos());
		assertFalse(theSource.isFull());
		samples = new float[] {11.0F, 12.0F, 13.0F, 14.0F};
		theSource.addSamples(samples);
		assertEquals(2, theSource.getNextInsertPos());
		assertFalse(theSource.isFull());
		samples = new float[] {21.0F, 22.0F, 23.0F, 24.0F};
		theSource.addSamples(samples);
		assertEquals(3, theSource.getNextInsertPos());
		assertFalse(theSource.isFull());
		samples = new float[] {31.0F, 32.0F, 33.0F, 34.0F};
		theSource.addSamples(samples);
		assertEquals(4, theSource.getNextInsertPos());
		assertFalse(theSource.isFull());
		samples = new float[] {41.0F, 42.0F, 43.0F, 44.0F};
		theSource.addSamples(samples);
		assertEquals(5, theSource.getNextInsertPos());
		assertFalse(theSource.isFull());
		samples = new float[] {51.0F, 52.0F, 53.0F, 54.0F};
		theSource.addSamples(samples);
		assertEquals(6, theSource.getNextInsertPos());
		assertFalse(theSource.isFull());
		samples = new float[] {61.0F, 62.0F, 63.0F, 64.0F};
		theSource.addSamples(samples);
		assertEquals(7, theSource.getNextInsertPos());
		assertFalse(theSource.isFull());
		samples = new float[] {71.0F, 72.0F, 73.0F, 74.0F};
		theSource.addSamples(samples);
		assertEquals(8, theSource.getNextInsertPos());
		assertFalse(theSource.isFull());
		samples = new float[] {81.0F, 82.0F, 83.0F, 84.0F};
		theSource.addSamples(samples);
		assertEquals(9, theSource.getNextInsertPos());
		assertFalse(theSource.isFull());
		samples = new float[] {91.0F, 92.0F, 93.0F, 94.0F};
		theSource.addSamples(samples);
		assertEquals(0, theSource.getNextInsertPos());
		assertTrue(theSource.isFull());
		samples = new float[] {101.0F, 102.0F, 103.0F, 104.0F};
		theSource.addSamples(samples);
		assertEquals(1, theSource.getNextInsertPos());
		assertTrue(theSource.isFull());
		samples = new float[] {111.0F, 112.0F, 113.0F, 114.0F};
		theSource.addSamples(samples);
		assertEquals(2, theSource.getNextInsertPos());
		assertTrue(theSource.isFull());
		double[][] theSamples = theSource.getSamples();
		assertEquals(101.0, theSamples[0][0], 0.1);
		assertEquals(102.0, theSamples[1][0], 0.1);
		assertEquals(103.0, theSamples[2][0], 0.1);
		assertEquals(104.0, theSamples[3][0], 0.1);
		assertEquals(111.0, theSamples[0][1], 0.1);
		assertEquals(112.0, theSamples[1][1], 0.1);
		assertEquals(113.0, theSamples[2][1], 0.1);
		assertEquals(114.0, theSamples[3][1], 0.1);
		assertEquals(21.0, theSamples[0][2], 0.1);
		assertEquals(22.0, theSamples[1][2], 0.1);
		assertEquals(23.0, theSamples[2][2], 0.1);
		assertEquals(24.0, theSamples[3][2], 0.1);
		assertEquals(91.0, theSamples[0][9], 0.1);
		assertEquals(92.0, theSamples[1][9], 0.1);
		assertEquals(93.0, theSamples[2][9], 0.1);
		assertEquals(94.0, theSamples[3][9], 0.1);
	}

	/**
	 * Test method for {@link org.signalml.domain.signal.samplesource.RoundBufferMultichannelSampleSource#getSamples(int, double[], int, int, int)}.
	 */
	@Test
	public void testGetSamples() {

		double[] target = new double[TEST_SAMPLE_COUNT];
		theSource.getSamples(0 , target, 0, TEST_SAMPLE_COUNT, 0);
		for (int i = 0; i < TEST_SAMPLE_COUNT; i++) {
			assertEquals(0.0, target[i], 0.1);
		}
		theSource.addSamples(new float[] {1.0F, 2.0F, 3.0F, 4.0F});
		theSource.addSamples(new float[] {11.0F, 12.0F, 13.0F, 14.0F});
		theSource.addSamples(new float[] {21.0F, 22.0F, 23.0F, 24.0F});
		theSource.getSamples(0 , target, 0, TEST_SAMPLE_COUNT, 0);
		int i = 0;
		for (; i < TEST_SAMPLE_COUNT - 3; i++) {
			assertEquals(0.0, target[i], 0.1);
		}
		assertEquals(1.0, target[i++], 0.1);
		assertEquals(11.0, target[i++], 0.1);
		assertEquals(21.0, target[i++], 0.1);
		theSource.addSamples(new float[] {31.0F, 32.0F, 33.0F, 34.0F});
		theSource.addSamples(new float[] {41.0F, 42.0F, 43.0F, 44.0F});
		theSource.addSamples(new float[] {51.0F, 52.0F, 53.0F, 54.0F});
		theSource.addSamples(new float[] {61.0F, 62.0F, 63.0F, 64.0F});
		theSource.addSamples(new float[] {71.0F, 72.0F, 73.0F, 74.0F});
		theSource.addSamples(new float[] {81.0F, 82.0F, 83.0F, 84.0F});
		theSource.addSamples(new float[] {91.0F, 92.0F, 93.0F, 94.0F});
		theSource.addSamples(new float[] {101.0F, 102.0F, 103.0F, 104.0F});
		theSource.addSamples(new float[] {111.0F, 112.0F, 113.0F, 114.0F});
		theSource.addSamples(new float[] {121.0F, 122.0F, 123.0F, 124.0F});
		theSource.getSamples(0 , target, 0, TEST_SAMPLE_COUNT, 0);

	}

}