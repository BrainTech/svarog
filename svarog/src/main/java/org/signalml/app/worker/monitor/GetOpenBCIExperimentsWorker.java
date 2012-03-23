package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Container;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.List;

import javax.swing.SwingWorker;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.util.NetworkUtils;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.app.worker.monitor.messages.FindEEGExperimentsRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.Netstring;
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
			if (!TCPHelper.wasOpenbciConfigFileLoaded())
				TCPHelper.loadOpenbciConfigFile();
		} catch (Exception ex) {
			Dialogs.showError("Could not read ~/.obci/main_config.ini file correctly");
			return null;
		}
		
		try {
			TCPHelper.findOpenbciIpAddress();
		} catch (SocketException ex) {
			Dialogs.showExceptionDialog(ex);
			return null;
		}

		FindEEGExperimentsRequest request = new FindEEGExperimentsRequest();
		String response;

		try {
			response = TCPHelper.sendRequest(request, TCPHelper.getOpenBCIIpAddress(), TCPHelper.getOpenbciPort());
		} catch (ConnectException ex) {
			Dialogs.showError(_("OpenBCI server is not running!"));
			return null;
		}

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
