package org.signalml.plugin.newartifact.logic.mgr;

import static org.signalml.plugin.i18n.PluginI18n._;
import static org.signalml.plugin.i18n.PluginI18n._R;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.IPluginComputationMgrStep;
import org.signalml.plugin.method.logic.PluginCheckedThreadGroup;
import org.signalml.plugin.method.logic.PluginComputationMgr;
import org.signalml.plugin.newartifact.data.NewArtifactResult;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrData;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrStepData;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrStepResult;
import org.signalml.plugin.newartifact.method.NewArtifactMethod;

public class NewArtifactComputationMgr extends
	PluginComputationMgr<NewArtifactMgrData, NewArtifactResult> {
	protected static final Logger logger = Logger
										   .getLogger(NewArtifactMethod.class);

	private class TrackerProxy
		extends
		AbstractPluginComputationMgrStepTrackerProxy<NewArtifactComputationProgressPhase> {

		public TrackerProxy(PluginCheckedThreadGroup threadGroup,
							MethodExecutionTracker tracker) {
			super(threadGroup, tracker);
		}

		@Override
		public void advance(IPluginComputationMgrStep step, int tick) {
			synchronized (this.tracker) {
				this.tracker.tick(0, tick);
			}
		}

		@Override
		public void setProgressPhase(NewArtifactComputationProgressPhase phase,
									 Object... arguments) {
			if (this.phase == NewArtifactComputationProgressPhase.ABORT_PHASE) {
				return;
			}
			super.setProgressPhase(phase, arguments);
		}

		@Override
		public boolean isRequestingAbort() {
			boolean result = super.isRequestingAbort();
			if (result) {
				this.setProgressPhase(NewArtifactComputationProgressPhase.ABORT_PHASE);
			}
			return result;
		}

		@Override
		protected String getMessageForPhase(
			NewArtifactComputationProgressPhase phase, Object... arguments) {
			switch (phase) {
			case PREPROCESS_PREPARE_PHASE:
				return _("Preparing");
			case SOURCE_FILE_INITIAL_READ_PHASE:
				return _("Reading source data");
			case INTERMEDIATE_COMPUTATION_PHASE:
				return _R("Computing intermediate data (block {0} of {1})",
						  arguments);
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
	private TrackerProxy trackerProxy;

	@Override
	protected Collection<IPluginComputationMgrStep> prepareStepChain() {
		this.steps = new LinkedList<IPluginComputationMgrStep>();

		this.trackerProxy = new TrackerProxy(this.getThreadGroup(),
											 this.tracker);

		NewArtifactMgrStepData stepData = new NewArtifactMgrStepData(
			data.artifactData, data.constants,
			new NewArtifactIntermediateFilesPathConstructor(
				data.artifactData), this.trackerProxy,
			this.getThreadFactory());

		this.steps.add(new NewArtifactMgrPreprocessStep(stepData));
		this.steps.add(new NewArtifactMgrTagStep(stepData));

		return this.steps;
	}

	@Override
	protected void initializeRun(Map<IPluginComputationMgrStep, Integer> tickMap) {
		for (Map.Entry<IPluginComputationMgrStep, Integer> v : tickMap
				.entrySet()) {
			this.trackerProxy.setTickerLimit(v.getKey(), v.getValue());
		}
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

}
