package org.signalml.plugin.newartifact.logic.mgr;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.method.logic.IPluginComputationMgrStep;
import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.PluginComputationMgr;
import org.signalml.plugin.newartifact.data.NewArtifactResult;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrData;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrStepData;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrStepResult;
import org.signalml.plugin.newartifact.method.NewArtifactMethod;
import static org.signalml.plugin.newartifact.NewArtifactPlugin._;
import static org.signalml.plugin.newartifact.NewArtifactPlugin._R;

public class NewArtifactComputationMgr extends
	PluginComputationMgr<NewArtifactMgrData, NewArtifactResult> {
	protected static final Logger logger = Logger
										   .getLogger(NewArtifactMethod.class);

	private class TrackerProxy implements
		IPluginComputationMgrStepTrackerProxy<NewArtifactProgressPhase> {

		private final CheckedThreadGroup threadGroup;

		private final MethodExecutionTracker tracker;
		private final int index;

		private NewArtifactProgressPhase phase;

		public TrackerProxy(CheckedThreadGroup threadGroup,
							MethodExecutionTracker tracker, int index) {
			this.threadGroup = threadGroup;
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
			String message = this.getMessageForPhase(phase, arguments);
			if (message != null) {
				synchronized (this.tracker) {
					this.tracker.setMessage(message);
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
				synchronized (this.tracker) {
					this.tracker.setTicker(this.index,
										   this.tracker.getTickerLimits()[this.index]);
				}
			}
			return result;
		}

		@Override
		public boolean isInterrupted() {
			return this.threadGroup.isShutdownStarted() || Thread.interrupted();
		}

		private String getMessageForPhase(NewArtifactProgressPhase phase, Object ... arguments) {
			switch (phase) {
			case PREPROCESS_PREPARE_PHASE:
				return _("Preparing");
			case SOURCE_FILE_INITIAL_READ_PHASE:
				return _("Reading source data");
			case INTERMEDIATE_COMPUTATION_PHASE:
				return _R("Computing intermediate data (block {0} of {1})", arguments);
			case TAGGER_PREPARE_PHASE:
				return _("Preparing tagger");
			case TAGGING_PHASE:
				return _("Tagging artifacts");
			case TAG_MERGING_PHASE:
				return _("Merging tag files");
			case ABORT_PHASE:
				return _("Aborting");
			default:
				return null;
			}
		}
	}

	private List<IPluginComputationMgrStep> steps;

	@Override
	protected Collection<IPluginComputationMgrStep> prepareStepChain() {
		this.steps = new LinkedList<IPluginComputationMgrStep>();
		this.steps.add(new NewArtifactMgrPreprocessStep(this.makeStepData(
						   this.data, this.tracker, 0)));
		this.steps.add(new NewArtifactMgrTagStep(this.makeStepData(this.data,
					   this.tracker, 0)));
		return this.steps;
	}

	@Override
	protected void initializeRun(Collection<IPluginComputationMgrStep> steps,
								 int ticks) {
		this.tracker.setTickerLimit(0, ticks);
	}

	@Override
	protected NewArtifactResult prepareComputationResult() {
		PluginComputationMgrStepResult lastStepResult = this.stepResults
				.get(this.steps.get(this.steps.size() - 1));
		if (lastStepResult == null) {
			return null;
		}

		NewArtifactMgrStepResult castedLastStepResult;
		try {
			castedLastStepResult = (NewArtifactMgrStepResult) lastStepResult;
		} catch (ClassCastException e) {
			logger.error("Unexpected step result class");
			return null;
		}

		NewArtifactResult result = new NewArtifactResult();
		result.setTagFile(new File(castedLastStepResult.resultTagPath));
		return result;
	}

	private NewArtifactMgrStepData makeStepData(NewArtifactMgrData data,
			MethodExecutionTracker tracker, int stepNumber) {
		return new NewArtifactMgrStepData(data.artifactData, data.constants,
										  new NewArtifactIntermediateFilesPathConstructor(
											  data.artifactData), new TrackerProxy(
											  this.getThreadGroup(), tracker, stepNumber),
										  this.getThreadFactory());
	}

}
