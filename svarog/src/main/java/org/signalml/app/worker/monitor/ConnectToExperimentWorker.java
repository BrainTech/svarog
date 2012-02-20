package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.JmxClient;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.messages.JoinExperimentRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestOKResponse;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.signalml.multiplexer.protocol.SvarogConstants;

public class ConnectToExperimentWorker extends SwingWorker<JmxClient, Void> {

	public static final int TIMEOUT_MILIS = 500;
	public static final int TRYOUT_COUNT = 10;

	private ExperimentDescriptor experimentDescriptor;
	
	private String multiplexerAddress;
	private int multiplexerPort;

	private InetSocketAddress multiplexerSocket;

	public ConnectToExperimentWorker(ExperimentDescriptor experimentDescriptor) {
		this.experimentDescriptor = experimentDescriptor;
	}
	
	@Override
	protected JmxClient doInBackground() throws Exception {
		if (sendJoinExperimentRequest())
			return connectToMultiplexer();
		return null;
	}
	
	protected boolean sendJoinExperimentRequest() throws JsonParseException, JsonProcessingException, IOException { 
		JoinExperimentRequest request = new JoinExperimentRequest(experimentDescriptor);

		String responseString = Helper.sendRequest(request, experimentDescriptor.getExperimentAddress());

		if (responseString == null) {
			Dialogs.showError(_("Experiment is not responding!"));
			return false;
		}

		if (!MessageParser.checkIfResponseIsOK(responseString, MessageType.REQUEST_OK_RESPONSE)) {
			return false;
		}
		
		RequestOKResponse response = (RequestOKResponse) MessageParser.parseMessageFromJSON(responseString, MessageType.REQUEST_OK_RESPONSE);
		
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
