package org.signalml.app.worker.monitor;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.JmxClient;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.worker.monitor.messages.LeaveExperimentRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;

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

		try {
			String response = Helper.sendRequest(request, experimentDescriptor.getExperimentIPAddress(), experimentDescriptor.getExperimentPort());
			MessageParser.checkIfResponseIsOK(response, MessageType.REQUEST_OK_RESPONSE);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
