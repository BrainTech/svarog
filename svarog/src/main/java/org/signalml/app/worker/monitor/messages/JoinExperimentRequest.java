package org.signalml.app.worker.monitor.messages;

import java.util.Date;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;

public class JoinExperimentRequest extends AbstractJoinOrLeaveExperimentRequest {

	public JoinExperimentRequest(ExperimentDescriptor experiment) {
		super(MessageType.JOIN_EXPERIMENT_REQUEST, experiment);

		String myPeerId = "svarog" + (new Date().getMinutes()) + "" + (new Date().getSeconds());
		experiment.setPeerId(myPeerId);
		this.setPeerId(myPeerId);
	}
	
}
