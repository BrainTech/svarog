package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.JmxClient;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.messages.GetExperimentContactRequest;
import org.signalml.app.worker.monitor.messages.GetExperimentContactResponse;
import org.signalml.app.worker.monitor.messages.JoinExperimentRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestOKResponse;
import org.signalml.app.worker.monitor.messages.StartEEGSignalRequest;
import org.signalml.app.worker.monitor.messages.StartEEGSignalResponse;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.signalml.multiplexer.protocol.SvarogConstants;

public class ConnectToExperimentWorker extends SwingWorker<JmxClient, Void> {

	public static final int TIMEOUT_MILIS = 500;
	public static final int TRYOUT_COUNT = 20;
	private static Logger logger = Logger.getLogger(ConnectToExperimentWorker.class);

	private ExperimentDescriptor experimentDescriptor;

	private String multiplexerAddress;
	private int multiplexerPort;

	private InetSocketAddress multiplexerSocket;

	public ConnectToExperimentWorker(ExperimentDescriptor experimentDescriptor) {
		this.experimentDescriptor = experimentDescriptor;
	}

	@Override
	protected JmxClient doInBackground() throws Exception {

		if (experimentDescriptor.getStatus() == ExperimentStatus.NEW) {
			if (!startNewExperiment())
				return null;
		}

		if (sendJoinExperimentRequest())
			return connectToMultiplexer();

		return null;
	}

	protected boolean startNewExperiment() {
		StartEEGSignalRequest request = new StartEEGSignalRequest(experimentDescriptor);

		StartEEGSignalResponse response = (StartEEGSignalResponse) Helper.sendRequestAndParseResponse(request,
										  Helper.getOpenBCIIpAddress(),
										  Helper.getOpenbciPort(),
										  MessageType.START_EEG_SIGNAL_RESPONSE);

		if (response == null)
			return false;

		experimentDescriptor.setId(response.getSender());

		return getExperimentContact();
	}

	protected boolean getExperimentContact() {
		GetExperimentContactRequest request = new GetExperimentContactRequest(experimentDescriptor.getId());

		GetExperimentContactResponse response = (GetExperimentContactResponse) Helper.sendRequestAndParseResponse(
				request,
				Helper.getOpenBCIIpAddress(),
				Helper.getOpenbciPort(),
				MessageType.GET_EXPERIMENT_CONTACT_RESPONSE);

		experimentDescriptor.setExperimentIPAddress(response.getExperimentIPAddress());
		experimentDescriptor.setExperimentPort(response.getExperimentPort());

		return response != null;
	}

	protected boolean sendJoinExperimentRequest() throws JsonParseException, JsonProcessingException, IOException {
		JoinExperimentRequest request = new JoinExperimentRequest(experimentDescriptor);
		RequestOKResponse response = null;

		for (int i = 0; i < TRYOUT_COUNT; i++) {
			String responseString = Helper.sendRequestAndHandleExceptions(request,
									experimentDescriptor.getExperimentIPAddress(),
									experimentDescriptor.getExperimentPort());

			MessageType type = MessageType.parseMessageTypeFromResponse(responseString);
			if (type != MessageType.REQUEST_ERROR_RESPONSE) {
				response = (RequestOKResponse) MessageParser.parseMessageFromJSON(responseString, MessageType.REQUEST_OK_RESPONSE);
				break;
			}
			else {
				logger.warn("Error while connecting to experiment, retrying");
				try {
					Thread.sleep(TIMEOUT_MILIS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (response == null)
			return false;

		String mxAddr = (String) response.getParams().get("mx_addr");
		StringTokenizer tokenizer = new StringTokenizer(mxAddr, ":");
		multiplexerAddress = tokenizer.nextToken();
		multiplexerPort = Integer.parseInt(tokenizer.nextToken());

		return true;
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

}
