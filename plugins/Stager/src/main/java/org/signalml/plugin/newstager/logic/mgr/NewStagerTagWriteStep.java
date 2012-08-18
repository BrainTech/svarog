package org.signalml.plugin.newstager.logic.mgr;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStep;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorStepResult;
import org.signalml.plugin.newstager.data.logic.NewStagerComputationProgressPhase;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrStepData;
import org.signalml.plugin.newstager.data.logic.NewStagerTagWriteStepResult;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;
import org.signalml.plugin.newstager.io.NewStagerTagWriter;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerTagMergerHelper;
import org.signalml.plugin.signal.PluginSignalHelper;

public class NewStagerTagWriteStep extends
		AbstractPluginComputationMgrStep<NewStagerMgrStepData> {

	private File primaryTagFile;
	private Integer signalBlockCount;

	public NewStagerTagWriteStep(NewStagerMgrStepData data) {
		super(data);
		this.primaryTagFile = null;
	}

	@Override
	public int getStepNumberEstimate() {
		return this.getNumberOfOutputFiles() * this.getSignalBlockCountScaled();
	}

	@Override
	protected NewStagerTagWriteStepResult prepareStepResult() {
		if (this.primaryTagFile == null) {
			return null;
		}

		return new NewStagerTagWriteStepResult(
				this.primaryTagFile.getAbsolutePath());
	}

	@Override
	protected PluginComputationMgrStepResult doRun(
			PluginComputationMgrStepResult prevStepResult)
			throws PluginToolAbortException, PluginToolInterruptedException,
			ComputationException {

		NewStagerBookProcessorStepResult tagResult;
		try {
			tagResult = (NewStagerBookProcessorStepResult) prevStepResult;
		} catch (ClassCastException e) {
			throw new ComputationException(e);
		}

		this.data.tracker
				.setProgressPhase(NewStagerComputationProgressPhase.TAG_WRITING_PREPARE_PHASE);

		Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> tags = NewStagerTagMergerHelper
				.MergeTags(tagResult, this.data.constants);
		try {
			this.writeTagsToFile(tags);
		} catch (IOException e) {
			throw new ComputationException(e);
		} catch (SignalMLException e) {
			throw new ComputationException(e);
		}

		return this.prepareStepResult();
	}

	private void writeTagsToFile(
			Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> tagMap)
			throws IOException, SignalMLException {
		NewStagerTagWriter writer = new NewStagerTagWriter(
				this.data.stagerData, this.data.constants);

		if (this.data.stagerData.getParameters().primaryHypnogramFlag) {
			this.updateTracker(NewStagerComputationProgressPhase.TAG_WRITING_ALPHA);
			writer.writeTags(NewStagerTagCollectionType.HYPNO_ALPHA,
					EnumSet.of(NewStagerTagCollectionType.HYPNO_ALPHA), tagMap);

			this.updateTracker(NewStagerComputationProgressPhase.TAG_WRITING_DELTA);
			writer.writeTags(NewStagerTagCollectionType.HYPNO_DELTA,
					EnumSet.of(NewStagerTagCollectionType.HYPNO_DELTA), tagMap);

			this.updateTracker(NewStagerComputationProgressPhase.TAG_WRITING_SPINDLE);
			writer.writeTags(NewStagerTagCollectionType.HYPNO_SPINDLE,
					EnumSet.of(NewStagerTagCollectionType.HYPNO_SPINDLE),
					tagMap);
		}

		this.updateTracker(NewStagerComputationProgressPhase.TAG_WRITING_SLEEP_PAGES);
		writer.writeTags(NewStagerTagCollectionType.SLEEP_PAGES,
				NewStagerTagCollectionType.GetSleepStages(), tagMap);

		this.updateTracker(NewStagerComputationProgressPhase.TAG_WRITING_CONSOLIDATED_SLEEP_PAGES);
		this.primaryTagFile = writer
				.writeTags(
						NewStagerTagCollectionType.CONSOLIDATED_SLEEP_PAGES,
						NewStagerTagCollectionType.GetConsolidatedSleepStages(),
						tagMap);
	}

	private int getSignalBlockCount() {
		if (this.signalBlockCount == null) {
			this.signalBlockCount = PluginSignalHelper.GetBlockCount(
					this.data.stagerData.getSampleSource(),
					this.data.constants.getBlockLength());
		}

		return this.signalBlockCount;
	}

	private int getSignalBlockCountScaled() {
		return this.getSignalBlockCount() / 20;
	}

	private void updateTracker(NewStagerComputationProgressPhase phase) {
		this.data.tracker.setProgressPhase(phase);

		boolean primaryFlag = this.data.stagerData.getParameters().primaryHypnogramFlag;
		int numberOfFiles = this.getNumberOfOutputFiles();
		int pos;

		switch (phase) {
		case TAG_WRITING_ALPHA:
			pos = 1;
			break;
		case TAG_WRITING_DELTA:
			pos = 2;
			break;
		case TAG_WRITING_SPINDLE:
			pos = 3;
			break;
		case TAG_WRITING_SLEEP_PAGES:
			pos = primaryFlag ? 4 : 1;
		case TAG_WRITING_CONSOLIDATED_SLEEP_PAGES:
			pos = primaryFlag ? 5 : 2;
		default:
			return;
		}

		int blockCount = this.getSignalBlockCountScaled();
		this.data.tracker
				.advance(this, (int) (((double) pos) / numberOfFiles
						* blockCount - ((double) pos - 1) / numberOfFiles
						* blockCount));
	}

	private int getNumberOfOutputFiles() {
		boolean primaryFlag = this.data.stagerData.getParameters().primaryHypnogramFlag;
		return primaryFlag ? 5 : 2;
	}

}
