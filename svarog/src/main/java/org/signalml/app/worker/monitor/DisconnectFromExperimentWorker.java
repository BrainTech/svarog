package org.signalml.app.worker.monitor;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.JmxClient;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.worker.monitor.messages.AbstractJoinOrLeaveExperimentRequest;
import org.signalml.app.worker.monitor.messages.LeaveExperimentRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.zeromq.ZMQ;

public class DisconnectFromExperimentWorker extends SwingWorker<Void, Void> {

	private ExperimentDescriptor experimentDescriptor;

	public DisconnectFromExperimentWorker(ExperimentDescriptor experimentDescriptor) {
		this.experimentDescriptor = experimentDescriptor;
	}

	@Override
	protected Void doInBackground() throws Exception {
		disconnectFromMultiplexer();
		sendLeaveExperimentRequest();
		return null;
	}

	private void disconnectFromMultiplexer() {
		JmxClient jmxClient = experimentDescriptor.getJmxClient();
		
		if (jmxClient != null)
			try {
				jmxClient.shutdown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		experimentDescriptor.setJmxClient(null);
	}

	private void sendLeaveExperimentRequest() {		
		LeaveExperimentRequest request = new LeaveExperimentRequest(experimentDescriptor);
		String response = Helper.sendRequest(request, experimentDescriptor.getExperimentAddress());
		
		MessageParser.checkIfResponseIsOK(response, MessageType.REQUEST_OK_RESPONSE);
	}

}
