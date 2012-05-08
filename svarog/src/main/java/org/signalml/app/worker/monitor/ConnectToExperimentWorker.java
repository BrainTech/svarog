package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.awt.Container;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import multiplexer.jmx.client.JmxClient;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.GetExperimentContactRequest;
import org.signalml.app.worker.monitor.messages.GetExperimentContactResponse;
import org.signalml.app.worker.monitor.messages.GetPeerParametersValuesRequest;
import org.signalml.app.worker.monitor.messages.GetPeerParametersValuesResponse;
import org.signalml.app.worker.monitor.messages.JoinExperimentRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestErrorResponse;
import org.signalml.app.worker.monitor.messages.RequestOKResponse;
import org.signalml.app.worker.monitor.messages.StartEEGSignalRequest;
import org.signalml.app.worker.monitor.messages.StartEEGSignalResponse;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.signalml.multiplexer.protocol.SvarogConstants;

public class ConnectToExperimentWorker extends SwingWorkerWithBusyDialog<JmxClient, Void> {

	public static final int TIMEOUT_MILIS = 500;
	public static final int TRYOUT_COUNT = 20;
	private static Logger logger = Logger.getLogger(ConnectToExperimentWorker.class);

	private ExperimentDescriptor experimentDescriptor;

	private String multiplexerAddress;
	private int multiplexerPort;

	private InetSocketAddress multiplexerSocket;

	public ConnectToExperimentWorker(Container parentContainer, ExperimentDescriptor experimentDescriptor) {
		super(parentContainer);
		this.experimentDescriptor = experimentDescriptor;
	}

	public ExperimentDescriptor getExperimentDescriptor() {
		return experimentDescriptor;
	}

	@Override
	protected JmxClient doInBackground() throws Exception {

		showBusyDialog();
		if (experimentDescriptor.getStatus() == ExperimentStatus.NEW) {
			startNewExperiment();
			waitForExperimentToStart();
		}

		sendJoinExperimentRequest();
		return connectToMultiplexer();
	}

	protected void startNewExperiment() throws OpenbciCommunicationException {
		StartEEGSignalRequest request = new StartEEGSignalRequest(experimentDescriptor);

		StartEEGSignalResponse response = (StartEEGSignalResponse) Helper.sendRequestAndParseResponse(request,
										  Helper.getOpenBCIIpAddress(),
										  Helper.getOpenbciPort(),
										  MessageType.START_EEG_SIGNAL_RESPONSE);

		experimentDescriptor.setId(response.getSender());

		getExperimentContact();
	}

	protected void getExperimentContact() throws OpenbciCommunicationException {
		GetExperimentContactRequest request = new GetExperimentContactRequest(experimentDescriptor.getId());

		GetExperimentContactResponse response = (GetExperimentContactResponse) Helper.sendRequestAndParseResponse(
				request,
				Helper.getOpenBCIIpAddress(),
				Helper.getOpenbciPort(),
				MessageType.GET_EXPERIMENT_CONTACT_RESPONSE);

		experimentDescriptor.setExperimentIPAddress(response.getExperimentIPAddress());
		experimentDescriptor.setExperimentPort(response.getExperimentPort());
	}

	protected void waitForExperimentToStart() throws OpenbciCommunicationException {

		GetPeerParametersValuesRequest request = new GetPeerParametersValuesRequest();
		request.setPeerId("amplifier");

		for (int i = 0; i < TRYOUT_COUNT; i++) {

			GetPeerParametersValuesResponse response =
					(GetPeerParametersValuesResponse) Helper.sendRequestAndParseResponse(request,
					experimentDescriptor.getExperimentIPAddress(),
					experimentDescriptor.getExperimentPort(),
					MessageType.GET_PEER_PARAMETERS_VALUES_RESPONSE);

			if (response.isAmplifierStarted())
				break;
			else
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}

	}

	protected void sendJoinExperimentRequest() throws OpenbciCommunicationException {
		JoinExperimentRequest request = new JoinExperimentRequest(experimentDescriptor);
		RequestOKResponse response = null;
		MessageType responseType = null;
		String responseString = null;

		for (int i = 0; i < TRYOUT_COUNT; i++) {

			responseString = Helper.sendRequest(request,
					experimentDescriptor.getExperimentIPAddress(),
					experimentDescriptor.getExperimentPort(),
					Helper.DEFAULT_RECEIVE_TIMEOUT);

			responseType = MessageType.parseMessageTypeFromResponse(responseString);
			if (responseType != MessageType.REQUEST_ERROR_RESPONSE) {
				response = (RequestOKResponse) MessageParser.parseMessageFromJSON(responseString, MessageType.REQUEST_OK_RESPONSE);
				break;
			}
			else {
				logger.warn("Error while connecting to experiment, retrying");
				try {
					Thread.sleep(TIMEOUT_MILIS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if (response == null) {
			throw new OpenbciCommunicationException(_R("There was an error while joinging to the experiment."));
		} else if (responseType == MessageType.REQUEST_ERROR_RESPONSE) {
			RequestErrorResponse errorResponse = (RequestErrorResponse) MessageParser.parseMessageFromJSON(responseString, MessageType.REQUEST_OK_RESPONSE);
			throw new OpenbciCommunicationException(_R("There was an error while joinging to the experiment ({0}).", errorResponse.getErrorCode()));
		}

		String mxAddr = (String) response.getParams().get("mx_addr");
		StringTokenizer tokenizer = new StringTokenizer(mxAddr, ":");
		multiplexerAddress = tokenizer.nextToken();
		multiplexerPort = Integer.parseInt(tokenizer.nextToken());
	}

	protected JmxClient connectToMultiplexer() {
		JmxClient client;

		multiplexerSocket = new InetSocketAddress(multiplexerAddress, multiplexerPort);
		client = new JmxClient(SvarogConstants.PeerTypes.STREAM_RECEIVER);
		ChannelFuture connectFuture = client.asyncConnect(multiplexerSocket);

		int i = 0;
		while (!isCancelled() && i < TRYOUT_COUNT) {
			i++;
			try {
				Thread.sleep(TIMEOUT_MILIS);
			}
			catch (InterruptedException e1) {}
			if ((connectFuture.isDone())) {
				break;
			}
		}

		return client;
	}

	@Override
	protected void done() {
		super.done();

		try {
			get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (ExecutionException e) {
			if (e.getCause() instanceof OpenbciCommunicationException) {
				OpenbciCommunicationException exception = (OpenbciCommunicationException) e.getCause();
				exception.showErrorDialog(_("An error occurred while connecting to experiment"));
			}
			else {
				e.printStackTrace();
			}
		}
	}

}
