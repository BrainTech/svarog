package org.signalml.app.worker.signal;

import java.util.concurrent.ExecutionException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.signalml.app.model.tag.SlopeType;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.Tag;

public class SynchronizeTagsWithTriggerWorkerTest extends AbstractSynchronizationTest {

	public void testSlope(SlopeType slopeType, Integer[] expectedPositions) throws InterruptedException, ExecutionException {

		parameters.setSlopeType(slopeType);
		FindSignalSlopesWorker signalSlopesWorker = new FindSignalSlopesWorker(parameters);
		signalSlopesWorker.setBusyDialogShouldBeShown(false);
		signalSlopesWorker.execute();
		Integer[] slopes = signalSlopesWorker.get();

		SynchronizeTagsWithTriggerWorker worker = new SynchronizeTagsWithTriggerWorker(parameters, slopes);
		worker.setBusyDialogShouldBeShown(false);
		worker.execute();
		worker.get();

		StyledTagSet tagSet = parameters.getTagSet();
		int i = 0;
		for (Tag tag: tagSet.getTags()) {
			double tagPositionSample = tag.getPosition() * parameters.getSampleSource().getSamplingFrequency();
			assertEquals(expectedPositions[i], tagPositionSample, 1e-5);
			i++;
		}
	}

	@Test
	public void testSynchronizeAscendingSlope() throws InterruptedException, ExecutionException {
		testSlope(SlopeType.ASCENDING, getSlopePositions(SlopeType.ASCENDING, 0));
	}

	@Test
	public void testSynchronizeDescendingSlope() throws InterruptedException, ExecutionException {
		testSlope(SlopeType.DESCENDING, getSlopePositions(SlopeType.DESCENDING, 0));
	}

	@Test
	public void testSynchronizeToBothSlopes() throws InterruptedException, ExecutionException {
		testSlope(SlopeType.BOTH, getSlopePositions(SlopeType.BOTH, 0));
	}

	@Test
	public void testRemoveExcessiveTags() throws InterruptedException, ExecutionException {

		StyledTagSet tagSet = parameters.getTagSet();
		Integer[] positions = new Integer[] { 1, 2 };

		Tag[] remainingTags = new Tag[positions.length];
		for (int i = 0; i < positions.length; i++)
			remainingTags[i] = tagSet.getChannelTagAt(i);

		SynchronizeTagsWithTriggerWorker worker = new SynchronizeTagsWithTriggerWorker(parameters, positions);
		worker.setBusyDialogShouldBeShown(false);
		worker.execute();
		worker.get();

		int channelStyleCount = tagSet.getChannelTagCount();
		assertEquals(positions.length, channelStyleCount);

		for (int i = 0; i < remainingTags.length; i++)
			assertEquals(tagSet.getChannelTagAt(i), remainingTags[i]);
	}

}
