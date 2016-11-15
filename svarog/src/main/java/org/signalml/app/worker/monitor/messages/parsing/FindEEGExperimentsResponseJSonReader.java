package org.signalml.app.worker.monitor.messages.parsing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;

public class FindEEGExperimentsResponseJSonReader extends AbstractResponseJSonReader {

	protected static final Logger logger = Logger.getLogger(FindEEGExperimentsResponseJSonReader.class);

	@Override
	protected String getExperimentsListFieldName() {
		return "experiment_list";
	}

	public ExperimentDescriptor parseSingleExperiment(LinkedHashMap<String, Object> map) {
		ExperimentDescriptor experiment = new ExperimentDescriptor();

		//experiment info
		LinkedHashMap<String, Object> experimentInfo = (LinkedHashMap<String, Object>) map.get("experiment_info");

		experiment.setName((String) experimentInfo.get("name"));
		experiment.setId((String) experimentInfo.get("uuid"));

		LinkedHashMap<String, Object> experimentStatus = (LinkedHashMap<String, Object>) experimentInfo.get("experiment_status");
		String statusName = (String) experimentStatus.get("status_name");
		experiment.setStatus(ExperimentStatus.valueOf(statusName.toUpperCase()));

		//amplifier_peer_info
		LinkedHashMap<String, Object> amplifierPeerInfo = (LinkedHashMap<String, Object>) map.get("amplifier_peer_info");
		String path = (String) amplifierPeerInfo.get("path");
		experiment.setPath(path);

		//amplifier
		LinkedHashMap<String, Object> amplifierParams = (LinkedHashMap<String, Object>) map.get("amplifier_params");
		Amplifier amplifier = experiment.getAmplifier();
		amplifier.setName((String) amplifierParams.get("amplifier_name"));
		amplifier.setSamplesPerPacket(Integer.parseInt((String) amplifierParams.get("samples_per_packet")));

		readSamplingFrequencies(amplifierParams, experiment);

		experiment.getSignalParameters().setSamplingFrequency(new Float((String) amplifierParams.get("sampling_rate")));

		List<Object> channelsInfo = (List<Object>) amplifierParams.get("channels_info");
		readChannelsList(channelsInfo, experiment);

		//channel names
		String channelNames = (String) amplifierParams.get("channel_names");
		StringTokenizer tokenizer = new StringTokenizer(channelNames, ";");
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			String channelName = tokenizer.nextToken();
			AmplifierChannel channel = amplifier.getChannels().get(i);
			channel.setSelected(true);
			channel.setLabel(channelName);
			i++;
		}

		List<Object> tcpAddress = (List<Object>) ((List<Object>) map.get("tcp_addrs")).get(0);
		experiment.setExperimentIPAddress((String) tcpAddress.get(0));
		experiment.setExperimentPort((Integer) tcpAddress.get(1));

		return experiment;
	}

}
