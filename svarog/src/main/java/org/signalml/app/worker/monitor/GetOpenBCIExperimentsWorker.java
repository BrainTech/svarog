package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Container;
import java.net.SocketException;
import java.util.List;

import javax.swing.SwingWorker;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.util.NetworkUtils;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.app.worker.monitor.messages.FindEEGExperimentsRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.parsing.ExperimentDescriptorJSonReader;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;

public class GetOpenBCIExperimentsWorker extends SwingWorkerWithBusyDialog<List<ExperimentDescriptor>, Void>{

	public GetOpenBCIExperimentsWorker(Container parent) {
		super(parent);
	}

	@Override
	protected List<ExperimentDescriptor> doInBackground() throws Exception {

		showBusyDialog();

		try {
			if (!Helper.wasOpenbciConfigFileLoaded())
				Helper.loadOpenbciConfigFile();
		} catch (Exception ex) {
			Dialogs.showError("Could not read ~/.obci/main_config.ini file correctly");
			return null;
		}
		
		try {
			Helper.findOpenbciIpAddress();
		} catch (SocketException ex) {
			Dialogs.showExceptionDialog(ex);
			return null;
		}
		
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
			Dialogs.showError(e.toString());
			return null;
		}
		
		if (myAddress == null)
			Dialogs.showError(_("Could not find my IP address!"));
		
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
			System.out.println("response: " + response);
		}
		else {
			Dialogs.showError(_("OpenBCI server is not responding!"));
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
		return Helper.getAddressString(Helper.getOpenbciIpAddress(), port);
	}

}
