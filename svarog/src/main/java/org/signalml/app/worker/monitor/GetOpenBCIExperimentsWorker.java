package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.SwingWorker;

import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.util.NetworkUtils;
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
		} catch (Exception e) {
			ErrorsDialog.showError(e.toString());
			return null;
		}
		
		if (myAddress == null)
			ErrorsDialog.showError(_("Could not find my IP address!"));
		
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

	protected String getPullAddress() throws Exception {
		int port = NetworkUtils.getFreePortNumber();

		InetAddress inetAddress = getMyIPAddress();
		if (inetAddress == null)
			return null;

		return Helper.getAddressString(inetAddress.getHostAddress(), port);
	}

	protected InetAddress getMyIPAddress() throws UnknownHostException, SocketException {
		String openbciServerAddressString = SvarogApplication.getApplicationConfiguration().getOpenBCIDaemonAddress();
		InetAddress openBCIServerAddress = InetAddress.getByName(openbciServerAddressString);

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		for (NetworkInterface networkInterface : Collections.list(networkInterfaces)) {
			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
				int prefixLength = interfaceAddress.getNetworkPrefixLength();
				InetAddress address = interfaceAddress.getAddress();

				if (NetworkUtils.isAddressIPv4(address)) {
					if (NetworkUtils.areAddressesInTheSameSubnet(address, openBCIServerAddress, prefixLength))
						return address;
				}

			}
		}
		return null;
	}

}
