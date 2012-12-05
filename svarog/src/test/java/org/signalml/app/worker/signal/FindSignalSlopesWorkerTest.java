package org.signalml.app.worker.signal;

import static org.junit.Assert.assertArrayEquals;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.signalml.app.model.tag.SlopeType;

public class FindSignalSlopesWorkerTest extends AbstractSynchronizationTest {

	public void testFindSlopes(SlopeType slopeType, Integer[] expectedPositions, int lengthThreshold) throws InterruptedException, ExecutionException {

		parameters.setSlopeType(slopeType);

		parameters.setLengthThresholdValue(convertSamplesToTime(lengthThreshold));
		parameters.setLengthThresholdEnabled(lengthThreshold != 0);

		FindSignalSlopesWorker worker = new FindSignalSlopesWorker(parameters);
		worker.setBusyDialogShouldBeShown(false);
		worker.execute();
		Integer[] slopePositions = worker.get();

		assertArrayEquals(expectedPositions, slopePositions);
	}

	@Test
	public void testFindAscendingSlopes() throws InterruptedException, ExecutionException {
		testFindSlopes(SlopeType.ASCENDING, getSlopePositions(SlopeType.ASCENDING, 0), 0);
	}

	@Test
	public void testFindDescendingSlopes() throws InterruptedException, ExecutionException {
		testFindSlopes(SlopeType.DESCENDING, getSlopePositions(SlopeType.DESCENDING, 0), 0);
	}

	@Test
	public void testFindBothSlopes() throws InterruptedException, ExecutionException {
		testFindSlopes(SlopeType.BOTH, getSlopePositions(SlopeType.BOTH, 0), 0);
	}

	@Test
	public void testFindAscendingSlopesWithLengthThreshold() throws InterruptedException, ExecutionException {
		int threshold = 50;
		testFindSlopes(SlopeType.ASCENDING, getSlopePositions(SlopeType.ASCENDING, threshold), threshold);
	}

	@Test
	public void testDescendingSlopesWithLengthThreshold() throws InterruptedException, ExecutionException {
		int threshold = 51;
		testFindSlopes(SlopeType.DESCENDING, getSlopePositions(SlopeType.DESCENDING, threshold), threshold);
	}

	@Test
	public void testBothSlopesWithLengthThreshold() throws InterruptedException, ExecutionException {
		int threshold = 51;
		testFindSlopes(SlopeType.BOTH, getSlopePositions(SlopeType.BOTH, threshold), threshold);
	}

	protected float  convertSamplesToTime(int numberOfSamples) {
		return (numberOfSamples)/parameters.getSampleSource().getSamplingFrequency();
	}

}
