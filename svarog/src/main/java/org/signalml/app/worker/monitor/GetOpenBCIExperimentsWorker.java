package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.SwingWorker;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.components.dialogs.ErrorsDialog;
import org.signalml.app.worker.monitor.messages.FindEEGExperimentsRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.parsing.ExperimentDescriptorJSonReader;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class GetOpenBCIExperimentsWorker extends SwingWorker<List<ExperimentDescriptor>, Void>{

	@Override
	protected List<ExperimentDescriptor> doInBackground() throws Exception {

		if (!Helper.isObciServerResponding()) {
			return null; 
		}
		
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket socketPull = context.socket(ZMQ.PULL);
		ZMQ.Poller poller = context.poller();
		poller.register(socketPull, Poller.POLLIN);

		String myAddress;
		try {
			myAddress = getPullAddress();
		} catch (IOException e) {
			ErrorsDialog.showError(_("Could not read ~/.obci/main_config.ini file!"));
			return null;
		}
		
		socketPull.bind(myAddress);
		FindEEGExperimentsRequest request = new FindEEGExperimentsRequest(myAddress);

		ZMQ.Socket socketSend = context.socket(ZMQ.REQ);
		socketSend.connect(Helper.getObciServerAddressString());
		socketSend.send(request.toJSON().getBytes(), 0);
		
		System.out.println("sent: " + request.toJSON());
		socketSend.close();
		
		String response;
		if (poller.poll(Helper.getTimeOutLength()) > 0) {
			byte[] responseBytes = socketPull.recv(0);
			response = new String(responseBytes);
		}
		else {
			ErrorsDialog.showError(_("OpenBCI server is not responding!"));
			return null;
		}
		socketSend.close();
		socketPull.close();

		if (MessageParser.checkIfResponseIsOK(response, MessageType.EEG_EXPERIMENTS_RESPONSE)) {
			ExperimentDescriptorJSonReader reader = new ExperimentDescriptorJSonReader();
			List<ExperimentDescriptor> result = reader.parseExperiments(response);
			return result;
		}
		else
			return null;
	}

 	protected String getPullAddress() throws IOException {
 		ServerSocket server = new ServerSocket(0);
 		int port = server.getLocalPort();
 		server.close();
 		
 		String localAddress = GetOpenBCIExperimentsWorker.getMyIPAddress();
 		String pullAddress = localAddress + ":" + port;
 		return pullAddress;
 	}
	
	public static String getMyIPAddress() throws InvalidFileFormatException, IOException {
		File homeDir = new File(System.getProperty("user.home"));
		File obciSettingsDir = new File(homeDir, ".obci");
		
		File mainConfigFile = new File(obciSettingsDir, "main_config.ini");
		Wini mainConfig = new Wini(mainConfigFile);

		String ifname = mainConfig.get("server", "ifname");
		
		if (ifname == null)
			return null;

		String address = "";

		try {
			NetworkInterface networkInterface = NetworkInterface.getByName(ifname);
			Enumeration<InetAddress> networkAddressess = networkInterface.getInetAddresses();
			// find ipv4 address
			for (InetAddress inetAddress : Collections.list(networkAddressess)) {
				String test = inetAddress.toString();

				if (test.split(":").length == 1) {
					address = "tcp:/" + test;
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return address;
	}

}
