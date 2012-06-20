package org.signalml.plugin.newstager.logic.mgr;

import static org.signalml.plugin.newstager.NewStagerPlugin._;
import static org.signalml.plugin.newstager.NewStagerPlugin._R;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.signalml.method.MethodExecutionTracker;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.IPluginComputationMgrStep;
import org.signalml.plugin.method.logic.PluginCheckedThreadGroup;
import org.signalml.plugin.method.logic.PluginComputationMgr;
import org.signalml.plugin.newstager.data.NewStagerResult;
import org.signalml.plugin.newstager.data.logic.NewStagerComputationProgressPhase;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrData;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrStepData;
import org.signalml.plugin.newstager.data.logic.NewStagerTagWriteStepResult;

public class NewStagerComputationMgr extends
	PluginComputationMgr<NewStagerMgrData, NewStagerResult> {

	protected static final Logger logger = Logger.getLogger(NewStagerComputationMgr.class);

	protected class TrackerProxy
		extends
		AbstractPluginComputationMgrStepTrackerProxy<NewStagerComputationProgressPhase> {

		public TrackerProxy(PluginCheckedThreadGroup threadGroup,
							MethodExecutionTracker tracker) {
			super(threadGroup, tracker);
		}

		@Override
		public void advance(IPluginComputationMgrStep step, int tick) {
			this.tracker.tick(0, tick);
		}

		@Override
		public boolean isRequestingAbort() {
			boolean result = super.isRequestingAbort();
			if (result) {
				this.setProgressPhase(NewStagerComputationProgressPhase.ABORT_PHASE);
			}
			return result;
		}

		@Override
		protected String getMessageForPhase(
			NewStagerComputationProgressPhase phase, Object... arguments) {
			switch (phase) {
			case SIGNAL_STATS_PREPARE_PHASE:
				return _("Preparing");
			case SIGNAL_STATS_SOURCE_FILE_INITIAL_READ_PHASE:
				return _("Reading source data");
			case SIGNAL_STATS_BLOCK_COMPUTATION_PHASE:
				return _R("Computing signal statistics (block {0} of {1})",
						  arguments);
			case BOOK_FILE_INITIAL_READ_PHASE:
				return _("Reading book file");
			case BOOK_PROCESSING_PHASE:
				return _R("Processing book atoms (segment {0} of {1})", arguments);
			case TAG_WRITING_PREPARE_PHASE:
				return _("Preparing to write tag files");
			case TAG_WRITING_ALPHA:
				return _("Writing primary hypnogram tags (alpha)");
			case TAG_WRITING_DELTA:
				return _("Writing primary hypnogram tags (delta)");
			case TAG_WRITING_SPINDLE:
				return _("Writing primary hypnogram tags (spindle)");
			case TAG_WRITING_SLEEP_PAGES:
				return _("Writing sleep tags");
			case TAG_WRITING_CONSOLIDATED_SLEEP_PAGES:
				return _("Writing consolidated sleep tags");
			case ABORT_PHASE:
				return _("Aborting");
			default:
				return null;
			}
		}

	}

	private List<IPluginComputationMgrStep> steps;
	private TrackerProxy trackerProxy;

	@Override
	protected Collection<IPluginComputationMgrStep> prepareStepChain() {
		this.steps = new LinkedList<IPluginComputationMgrStep>();

		this.trackerProxy = new TrackerProxy(this.getThreadGroup(), tracker);

		NewStagerMgrStepData data = new NewStagerMgrStepData(
			this.data.stagerData, this.data.constants, this.trackerProxy,
			this.getThreadFactory());

		this.steps.add(new NewStagerSignalStatsStep(data));
		this.steps.add(new NewStagerBookProcessStep(data));
		this.steps.add(new NewStagerTagWriteStep(data));

		return this.steps;
	}

	@Override
	protected void initializeRun(
		Map<IPluginComputationMgrStep, Integer> stepTicks) {
		for (Map.Entry<IPluginComputationMgrStep, Integer> entry : stepTicks.entrySet()) {
			this.trackerProxy.setTickerLimit(entry.getKey(), entry.getValue());
		}
	}

	@Override
	protected NewStagerResult prepareComputationResult() {
		if (this.steps.size() != 3) {
			return null;
		}

		NewStagerTagWriteStepResult castedWriterResult;
		try {
			castedWriterResult = (NewStagerTagWriteStepResult) this.stepResults
								 .get(this.steps.get(2));
		} catch (ClassCastException e) {
			logger.error("Unexpected step result class");
			return null;
		}

		NewStagerResult result = new NewStagerResult();
		result.setTagFile(new File(castedWriterResult.primaryTagFilePath));
		return result;
	}

}
