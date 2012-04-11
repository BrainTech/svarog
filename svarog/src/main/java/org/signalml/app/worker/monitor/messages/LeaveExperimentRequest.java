package org.signalml.app.worker.monitor.messages;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;

public class LeaveExperimentRequest extends AbstractJoinOrLeaveExperimentRequest {

	public LeaveExperimentRequest(ExperimentDescriptor experiment) {
		super(MessageType.LEAVE_EXPERIMENT_REQUEST, experiment);
		setPeerId(experiment.getPeerId());
	}
	
}
