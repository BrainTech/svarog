package org.signalml.app.worker.monitor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

import java.util.List;

import javax.swing.SwingWorker;

import org.signalml.ObciTester;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.document.opensignal.Amplifier;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentStatus;
import org.signalml.app.model.document.opensignal.SignalParameters;
import org.signalml.app.view.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.worker.monitor.zeromq.ExperimentDescriptorJSonReader;
import org.signalml.app.worker.monitor.zeromq.FindEEGExperimentsRequest;
import org.signalml.app.worker.monitor.zeromq.Message;
import org.signalml.app.worker.monitor.zeromq.MessageType;
import org.zeromq.ZMQ;

public class GetOpenBCIExperimentsWorker extends SwingWorker<List<ExperimentDescriptor>, Void>{

	private ApplicationConfiguration applicationConfiguration;

	public GetOpenBCIExperimentsWorker(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

	@Override
	protected List<ExperimentDescriptor> doInBackground() throws Exception {

		//TODO - tu powinno być sprawdzenie, czy openBCI daemon jest odpalony
		//jeśli nie - to odpalić
		
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket socketPull = context.socket(ZMQ.PULL);

		String myAddress = getPullAddress();
		socketPull.bind(myAddress);
		FindEEGExperimentsRequest request = new FindEEGExperimentsRequest(myAddress);

		ZMQ.Socket socketSend = context.socket(ZMQ.REQ);
		socketSend.connect(getDaemonAddressString());
		socketSend.send(request.toJSON().getBytes(), 0);
		
		System.out.println("sent: " + request.toJSON());
		socketSend.close();

		byte[] responseBytes = socketPull.recv(0);
		String response = new String(responseBytes);
		
		System.out.println("response: " + response);
			
		ExperimentDescriptorJSonReader reader = new ExperimentDescriptorJSonReader();
		List<ExperimentDescriptor> result = reader.parseExperiments(response);

		return result;
	}
	
	private String getDaemonAddressString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("tcp://");
		stringBuffer.append(applicationConfiguration.getOpenBCIDaemonAddress());
		stringBuffer.append(":");
		stringBuffer.append(applicationConfiguration.getOpenBCIDaemonPort());
		
		System.out.println("address: " + stringBuffer.toString());
		
		return stringBuffer.toString();
	}
	
	protected String getPullAddress() throws IOException {
		ServerSocket server = new ServerSocket(0);
		int port = server.getLocalPort();
		server.close();
		
		String localAddress = "127.0.0.1"; //tymczasowo nasłuchujemy na interfejsie lo
		
		String pullAddress = "tcp://" + localAddress + ":" + port; 
		return pullAddress;
	}

}
