package org.signalml.app.worker.monitor;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.JmxClient;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.worker.monitor.zeromq.JoinExperimentsRequest;
import org.signalml.multiplexer.protocol.SvarogConstants;
import org.zeromq.ZMQ;

public class ConnectToExperimentWorker extends SwingWorker<JmxClient, Void> {

	public static final int TIMEOUT_MILIS = 500;
	public static final int TRYOUT_COUNT = 10;

	private ExperimentDescriptor experimentDescriptor;

	private InetSocketAddress multiplexerSocket;
	private JmxClient client;

	public ConnectToExperimentWorker(ExperimentDescriptor experimentDescriptor) {
		this.experimentDescriptor = experimentDescriptor;
	}
	
	@Override
	protected JmxClient doInBackground() throws Exception {

		//wysłanie join_experiment do eksperymentu
		JoinExperimentsRequest request = new JoinExperimentsRequest(experimentDescriptor);
		String requestString = request.toJSON().toString();

		String experimentAddress = experimentDescriptor.getExperimentAddress();

		System.out.println("msg sent to: " + experimentAddress + " data: " + requestString);
		
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket socket = context.socket(ZMQ.REQ);
		socket.connect(experimentAddress);
		
		socket.send(requestString.getBytes(), 0);
		byte[] responseBytes = socket.recv(0);
		String response = new String(responseBytes);
		
		ObjectMapper mapper = new ObjectMapper();
		LinkedHashMap<String, Object> list = mapper.readValue(response.getBytes(), new TypeReference<LinkedHashMap<String, Object>>() {});
		
		list = (LinkedHashMap<String, Object>) list.get("params");
		String mxAddr = (String) list.get("mx_addr");
		
		StringTokenizer tokenizer = new StringTokenizer(mxAddr, ":");
		
		String multiplexerAddress = tokenizer.nextToken();
		int multiplexerPort = Integer.parseInt(tokenizer.nextToken());
		
		System.out.println("Got response: " + response);
		
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
