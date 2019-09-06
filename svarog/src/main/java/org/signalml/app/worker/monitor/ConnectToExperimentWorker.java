package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.awt.Container;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.GetExperimentContactRequest;
import org.signalml.app.worker.monitor.messages.GetExperimentContactResponse;
import org.signalml.app.worker.monitor.messages.JoinExperimentRequest;
import org.signalml.app.worker.monitor.messages.LauncherMessage;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestErrorResponse;
import org.signalml.app.worker.monitor.messages.RequestOKResponse;
import org.signalml.app.worker.monitor.messages.StartEEGSignalRequest;
import org.signalml.app.worker.monitor.messages.StartEEGSignalResponse;
import org.signalml.peer.BrokerInfo;
import org.signalml.peer.BrokerTcpConnector;
import org.signalml.peer.Peer;

public class ConnectToExperimentWorker extends SwingWorkerWithBusyDialog<Void, Void> {

	public static final int TIMEOUT_MILIS = 500;
	public static final int TRYOUT_COUNT = 20;
        public static final int EXPERIMENT_START_TIMEOUT_MILIS = 10000;
        
        private Container parentContainer;
	private static Logger logger = Logger.getLogger(ConnectToExperimentWorker.class);
	private ExperimentDescriptor experimentDescriptor;

	private String multiplexerAddress;
	private int multiplexerPort;

	private InetSocketAddress multiplexerSocket;
	

	public ConnectToExperimentWorker(Container parentContainer, ExperimentDescriptor experimentDescriptor) {
		super(parentContainer);
                this.parentContainer = parentContainer;
		this.experimentDescriptor = experimentDescriptor;
		getBusyDialog().setText(_("Connecting to the experiment"));
		getBusyDialog().setCancellable(false);
	}

	public ExperimentDescriptor getExperimentDescriptor() {
		return experimentDescriptor;
	}

	@Override
	protected Void doInBackground() throws Exception {

		showBusyDialog();
		if (experimentDescriptor.getStatus() == ExperimentStatus.NEW) {
			startNewExperiment();
		}

		sendJoinExperimentRequest();
		connectToMultiplexer();

		experimentDescriptor.setConnected(true);

		return null;
	}

	protected void startNewExperiment() throws OpenbciCommunicationException {
		StartEEGSignalRequest request = new StartEEGSignalRequest(experimentDescriptor);
		
                //After getting Start EEG obci server launches experiment and after it is
                // launched (or launch failed) it sends needed data
                // It can take few secodsn, this worker is not cancelable, so we can
                // just extend the timout on Pull socket.
		ObciPullSocket pullSocket = new ObciPullSocket(EXPERIMENT_START_TIMEOUT_MILIS);
		request.setClientPushAddress(pullSocket.getAddressLocal());

		Helper.sendRequestAndParseResponse(request, Helper.getOpenBCIIpAddress(), Helper.getOpenbciPort(), MessageType.REQUEST_OK_RESPONSE);
		
		StartEEGSignalResponse response = (StartEEGSignalResponse)pullSocket.getAndParsePushPullResponse(MessageType.START_EEG_SIGNAL_RESPONSE);
		pullSocket.close();
		String exp_id = response.getSender();
		logger.debug("GOT expereiment ID "+exp_id);
		experimentDescriptor.setId(exp_id);

		getExperimentContact();
	}

	protected void getExperimentContact() throws OpenbciCommunicationException {
		GetExperimentContactRequest request = new GetExperimentContactRequest(experimentDescriptor.getId());

		GetExperimentContactResponse response = (GetExperimentContactResponse) Helper.sendRequestAndParseResponse(
				request,
				Helper.getOpenBCIIpAddress(),
				Helper.getOpenbciPort(),
				MessageType.GET_EXPERIMENT_CONTACT_RESPONSE);

		experimentDescriptor.setExperimentRepUrls(response.getRepAddress());
	}

	protected void sendJoinExperimentRequest() throws OpenbciCommunicationException {
		JoinExperimentRequest request = new JoinExperimentRequest(experimentDescriptor);
		RequestOKResponse response = null;
		MessageType responseType = null;

		for (int i = 0; i < TRYOUT_COUNT; i++) {

			try{
				response = (RequestOKResponse)Helper.sendRequestAndParseResponse(request,
						experimentDescriptor.getFirstRepHost(),
						experimentDescriptor.getFirstRepPort(),
						MessageType.REQUEST_OK_RESPONSE);
				break;
			}
			catch (OpenbciCommunicationException ex){
				logger.warn("Error while connecting to experiment, retrying");
				
				try {
					Thread.sleep(TIMEOUT_MILIS);
				} catch (InterruptedException e) {
					logger.error("", e);
				}
			}
		}

		if (response==null){
			throw new OpenbciCommunicationException("Couldn't connect to experiment");
		}
		String mxAddr = (String) response.getParams().get("mx_addr");
		StringTokenizer tokenizer = new StringTokenizer(mxAddr, ":");
		multiplexerAddress = tokenizer.nextToken();
		multiplexerPort = Integer.parseInt(tokenizer.nextToken());
		boolean hasVideoSaver = response.getParams().containsKey("has_video_saver") && (Boolean) response.getParams().get("has_video_saver");
		experimentDescriptor.setHasVideoSaver(hasVideoSaver);
	}

	protected void connectToMultiplexer() {
		logger.debug("Connecting to OBCI");
		BrokerTcpConnector connector = new BrokerTcpConnector(multiplexerAddress, multiplexerPort);
		try {
			BrokerInfo brokerInfo = connector.fetchBrokerInfo();
			Peer peer = new Peer(experimentDescriptor.getPeerId(), brokerInfo);
			peer.connect();
			experimentDescriptor.setPeer(peer);
		} catch (Exception ex) {
			logger.error("Connection to JMX failed", ex);
		}
	}

	@Override
	protected void done() {
		super.done();

		boolean shouldDisconnect = false;

		try {
			get();
		} catch (CancellationException e) {
			shouldDisconnect = true;
			logger.debug("Connecting to experiment cancelled");
		} catch (InterruptedException e) {
			shouldDisconnect = true;
			logger.error("", e);
		} catch (ExecutionException e) {
			shouldDisconnect = true;
			if (e.getCause() instanceof OpenbciCommunicationException) {
				OpenbciCommunicationException exception = (OpenbciCommunicationException) e.getCause();
				exception.showErrorDialog(_("An error occurred while connecting to experiment"));
			}
			else {
				logger.error("", e);
			}
		}

		if (shouldDisconnect) {
			DisconnectFromExperimentWorker worker = new DisconnectFromExperimentWorker(this.parentContainer, experimentDescriptor);
			worker.execute();
		}
	}

}
