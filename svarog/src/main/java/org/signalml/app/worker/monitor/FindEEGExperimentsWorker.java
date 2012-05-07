package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.util.List;

import javax.swing.SwingWorker;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.worker.monitor.messages.AmplifierType;
import org.signalml.app.worker.monitor.messages.FindEEGAmplifiersRequest;
import org.signalml.app.worker.monitor.messages.FindEEGExperimentsRequest;
import org.signalml.app.worker.monitor.messages.Message;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.parsing.AbstractResponseJSonReader;
import org.signalml.app.worker.monitor.messages.parsing.FindEEGAmplifiersResponseJSonReader;
import org.signalml.app.worker.monitor.messages.parsing.FindEEGExperimentsResponseJSonReader;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;

public class FindEEGExperimentsWorker extends SwingWorker<Void, List<ExperimentDescriptor>> {

	public static String WORKER_LOG_APPENDED_PROPERTY = "workerLoggerProperty";
	public static String NEW_EXPERIMENTS_RECEIVED = "newExperimentsReceived";

	private String openbciIpAddress;
	private int openbciPort;

	public FindEEGExperimentsWorker() {
	}

	@Override
	protected void done() {
		if (isCancelled()) {
			Helper.cancelReceiving();
		}
		super.done();
	}

	@Override
	protected Void doInBackground() throws Exception {

		openbciIpAddress = Helper.getOpenBCIIpAddress();
		openbciPort = Helper.getOpenbciPort();

		boolean ok = getRunningExperiments() != null;

		if (ok) {
			for (AmplifierType amplifierType: AmplifierType.values()) {
				if (isCancelled())
					break;
				if (getNewExperiments(amplifierType) == null) {
					ok = false;
					break;
				}
			}
		}

		if (isCancelled())
			log(_("Refreshing cancelled."));
		else if (!ok)
			log(_("There was an error while refreshing the list of experiments."));
		else
			log(_("Refreshing successfully accomplished!"));

		return null;
	}

	protected List<ExperimentDescriptor> getRunningExperiments() {
		FindEEGExperimentsRequest findEEGExperimentsRequest = new FindEEGExperimentsRequest();
		FindEEGExperimentsResponseJSonReader reader = new FindEEGExperimentsResponseJSonReader();
		log(_("Requesting the list of running experiments..."));

		return getExperiments(findEEGExperimentsRequest, MessageType.EEG_EXPERIMENTS_RESPONSE, reader);
	}

	protected List<ExperimentDescriptor> getNewExperiments(AmplifierType amplifierType) {
		FindEEGAmplifiersRequest findEEGAmplifiersRequest = new FindEEGAmplifiersRequest(amplifierType);
		FindEEGAmplifiersResponseJSonReader reader = new FindEEGAmplifiersResponseJSonReader();
		log(_R("Requesting the list of available {0} amplifiers...", amplifierType));

		return getExperiments(findEEGAmplifiersRequest, MessageType.EEG_AMPLIFIERS_RESPONSE, reader);
	}

	protected List<ExperimentDescriptor> getExperiments(Message request, MessageType messageType, AbstractResponseJSonReader responseReader) {
		String response = Helper.sendRequestAndHandleExceptions(request, openbciIpAddress, openbciPort, Helper.INFINITE_TIMEOUT);

		if (response != null && MessageParser.checkIfResponseIsOK(response, messageType)) {
			List<ExperimentDescriptor> result = responseReader.parseExperiments(response);
			publish(result);

			String readerLog = responseReader.getLog();
			if (readerLog.isEmpty())
				logln(_("OK"));
			else
				log(readerLog);

			return result;
		}
		else {
			if (isCancelled())
				logln(_("CANCELLED"));
			else
				logln(_("ERROR"));
			return null;
		}
	}

	protected void log(String message) {
		this.firePropertyChange(WORKER_LOG_APPENDED_PROPERTY, null, message);
	}

	protected void logln(String message) {
		log(message + "\n");
	}

	@Override
	protected void process(List<List<ExperimentDescriptor>> chunks) {
		for (List<ExperimentDescriptor> experiments: chunks) {
			firePropertyChange(NEW_EXPERIMENTS_RECEIVED, null, experiments);
		}
	}

}
