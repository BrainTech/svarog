package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.IOException;
import java.net.ServerSocket;
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

		String myAddress = getPullAddress();
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
		
		//tymczasowo nasłuchujemy na interfejsie lo
		String pullAddress = Helper.getAddressString("127.0.0.1", port); 
		return pullAddress;
	}

}
