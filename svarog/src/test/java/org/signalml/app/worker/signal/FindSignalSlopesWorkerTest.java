package org.signalml.app.worker.signal;

import static org.junit.Assert.assertArrayEquals;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.signalml.app.model.tag.SlopeType;

public class FindSignalSlopesWorkerTest extends AbstractSynchronizationTest {

	public void testFindSlopes(SlopeType slopeType, Integer[] expectedPositions) throws InterruptedException, ExecutionException {

		parameters.setSlopeType(slopeType);

		FindSignalSlopesWorker worker = new FindSignalSlopesWorker(parameters);
		worker.setBusyDialogShouldBeShown(false);
		worker.execute();
		Integer[] slopePositions = worker.get();

		assertArrayEquals(expectedPositions, slopePositions);
	}

	@Test
	public void testFindAscendingSlopes() throws InterruptedException, ExecutionException {
		testFindSlopes(SlopeType.ASCENDING, ascendingSlopes);
	}

	@Test
	public void testFindDescendingSlopes() throws InterruptedException, ExecutionException {
		testFindSlopes(SlopeType.DESCENDING, descendingSlopes);
	}

	@Test
	public void testFindBothSlopes() throws InterruptedException, ExecutionException {
		testFindSlopes(SlopeType.BOTH, getBothSlopePositions());
	}

}
