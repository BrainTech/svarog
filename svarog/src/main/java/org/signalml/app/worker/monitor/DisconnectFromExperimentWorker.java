package org.signalml.app.worker.monitor;

import java.util.List;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.KillExperimentRequest;
import org.signalml.app.worker.monitor.messages.LeaveExperimentRequest;
import org.signalml.app.worker.monitor.messages.LauncherMessage;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestErrorResponse;
import org.signalml.peer.Peer;
import static org.signalml.app.util.i18n.SvarogI18n._;

public class DisconnectFromExperimentWorker extends SwingWorker<Void, Void> {

	private static Logger logger = Logger.getLogger(DisconnectFromExperimentWorker.class);
	private ExperimentDescriptor experimentDescriptor;

	public DisconnectFromExperimentWorker(ExperimentDescriptor experimentDescriptor) {
		this.experimentDescriptor = experimentDescriptor;
	}

	@Override
	protected Void doInBackground() throws Exception {
		logger.debug("Disconnecting from experiment");

		if (!experimentDescriptor.isConnected()) {
			logger.debug("Experiment already disconnected - cancelling disconnection.");
			return null;
		}

		disconnectFromMultiplexer();
		sendLeaveExperimentRequest();

		if (experimentDescriptor.getRecommendedScenario() != null)
			sendKillExperimentRequest();

		experimentDescriptor.setConnected(false);
		return null;
	}

	private void disconnectFromMultiplexer() {
		Peer peer = experimentDescriptor.getPeer();
		logger.debug("Shutting down peer");

		if (peer != null) {
			peer.shutdown();
			logger.debug("Peer has been successfully shut down");
		}
		experimentDescriptor.setPeer(null);
	}

	private void sendLeaveExperimentRequest() throws OpenbciCommunicationException {
		LeaveExperimentRequest request = new LeaveExperimentRequest(experimentDescriptor);

		Helper.sendRequestAndParseResponse(request,
						   experimentDescriptor.getFirstRepHost(),
						   experimentDescriptor.getFirstRepPort(),
						   MessageType.REQUEST_OK_RESPONSE);
	}

	private void sendKillExperimentRequest() throws OpenbciCommunicationException {
		KillExperimentRequest request = new KillExperimentRequest(experimentDescriptor);

		BaseMessage response = Helper.sendRequestAndParseResponse(request,
									   Helper.getOpenBCIIpAddress(),
									   Helper.getOpenbciPort(),
									   MessageType.KILL_EXPERIMENT_RESPONSE);
		MessageType type = response.getType();
		if (type == MessageType.REQUEST_ERROR_RESPONSE) {
			RequestErrorResponse msg = (RequestErrorResponse) response;
			Dialogs.showError(String.format(_("Could not kill this experiment in openBCI (error code: %s)"), msg.getErrorCode()));
		}
	}

	@Override
	protected void done() {
		super.done();

		try {
			get();
		} catch (InterruptedException e) {
			logger.error("", e);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof OpenbciCommunicationException) {
				OpenbciCommunicationException openbciException = (OpenbciCommunicationException) e.getCause();
				logger.debug(_R("There was an error while disconnecting from the experiment ({0})", openbciException.getMessage()));
				//no dialog is shown.
			}
			else {
				logger.error("", e);
			}
		}
	}

}
