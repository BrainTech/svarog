/* MultichannelSampleBufferTest.java created 2007-09-24
 *
 */

package org.signalml.domain.signal.test;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.signalml.domain.signal.MultichannelSampleBuffer;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.test.Util;

/** MultichannelSampleBufferTest
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MultichannelSampleBufferTest {

	private MultichannelSampleSource source;
	private MultichannelSampleBuffer buffer;

	@Before
	public void setUp() throws Exception {

		source = new MultichannelSampleSourceMock(20,2000000);
		buffer = new MultichannelSampleBuffer(source,32);

	}

	@Test
	public void testBufferAlloc() throws Exception {

		double[] srcData = new double[6];
		source.getSamples(3, srcData, 3, srcData.length, 0);

		double[] bufData = new double[6];
		buffer.getSamples(3, bufData, 3, bufData.length, 0);

		Util.assertDoubleArrayEquals(srcData, bufData);

		assertEquals(20, buffer.getChannelCount());
		assertEquals(2000000, buffer.getSampleCount(3));

		assertEquals(0, buffer.getBoundary(3));
		assertEquals(3, buffer.getMinSample(3));
		assertEquals(9, buffer.getMaxSample(3));
		Util.assertDoubleArrayEquals(srcData, buffer.getBufferCopy(3, 0, 6));

	}

	@Test
	public void testBufferReloc() throws Exception {

		double[] bufData = new double[6];
		buffer.getSamples(3, bufData, 3, bufData.length, 0);

		double[] srcData = new double[6];
		source.getSamples(3, srcData, 13, srcData.length, 0);

		buffer.getSamples(3, bufData, 13, bufData.length, 0);

		Util.assertDoubleArrayEquals(srcData, bufData);

		assertEquals(0, buffer.getBoundary(3));
		assertEquals(13, buffer.getMinSample(3));
		assertEquals(19, buffer.getMaxSample(3));
		Util.assertDoubleArrayEquals(srcData, buffer.getBufferCopy(3, 0, 6));

	}

	@Test
	public void testBufferExtendRight() throws Exception {

		double[] bufData = new double[6];
		buffer.getSamples(3, bufData, 3, bufData.length, 0);

		double[] srcData = new double[6];
		source.getSamples(3, srcData, 6, srcData.length, 0);

		buffer.getSamples(3, bufData, 6, bufData.length, 0);

		Util.assertDoubleArrayEquals(srcData, bufData);

		assertEquals(0, buffer.getBoundary(3));
		assertEquals(3, buffer.getMinSample(3));
		assertEquals(12, buffer.getMaxSample(3));
		Util.assertDoubleArrayEquals(srcData, buffer.getBufferCopy(3, 3, 6));

	}

	@Test
	public void testBufferExtendRightAdjacent() throws Exception {

		double[] bufData = new double[6];
		buffer.getSamples(3, bufData, 3, bufData.length, 0);

		double[] srcData = new double[6];
		source.getSamples(3, srcData, 9, srcData.length, 0);

		buffer.getSamples(3, bufData, 9, bufData.length, 0);

		Util.assertDoubleArrayEquals(srcData, bufData);

		assertEquals(0, buffer.getBoundary(3));
		assertEquals(3, buffer.getMinSample(3));
		assertEquals(15, buffer.getMaxSample(3));

	}

	@Test
	public void testBufferExtendLeft() throws Exception {

		double[] bufData = new double[6];
		buffer.getSamples(3, bufData, 13, bufData.length, 0);

		double[] srcData = new double[6];
		source.getSamples(3, srcData, 10, srcData.length, 0);

		buffer.getSamples(3, bufData, 10, bufData.length, 0);

		Util.assertDoubleArrayEquals(srcData, bufData);

		assertEquals(32-3, buffer.getBoundary(3));
		assertEquals(10, buffer.getMinSample(3));
		assertEquals(19, buffer.getMaxSample(3));

	}

	@Test
	public void testBufferExtendLeftAdjacent() throws Exception {

		double[] bufData = new double[6];
		buffer.getSamples(3, bufData, 13, bufData.length, 0);

		double[] srcData = new double[6];
		source.getSamples(3, srcData, 7, srcData.length, 0);

		buffer.getSamples(3, bufData, 7, bufData.length, 0);

		Util.assertDoubleArrayEquals(srcData, bufData);

		assertEquals(32-6, buffer.getBoundary(3));
		assertEquals(7, buffer.getMinSample(3));
		assertEquals(19, buffer.getMaxSample(3));

	}

	@Test
	public void testBufferExtendBoth() throws Exception {

		double[] bufData = new double[12];
		buffer.getSamples(3, bufData, 13, 6, 0);

		double[] srcData = new double[12];
		source.getSamples(3, srcData, 10, srcData.length, 0);

		buffer.getSamples(3, bufData, 10, bufData.length, 0);

		Util.assertDoubleArrayEquals(srcData, bufData);

		assertEquals(32-3, buffer.getBoundary(3));
		assertEquals(10, buffer.getMinSample(3));
		assertEquals(22, buffer.getMaxSample(3));

	}

	@Test
	public void testBufferInclusive() throws Exception {

		double[] setupData = new double[12];
		buffer.getSamples(3, setupData, 6, setupData.length, 0);

		double[] srcData = new double[6];
		double[] bufData = new double[6];
		source.getSamples(3, srcData, 9, srcData.length, 0);

		buffer.getSamples(3, bufData, 9, bufData.length, 0);

		Util.assertDoubleArrayEquals(srcData, bufData);

		assertEquals(0, buffer.getBoundary(3));
		assertEquals(6, buffer.getMinSample(3));
		assertEquals(18, buffer.getMaxSample(3));

	}

	@Test
	public void testBufferOversize() throws Exception {

		buffer.setBufferLength(3);

		double[] srcData = new double[6];
		double[] bufData = new double[6];
		source.getSamples(3, srcData, 9, srcData.length, 0);

		buffer.getSamples(3, bufData, 9, bufData.length, 0);

		Util.assertDoubleArrayEquals(srcData, bufData);

		assertEquals(0, buffer.getBoundary(3));
		assertEquals(0, buffer.getMinSample(3));
		assertEquals(0, buffer.getMaxSample(3));

	}

	@Test
	public void testBufferIterateForward() throws Exception {

		buffer.setBufferLength(12);

		double[] srcData = new double[5];
		double[] bufData = new double[5];

		for (int i=0; i<100; i++) {
			source.getSamples(3, srcData, 9+(i+1)*srcData.length, srcData.length, 0);
			buffer.getSamples(3, bufData, 9+(i+1)*bufData.length, bufData.length, 0);

			Util.assertDoubleArrayEquals(srcData, bufData);
		}

	}

	@Test
	public void testBufferIterateBackward() throws Exception {

		buffer.setBufferLength(12);

		double[] srcData = new double[5];
		double[] bufData = new double[5];

		for (int i=0; i<100; i++) {
			source.getSamples(3, srcData, 20000-(i+1)*srcData.length, srcData.length, 0);
			buffer.getSamples(3, bufData, 20000-(i+1)*bufData.length, bufData.length, 0);

			Util.assertDoubleArrayEquals(srcData, bufData);
		}

	}

}
