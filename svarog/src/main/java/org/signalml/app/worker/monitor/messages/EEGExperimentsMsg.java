/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor.messages;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;

/**
 *
 * @author marian
 */
public class EEGExperimentsMsg extends AbstractEEGExperimentsMsg{
	@JsonProperty("experiment_list")
	private List<LinkedHashMap<String, Object>> experimentList;
	
	@JsonIgnore
	@Override
	protected List<LinkedHashMap<String, Object>> getExperimentsList()
	{
		return experimentList;
	}

	@JsonIgnore
	@Override
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

		experiment.setExperimentRepUrls((List<String>) map.get("rep_addrs"));
		return experiment;
	}
		
}
