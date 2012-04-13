package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.awt.Container;
import java.net.ConnectException;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.List;

import javax.swing.text.NumberFormatter;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.app.worker.monitor.messages.FindEEGExperimentsRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.parsing.ExperimentDescriptorJSonReader;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.signalml.util.FormatUtils;

public class GetOpenBCIExperimentsWorker extends SwingWorkerWithBusyDialog<List<ExperimentDescriptor>, Void> {

	public GetOpenBCIExperimentsWorker(Container parent) {
		super(parent);
	}

	@Override
	protected List<ExperimentDescriptor> doInBackground() throws Exception {

		showBusyDialog();

		FindEEGExperimentsRequest request = new FindEEGExperimentsRequest();
		String response;

		String openbciIpAddress = Helper.getOpenBCIIpAddress();
		int openbciPort = Helper.getOpenbciPort();
		try {
			response = Helper.sendRequest(request, openbciIpAddress, openbciPort);
		} catch (ConnectException ex) {
			String openbciPortFormatted = FormatUtils.formatNoGrouping(openbciPort);
			String errorMsg = _R("OpenBCI daemon at {0}:{1} is not running! Please check if the IP address is correct.", openbciIpAddress, openbciPortFormatted);
			Dialogs.showError(errorMsg);
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

}
