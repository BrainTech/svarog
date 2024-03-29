package org.signalml.app.worker.monitor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.AbstractEEGExperimentsMsg;
import org.signalml.app.worker.monitor.messages.AmplifierType;
import org.signalml.app.worker.monitor.messages.FindEEGExperimentsRequest;
import org.signalml.app.worker.monitor.messages.LongRequest;
import org.signalml.app.worker.monitor.messages.MessageType;

public class FindEEGExperimentsWorker extends SwingWorker<Void, List<ExperimentDescriptor>> {
	
	protected static final Logger logger = Logger.getLogger(FindEEGExperimentsWorker.class);

	public static String WORKER_LOG_APPENDED_PROPERTY = "workerLoggerProperty";
	public static String NEW_EXPERIMENTS_RECEIVED = "newExperimentsReceived";

	private String openbciIpAddress;
	private int openbciPort;
	
	private int PULL_TIMEOUT = 70; // maximum time to wait for obci answer
	private int BLUETOOTH_PULL_TIMEOUT = 60 * 5;  //~30s required for one tmsi device.
	
	public FindEEGExperimentsWorker() {
		
	}
	
	protected void mainWork() throws OpenbciCommunicationException {

		getRunningExperiments(true);
		getRunningExperiments(false);
		
	}
	
	@Override
	protected Void doInBackground() throws OpenbciCommunicationException {

		openbciIpAddress = Helper.getOpenBCIIpAddress();
		openbciPort = Helper.getOpenbciPort();

		mainWork();
		
		if (!isCancelled())
			log(_("Refreshing successfully accomplished!"));

		return null;
	}

	protected void getRunningExperiments(boolean local_exps) {
		FindEEGExperimentsRequest findEEGExperimentsRequest = new FindEEGExperimentsRequest(local_exps);
		
		String msg;
		if (local_exps)
			msg = "Requesting the list of local running experiments...";
		else
			msg = "Requesting the list of running experiments on OBCI network...";
		log(_(msg));

		getExperiments(findEEGExperimentsRequest, null, MessageType.EEG_EXPERIMENTS_RESPONSE);

	}
	
	protected void getExperiments(LongRequest request, AmplifierType amplifierType, MessageType type){
		ObciPullSocket pullsocket = new ObciPullSocket();
		try {
			
			request.setClientPushAddress(pullsocket.getAddressLocal());
			Helper.sendRequestAndParseResponse(request, openbciIpAddress, openbciPort, MessageType.REQUEST_OK_RESPONSE);
		
			LocalDateTime started = LocalDateTime.now();
			AbstractEEGExperimentsMsg result = null;
			while (result == null & Duration.between(started, LocalDateTime.now()).getSeconds() <
					(amplifierType == amplifierType.BLUETOOTH ? BLUETOOTH_PULL_TIMEOUT : PULL_TIMEOUT) & !isCancelled())
				result = (AbstractEEGExperimentsMsg) pullsocket.getAndParsePushPullResponse(type);
			if (!isCancelled() & result != null){
				List<ExperimentDescriptor> experiments = result.getExperiments();
				for (ExperimentDescriptor e : experiments) e.getAmplifier().setAmplifierType(amplifierType);
				publish(experiments);
				logln(_("OK"));
			}
		}
		catch (OpenbciCommunicationException e) {
			String msg = "Error while sending request: " + e.getMessage();
			logger.error(msg);
			logln(msg);
		}
		if (isCancelled())
			logln(_("CANCELLED"));
		pullsocket.close();

	
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
			logger.error("", e);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof OpenbciCommunicationException) {
				OpenbciCommunicationException exception = (OpenbciCommunicationException) e.getCause();
				exception.showErrorDialog(_("An error occurred while refreshing experiments"));
			}
			else {
				logger.error(e.toString(), e);
			}
			log(_("ERROR"));
		} catch (CancellationException e) {
			//it's OK, this is thrown when user cancels the execution of this worker.
		}
	}

}
