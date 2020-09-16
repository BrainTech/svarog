package org.signalml.app.view.signal;

import junit.framework.TestCase;
import org.junit.Test;

public class StaticRenderingHelperTest extends TestCase {

	public static final double PAGE_SIZE = 10.0; // seconds

	private static final double SAMPLING_FREQUENCY = 1000.0; // Hz

	private StaticRenderingHelper helper;
	private StaticRendererHelperTestMonitorTagMock tag;

	public StaticRenderingHelperTest() {
		super();
	}

	@Override
	public void setUp() {
		// 10-second page size at 1000 Hz
		helper = new StaticRenderingHelper(1, (int) Math.round(SAMPLING_FREQUENCY * PAGE_SIZE), SAMPLING_FREQUENCY);
	}

	@Test
	public void testShortTag() {
		TagTiming timing;

		// 2-second tag starting at 4.0
		tag = new StaticRendererHelperTestMonitorTagMock(4.0, 2.0);
		setNow(5.0); // now is 5.0
		timing = helper.computeTagTiming(tag);
		assertEquals(4.0, timing.position);
		assertEquals(1.0, timing.length);

		// after a second...
		setNow(6.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(4.0, timing.position);
		assertEquals(2.0, timing.length);

		// same at the end of an epoch
		setNow(10.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(4.0, timing.position);
		assertEquals(2.0, timing.length);

		// as we approach the same position in the next epoch...
		setNow(15.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(5.0, timing.position);
		assertEquals(1.0, timing.length);

		// the tag shrinks to 0
		setNow(16.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(6.0, timing.position);
		assertEquals(0.0, timing.length);
	}

	public void testFutureTag() {
		TagTiming timing;

		// 2-second tag starting at 10.0
		tag = new StaticRendererHelperTestMonitorTagMock(10.0, 2.0);
		setNow(5.0); // now is 5.0
		timing = helper.computeTagTiming(tag);
		assertEquals(0.0, timing.position);
		assertEquals(0.0, timing.length);

		// same at the end of an epoch
		setNow(10.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(0.0, timing.position);
		assertEquals(0.0, timing.length);

		// it starts to be visible only in the proper cycle
		setNow(11.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(0.0, timing.position);
		assertEquals(1.0, timing.length);

		setNow(15.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(0.0, timing.position);
		assertEquals(2.0, timing.length);

		// and disappeares in the next cycle
		setNow(21.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(1.0, timing.position);
		assertEquals(1.0, timing.length);
	}

	public void testCrossingTag() {
		TagTiming timing;

		// 2-second tag starting at 9.0
		tag = new StaticRendererHelperTestMonitorTagMock(9.0, 2.0);
		setNow(9.0); // now is 9.0
		timing = helper.computeTagTiming(tag);
		assertEquals(9.0, timing.position);
		assertEquals(0.0, timing.length);

		// still visible...
		setNow(9.5);
		timing = helper.computeTagTiming(tag);
		assertEquals(9.0, timing.position);
		assertEquals(0.5, timing.length);

		// second part visible in the new epoch
		setNow(10.5);
		timing = helper.computeTagTiming(tag);
		assertEquals(0.0, timing.position);
		assertEquals(0.5, timing.length);

		// through the entire epoch
		setNow(15.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(0.0, timing.position);
		assertEquals(1.0, timing.length);

		// and finally disappeares only in the next one
		setNow(20.5);
		timing = helper.computeTagTiming(tag);
		assertEquals(0.5, timing.position);
		assertEquals(0.5, timing.length);
	}

	public void testVeryLongTag() {
		TagTiming timing;

		// 20-second tag starting at 5.0
		tag = new StaticRendererHelperTestMonitorTagMock(5.0, 20.0);
		setNow(4.0); // now is 4.0
		timing = helper.computeTagTiming(tag);
		assertEquals(5.0, timing.position);
		assertEquals(0.0, timing.length);

		// starts to be visible
		setNow(6.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(5.0, timing.position);
		assertEquals(1.0, timing.length);

		// at the end of an epoch
		setNow(9.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(5.0, timing.position);
		assertEquals(4.0, timing.length);

		// at the new epoch, switching to current part
		setNow(11.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(0.0, timing.position);
		assertEquals(1.0, timing.length);

		// through the entire epoch
		setNow(19.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(0.0, timing.position);
		assertEquals(9.0, timing.length);

		// and partially through the next one
		setNow(21.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(0.0, timing.position);
		assertEquals(1.0, timing.length);

		// and partially through the next one
		setNow(29.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(0.0, timing.position);
		assertEquals(5.0, timing.length);

		// to disappear finally in the next
		setNow(33.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(3.0, timing.position);
		assertEquals(2.0, timing.length);

		setNow(35.0);
		timing = helper.computeTagTiming(tag);
		assertEquals(5.0, timing.position);
		assertEquals(0.0, timing.length);
	}

	private void setNow(double now) {
		tag.setNow(now);
		helper.setState(Math.round(now * SAMPLING_FREQUENCY));
	}
}
