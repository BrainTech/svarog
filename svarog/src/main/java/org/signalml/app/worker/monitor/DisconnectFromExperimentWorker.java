package org.signalml.app.worker.monitor;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.JmxClient;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.worker.monitor.zeromq.JoinOrLeaveExperimentsRequest;
import org.signalml.app.worker.monitor.zeromq.MessageType;
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
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket socket = context.socket(ZMQ.REQ);
		socket.connect(experimentDescriptor.getExperimentAddress());
		
		JoinOrLeaveExperimentsRequest request = new JoinOrLeaveExperimentsRequest(experimentDescriptor);
		request.setType(MessageType.LEAVE_EXPERIMENT);
		request.setPeerId(experimentDescriptor.getPeerId());
		
		String requestString = request.toJSON();
		socket.send(requestString.getBytes(), 0);
		
		byte[] responseBytes = socket.recv(0);
		String response = new String(responseBytes);
		System.out.println("got response = " + response);
	}

}
