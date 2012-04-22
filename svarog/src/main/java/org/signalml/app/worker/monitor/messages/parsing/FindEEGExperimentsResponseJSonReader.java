package org.signalml.app.worker.monitor.messages.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;

public class FindEEGExperimentsResponseJSonReader {

	protected static final Logger logger = Logger.getLogger(FindEEGExperimentsResponseJSonReader.class);

	public List<ExperimentDescriptor> parseExperiments(String s) {

		ObjectMapper mapper = new ObjectMapper();

		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			map = mapper.readValue(s.getBytes(), new TypeReference<LinkedHashMap<String, Object>>() {});
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) map.get("experiment_list");

		List<ExperimentDescriptor> experiments = new ArrayList<ExperimentDescriptor>();
		for (LinkedHashMap<String, Object> exp: list) {
			try {
				ExperimentDescriptor descriptor = parseSingleExperiment(exp);
				descriptor.setCorrectlyRead(true);
				experiments.add(descriptor);
			} catch (Exception e) {
				logger.error("There was an error parsing an experiment: " + e.getCause());
			}
		}

		return experiments;
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
		Amplifier amplifier = new Amplifier();
		amplifier.setName((String) amplifierParams.get("amplifier_name"));
		amplifier.setAmplifierNull(Double.parseDouble((String) amplifierParams.get("amplifier_null")));
		amplifier.setSamplesPerPacket(Integer.parseInt((String) amplifierParams.get("samples_per_packet")));

		List<Integer> samplingFrequencies = (List<Integer>) amplifierParams.get("sampling_rates");
		amplifier.setSamplingFrequencies(new ArrayList<Float>());

		for (Integer sf: samplingFrequencies) {
			amplifier.getSamplingFrequencies().add(new Float(sf));
		}

		SignalParameters signalParameters = new SignalParameters();
		experiment.setSignalParameters(signalParameters);

		signalParameters.setSamplingFrequency(new Float((String) amplifierParams.get("sampling_rate")));

		List<Object> channelsInfo = (List<Object>) amplifierParams.get("channels_info");
		amplifier.setChannels(new ArrayList<AmplifierChannel>());

		int i = 0;
		for (Object item: channelsInfo) {
			LinkedHashMap<String, Object> channelInfo = (LinkedHashMap<String, Object>) item;
			String channelName = (String) channelInfo.get("name");
			AmplifierChannel channel = new AmplifierChannel(i+1, channelName);
			channel.setOriginalName(channelName);
			double gain = (Double) channelInfo.get("gain");
			double offset = (Double) channelInfo.get("offset");
			channel.setCalibrationGain((float)gain);
			channel.setCalibrationOffset((float)offset);
			channel.setSelected(false);
			amplifier.getChannels().add(channel);
			i++;
		}

		signalParameters.setChannelCount(i);

		//active channels
		String activeChannels = (String) amplifierParams.get("active_channels");

		StringTokenizer tokenizer = new StringTokenizer(activeChannels, ";");
		while (tokenizer.hasMoreTokens()) {
			String channelName = tokenizer.nextToken();

			try {
				int channelNumber = Integer.parseInt(channelName);
				amplifier.getChannels().get(channelNumber).setSelected(true);
			}
			catch (NumberFormatException ex) {
				for (AmplifierChannel channel: amplifier.getChannels()) {
					if (channel.getLabel().equalsIgnoreCase(channelName))
						channel.setSelected(true);
				}
			}
		}

		//channel names
		String channelNames = (String) amplifierParams.get("channel_names");
		tokenizer = new StringTokenizer(channelNames, ";");
		List<AmplifierChannel> selectedChannels = amplifier.getSelectedChannels();
		i = 0;
		while (tokenizer.hasMoreTokens()) {
			String channelName = tokenizer.nextToken();
			selectedChannels.get(i).setLabel(channelName);
			i++;
		}

		experiment.setAmplifier(amplifier);

		List<Object> tcpAddress = (List<Object>) map.get("tcp_addr");
		experiment.setExperimentIPAddress((String) tcpAddress.get(0));
		experiment.setExperimentPort((Integer) tcpAddress.get(1));

		return experiment;
	}

}
