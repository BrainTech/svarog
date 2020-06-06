package org.signalml.app.worker.monitor.messages;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class FindEEGAmplifiersRequest extends LongRequest {

	@JsonProperty("amplifier_types")
	private List<AmplifierType> amplifierTypes = new ArrayList<AmplifierType>();

	public FindEEGAmplifiersRequest() {
		super(MessageType.FIND_EEG_AMPLIFIERS_REQUEST);

		amplifierTypes.add(AmplifierType.USB);
		amplifierTypes.add(AmplifierType.BLUETOOTH);
		amplifierTypes.add(AmplifierType.VIRTUAL);
	}

	public FindEEGAmplifiersRequest(AmplifierType amplifierType) {
		super(MessageType.FIND_EEG_AMPLIFIERS_REQUEST);
		amplifierTypes.add(amplifierType);
	}
}
