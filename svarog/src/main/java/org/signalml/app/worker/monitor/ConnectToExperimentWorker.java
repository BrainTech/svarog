package org.signalml.app.worker.monitor;

import java.net.InetSocketAddress;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.JmxClient;

import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.multiplexer.protocol.SvarogConstants;

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

		// TODO - zapytanie openBCI o adres eksperymentu
		// z experimentDescriptor pobrane są i wysłane
		//   - id eksperymentu
		//   - numery kanałów
		//   - nazwy kanałów
		//   - częstotl. próbkowania
		//
		
		String multiplexerAddress = "127.0.0.1";
		int multiplexerPort = 88898;
		
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
