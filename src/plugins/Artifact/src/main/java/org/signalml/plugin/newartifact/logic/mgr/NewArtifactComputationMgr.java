package org.signalml.plugin.newartifact.logic.mgr;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.method.logic.IPluginComputationMgrStep;
import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.PluginComputationMgr;
import org.signalml.plugin.newartifact.data.NewArtifactResult;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrData;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrStepData;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrStepResult;
import org.signalml.plugin.newartifact.method.NewArtifactMethod;
import org.signalml.util.ResolvableString;
import org.springframework.context.MessageSourceResolvable;

public class NewArtifactComputationMgr extends
	PluginComputationMgr<NewArtifactMgrData, NewArtifactResult> {
	protected static final Logger logger = Logger
					       .getLogger(NewArtifactMethod.class);

	private class TrackerProxy implements
		IPluginComputationMgrStepTrackerProxy<NewArtifactProgressPhase> {

		private final MethodExecutionTracker tracker;
		private final int index;

		private NewArtifactProgressPhase phase;

		public TrackerProxy(MethodExecutionTracker tracker, int index) {
			this.tracker = tracker;
			this.index = index;
		}

		@Override
		public void advance(int step) {
			synchronized (this.tracker) {
				this.tracker.tick(this.index, step);
			}
		}

		@Override
		public void setProgressPhase(NewArtifactProgressPhase phase,
					     Object... arguments) {
			if (this.phase == NewArtifactProgressPhase.ABORT_PHASE) {
				return;
			}
			this.phase = phase;
			String messageCode = this.getMessageForPhase(phase);
			if (messageCode != null) {
				MessageSourceResolvable message = new ResolvableString(
					messageCode, arguments);
				if (message != null) {
					synchronized (this.tracker) {
						this.tracker.setMessage(message);
					}
				}
			}
		}

		@Override
		public boolean isRequestingAbort() {
			boolean result;
			synchronized (this.tracker) {
				result = this.tracker.isRequestingAbort();
			}
			if (result) {
				this.setProgressPhase(NewArtifactProgressPhase.ABORT_PHASE);
				this.tracker.setTicker(this.index, this.tracker.getTickerLimits()[this.index]);
			}
			return result;
		}

		private String getMessageForPhase(NewArtifactProgressPhase phase) {
			String suffix = this.doGetMessageForPhase(phase);
			return suffix == null ? null : "newArtifactMethod.step." + suffix;
		}

		private String doGetMessageForPhase(NewArtifactProgressPhase phase) {
			switch (phase) {
			case PREPROCESS_PREPARE_PHASE:
				return "precompute";
			case SOURCE_FILE_INITIAL_READ_PHASE:
				return "readSource";
			case INTERMEDIATE_COMPUTATION_PHASE:
				return "computeBin";
			case TAGGER_PREPARE_PHASE:
				return "prepareTags";
			case TAGGING_PHASE:
				return "tagging";
			case TAG_MERGING_PHASE:
				return "tagMerging";

			case ABORT_PHASE:
				return "abort";
			default:
				return null;
			}
		}

	}

	protected NewArtifactResult doCompute(NewArtifactMgrData data,
					      MethodExecutionTracker tracker) throws ComputationException, InterruptedException,
		PluginToolAbortException {
		Collection<IPluginComputationMgrStep<NewArtifactMgrStepResult>> steps = new LinkedList<IPluginComputationMgrStep<NewArtifactMgrStepResult>>();
		steps.add(new NewArtifactMgrPreprocessStep(this.makeStepData(data,
				tracker, 0)));
		steps.add(new NewArtifactMgrTagStep(this.makeStepData(data, tracker, 0)));

		int ticks = 0;
		for (IPluginComputationMgrStep<NewArtifactMgrStepResult> step : steps) {
			ticks += step.getStepNumberEstimate();
		}
		tracker.setTickerLimit(0, ticks);

		NewArtifactMgrStepResult stepResult = null;
		for (IPluginComputationMgrStep<NewArtifactMgrStepResult> step : steps) {
			stepResult = step.run();
		}

		NewArtifactResult result = new NewArtifactResult();
		result.setTagFile(new File(stepResult.resultTagPath));
		return result;
	}

	private NewArtifactMgrStepData makeStepData(NewArtifactMgrData data,
			MethodExecutionTracker tracker, int stepNumber) {
		return new NewArtifactMgrStepData(data.artifactData, data.constants,
						  new NewArtifactIntermediateFilesPathConstructor(
								  data.artifactData), new TrackerProxy(tracker,
										  stepNumber), this.getThreadFactory());
	}
}
