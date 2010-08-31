package org.signalml.domain.signal;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Mariusz Podsiadło
 *
 */
public class RoundBufferMultichannelSampleSourceTest {

	public static final int TEST_CHANNEL_COUNT = 4;
	public static final int TEST_SAMPLE_COUNT = 10;
	protected RoundBufferMultichannelSampleSource theSource;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		theSource = new RoundBufferMultichannelSampleSource( TEST_CHANNEL_COUNT, TEST_SAMPLE_COUNT);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		theSource = null;
	}

	/**
	 * Test method for {@link org.signalml.domain.signal.RoundBufferMultichannelSampleSource#incrNextInsertPos()}.
	 */
	@Test
	public void testIncrNextInsertPos() {
		assertEquals( 0, theSource.getNextInsertPos());
		theSource.incrNextInsertPos();
		assertEquals( 1, theSource.getNextInsertPos());
		theSource.setNextInsertPos(TEST_SAMPLE_COUNT - 2);
		theSource.incrNextInsertPos();
		assertEquals( TEST_SAMPLE_COUNT - 1, theSource.getNextInsertPos());
		theSource.incrNextInsertPos();
		assertEquals( 0, theSource.getNextInsertPos());
	}

	/**
	 * Test method for {@link org.signalml.domain.signal.RoundBufferMultichannelSampleSource#addSamples(double[])}.
	 */
	@Test
	public void testAddSamples() {
		assertEquals( 0, theSource.getNextInsertPos());
		double[] samples = null;
		samples = new double[] {1.0, 2.0, 3.0, 4.0};
		theSource.addSamples( samples);
		assertEquals( 1, theSource.getNextInsertPos());
		assertFalse( theSource.isFull());
		samples = new double[] {11.0, 12.0, 13.0, 14.0};
		theSource.addSamples( samples);
		assertEquals( 2, theSource.getNextInsertPos());
		assertFalse( theSource.isFull());
		samples = new double[] {21.0, 22.0, 23.0, 24.0};
		theSource.addSamples( samples);
		assertEquals( 3, theSource.getNextInsertPos());
		assertFalse( theSource.isFull());
		samples = new double[] {31.0, 32.0, 33.0, 34.0};
		theSource.addSamples( samples);
		assertEquals( 4, theSource.getNextInsertPos());
		assertFalse( theSource.isFull());
		samples = new double[] {41.0, 42.0, 43.0, 44.0};
		theSource.addSamples( samples);
		assertEquals( 5, theSource.getNextInsertPos());
		assertFalse( theSource.isFull());
		samples = new double[] {51.0, 52.0, 53.0, 54.0};
		theSource.addSamples( samples);
		assertEquals( 6, theSource.getNextInsertPos());
		assertFalse( theSource.isFull());
		samples = new double[] {61.0, 62.0, 63.0, 64.0};
		theSource.addSamples( samples);
		assertEquals( 7, theSource.getNextInsertPos());
		assertFalse( theSource.isFull());
		samples = new double[] {71.0, 72.0, 73.0, 74.0};
		theSource.addSamples( samples);
		assertEquals( 8, theSource.getNextInsertPos());
		assertFalse( theSource.isFull());
		samples = new double[] {81.0, 82.0, 83.0, 84.0};
		theSource.addSamples( samples);
		assertEquals( 9, theSource.getNextInsertPos());
		assertFalse( theSource.isFull());
		samples = new double[] {91.0, 92.0, 93.0, 94.0};
		theSource.addSamples( samples);
		assertEquals( 0, theSource.getNextInsertPos());
		assertTrue( theSource.isFull());
		samples = new double[] {101.0, 102.0, 103.0, 104.0};
		theSource.addSamples( samples);
		assertEquals( 1, theSource.getNextInsertPos());
		assertTrue( theSource.isFull());
		samples = new double[] {111.0, 112.0, 113.0, 114.0};
		theSource.addSamples( samples);
		assertEquals( 2, theSource.getNextInsertPos());
		assertTrue( theSource.isFull());
		double[][] theSamples = theSource.getSamples();
		assertEquals( 101.0, theSamples[0][0], 0.1);
		assertEquals( 102.0, theSamples[1][0], 0.1);
		assertEquals( 103.0, theSamples[2][0], 0.1);
		assertEquals( 104.0, theSamples[3][0], 0.1);
		assertEquals( 111.0, theSamples[0][1], 0.1);
		assertEquals( 112.0, theSamples[1][1], 0.1);
		assertEquals( 113.0, theSamples[2][1], 0.1);
		assertEquals( 114.0, theSamples[3][1], 0.1);
		assertEquals( 21.0, theSamples[0][2], 0.1);
		assertEquals( 22.0, theSamples[1][2], 0.1);
		assertEquals( 23.0, theSamples[2][2], 0.1);
		assertEquals( 24.0, theSamples[3][2], 0.1);
		assertEquals( 91.0, theSamples[0][9], 0.1);
		assertEquals( 92.0, theSamples[1][9], 0.1);
		assertEquals( 93.0, theSamples[2][9], 0.1);
		assertEquals( 94.0, theSamples[3][9], 0.1);
	}

	/**
	 * Test method for {@link org.signalml.domain.signal.RoundBufferMultichannelSampleSource#getSamples(int, double[], int, int, int)}.
	 */
	@Test
	public void testGetSamples() {
		
		double[] target = new double[TEST_SAMPLE_COUNT];
		theSource.getSamples( 0 , target, 0, TEST_SAMPLE_COUNT, 0);
		for (int i=0; i<TEST_SAMPLE_COUNT; i++) {
			assertEquals( 0.0, target[i], 0.1);
		}
		theSource.addSamples( new double[] {1.0, 2.0, 3.0, 4.0});
		theSource.addSamples( new double[] {11.0, 12.0, 13.0, 14.0});
		theSource.addSamples( new double[] {21.0, 22.0, 23.0, 24.0});
		theSource.getSamples( 0 , target, 0, TEST_SAMPLE_COUNT, 0);
		int i=0;
		for (; i<TEST_SAMPLE_COUNT-3; i++) {
			assertEquals( 0.0, target[i], 0.1);
		}
		assertEquals( 1.0, target[i++], 0.1);
		assertEquals( 11.0, target[i++], 0.1);
		assertEquals( 21.0, target[i++], 0.1);
		theSource.addSamples( new double[] {31.0, 32.0, 33.0, 34.0});
		theSource.addSamples( new double[] {41.0, 42.0, 43.0, 44.0});
		theSource.addSamples( new double[] {51.0, 52.0, 53.0, 54.0});
		theSource.addSamples( new double[] {61.0, 62.0, 63.0, 64.0});
		theSource.addSamples( new double[] {71.0, 72.0, 73.0, 74.0});
		theSource.addSamples( new double[] {81.0, 82.0, 83.0, 84.0});
		theSource.addSamples( new double[] {91.0, 92.0, 93.0, 94.0});
		theSource.addSamples( new double[] {101.0, 102.0, 103.0, 104.0});
		theSource.addSamples( new double[] {111.0, 112.0, 113.0, 114.0});
		theSource.addSamples( new double[] {121.0, 122.0, 123.0, 124.0});
		theSource.getSamples( 0 , target, 0, TEST_SAMPLE_COUNT, 0);
		
	}

}
