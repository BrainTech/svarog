package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
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
	protected Void doInBackground() throws OpenbciCommunicationException {

		openbciIpAddress = Helper.getOpenBCIIpAddress();
		openbciPort = Helper.getOpenbciPort();

		getRunningExperiments();

		for (AmplifierType amplifierType: AmplifierType.values()) {
			if (isCancelled())
				break;

			getNewExperiments(amplifierType);
		}

		if (!isCancelled())
			log(_("Refreshing successfully accomplished!"));

		return null;
	}

	protected void getRunningExperiments() throws OpenbciCommunicationException {
		FindEEGExperimentsRequest findEEGExperimentsRequest = new FindEEGExperimentsRequest();
		FindEEGExperimentsResponseJSonReader reader = new FindEEGExperimentsResponseJSonReader();
		log(_("Requesting the list of running experiments..."));

		getExperiments(findEEGExperimentsRequest, MessageType.EEG_EXPERIMENTS_RESPONSE, reader);
	}

	protected void getNewExperiments(AmplifierType amplifierType) throws OpenbciCommunicationException {
		FindEEGAmplifiersRequest findEEGAmplifiersRequest = new FindEEGAmplifiersRequest(amplifierType);
		FindEEGAmplifiersResponseJSonReader reader = new FindEEGAmplifiersResponseJSonReader();
		log(_R("Requesting the list of available {0} amplifiers...", amplifierType));

		getExperiments(findEEGAmplifiersRequest, MessageType.EEG_AMPLIFIERS_RESPONSE, reader);
	}

	protected void getExperiments(Message request, MessageType messageType, AbstractResponseJSonReader responseReader) throws OpenbciCommunicationException {
		String response = Helper.sendRequest(request, openbciIpAddress, openbciPort, Helper.INFINITE_TIMEOUT);

		MessageParser.checkIfResponseIsOK(response, messageType);

		List<ExperimentDescriptor> result = responseReader.parseExperiments(response);
		publish(result);

		String readerLog = responseReader.getLog();
		if (isCancelled())
			logln(_("CANCELLED"));
		else if (readerLog.isEmpty())
			logln(_("OK"));
		else
			log(readerLog);
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

	@Override
	protected void done() {
		if (isCancelled()) {
			Helper.cancelReceiving();
		}
		super.done();

		try {
			get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof OpenbciCommunicationException) {
				OpenbciCommunicationException exception = (OpenbciCommunicationException) e.getCause();
				exception.showErrorDialog(_("An error occurred while refreshing experiments"));
			}
			else {
				e.printStackTrace();
			}
			log(_("ERROR"));
		} catch (CancellationException e) {
			//it's OK, this is thrown when user cancels the execution of this worker.
		}
	}

}
