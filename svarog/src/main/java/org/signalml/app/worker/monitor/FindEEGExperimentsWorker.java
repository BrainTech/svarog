package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.awt.Container;
import java.net.ConnectException;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.swing.text.NumberFormatter;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.app.worker.monitor.messages.FindEEGAmplifiersRequest;
import org.signalml.app.worker.monitor.messages.FindEEGExperimentsRequest;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.parsing.FindEEGAmplifiersResponseJSonReader;
import org.signalml.app.worker.monitor.messages.parsing.FindEEGExperimentsResponseJSonReader;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.signalml.util.FormatUtils;

public class FindEEGExperimentsWorker extends SwingWorkerWithBusyDialog<List<ExperimentDescriptor>, Void> {

	private String openbciIpAddress;
	private int openbciPort;
	private List<ExperimentDescriptor> result = new ArrayList<ExperimentDescriptor>();

	public FindEEGExperimentsWorker(Container parent) {
		super(parent);
	}

	@Override
	protected List<ExperimentDescriptor> doInBackground() throws Exception {

		showBusyDialog();

		String openbciIpAddress = Helper.getOpenBCIIpAddress();
		int openbciPort = Helper.getOpenbciPort();

		//eeg experiments
		FindEEGExperimentsRequest findEEGExperimentsRequest = new FindEEGExperimentsRequest();
		String response;

		try {
			response = Helper.sendRequest(findEEGExperimentsRequest, openbciIpAddress, openbciPort);
		} catch (ConnectException ex) {
			String openbciPortFormatted = FormatUtils.formatNoGrouping(openbciPort);
			String errorMsg = _R("OpenBCI daemon at {0}:{1} is not running! Please check if the IP address is correct.", openbciIpAddress, openbciPortFormatted);
			Dialogs.showError(errorMsg);
			return null;
		}

		if (MessageParser.checkIfResponseIsOK(response, MessageType.EEG_EXPERIMENTS_RESPONSE)) {
			FindEEGExperimentsResponseJSonReader reader = new FindEEGExperimentsResponseJSonReader();
			result.addAll(reader.parseExperiments(response));
		}
		else
			return null;

		//eeg amplifiers
		FindEEGAmplifiersRequest findEEGAmplifiersRequest = new FindEEGAmplifiersRequest();
		try {
			response = Helper.sendRequest(findEEGAmplifiersRequest, openbciIpAddress, openbciPort);
		} catch (ConnectException ex) {
			String openbciPortFormatted = FormatUtils.formatNoGrouping(openbciPort);
			String errorMsg = _R("OpenBCI daemon at {0}:{1} is not running! Please check if the IP address is correct.", openbciIpAddress, openbciPortFormatted);
			Dialogs.showError(errorMsg);
			return null;
		}

		if (MessageParser.checkIfResponseIsOK(response, MessageType.EEG_AMPLIFIERS_RESPONSE)) {
			FindEEGAmplifiersResponseJSonReader reader = new FindEEGAmplifiersResponseJSonReader();
			result.addAll(reader.parseExperiments(response));
		}
		else
			return null;

		return result;
	}

}
