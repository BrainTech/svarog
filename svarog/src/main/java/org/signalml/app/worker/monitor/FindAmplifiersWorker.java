package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.AmplifierType;
import org.signalml.app.worker.monitor.messages.FindEEGAmplifiersRequest;
import org.signalml.app.worker.monitor.messages.MessageType;


public class FindAmplifiersWorker extends FindEEGExperimentsWorker{

	protected void mainWork() throws OpenbciCommunicationException {
		
		for (AmplifierType amplifierType: AmplifierType.values()) {
			if (isCancelled())
				break;

			getNewExperiments(amplifierType);
		}
		
	}

	protected void getNewExperiments(AmplifierType amplifierType) {
		FindEEGAmplifiersRequest findEEGAmplifiersRequest = new FindEEGAmplifiersRequest(amplifierType);
		log(_R("Requesting the list of available {0} amplifiers...", amplifierType));
		getExperiments(findEEGAmplifiersRequest, amplifierType, MessageType.EEG_AMPLIFIERS_RESPONSE);
	}
}
	
