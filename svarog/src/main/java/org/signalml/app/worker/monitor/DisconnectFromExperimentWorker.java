package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.JmxClient;

import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.KillExperimentRequest;
import org.signalml.app.worker.monitor.messages.LeaveExperimentRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestErrorResponse;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;

public class DisconnectFromExperimentWorker extends SwingWorker<Void, Void> {

	private static Logger logger = Logger.getLogger(DisconnectFromExperimentWorker.class);
	private ExperimentDescriptor experimentDescriptor;

	public DisconnectFromExperimentWorker(ExperimentDescriptor experimentDescriptor) {
		this.experimentDescriptor = experimentDescriptor;
	}

	@Override
	protected Void doInBackground() throws Exception {
		logger.debug("Disconnecting from experiment");
		disconnectFromMultiplexer();
		sendLeaveExperimentRequest();

		if (experimentDescriptor.getRecommendedScenario() != null)
			sendKillExperimentRequest();
		return null;
	}

	private void disconnectFromMultiplexer() {
		JmxClient jmxClient = experimentDescriptor.getJmxClient();
		logger.debug("Shutting down multiplexer");

		if (jmxClient != null)
			try {
				jmxClient.shutdown();
				logger.debug("MUltiplexer was shutdown");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		experimentDescriptor.setJmxClient(null);
	}

	private void sendLeaveExperimentRequest() throws OpenbciCommunicationException {
		LeaveExperimentRequest request = new LeaveExperimentRequest(experimentDescriptor);

		String response = Helper.sendRequest(request, experimentDescriptor.getExperimentIPAddress(), experimentDescriptor.getExperimentPort(), Helper.DEFAULT_RECEIVE_TIMEOUT);
		MessageParser.checkIfResponseIsOK(response, MessageType.REQUEST_OK_RESPONSE);
	}

	private void sendKillExperimentRequest() throws OpenbciCommunicationException {
		KillExperimentRequest request = new KillExperimentRequest(experimentDescriptor);

		String response = Helper.sendRequest(request, Helper.getOpenBCIIpAddress(), Helper.getOpenbciPort(), Helper.DEFAULT_RECEIVE_TIMEOUT);
		MessageType type = MessageType.parseMessageTypeFromResponse(response);
		if (type == MessageType.REQUEST_ERROR_RESPONSE) {
			RequestErrorResponse msg = (RequestErrorResponse) MessageParser.parseMessageFromJSON(response, type);
			Dialogs.showError("Could not kill this experiment in openBCI (error code: " + msg.getErrorCode() + ")");
		}
	}

	@Override
	protected void done() {
		super.done();

		try {
			get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof OpenbciCommunicationException) {
				OpenbciCommunicationException openbciException = (OpenbciCommunicationException) e.getCause();
				logger.debug(_R("There was an error while disconnecting from the experiment ({0})", openbciException.getMessage()));
				//no dialog is shown.
			}
			else {
				e.printStackTrace();
			}
		}
	}

}
