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

public class FindEEGAmplifiersResponseJSonReader {

	protected static final Logger logger = Logger.getLogger(FindEEGAmplifiersResponseJSonReader.class);

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

		List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) map.get("amplifier_list");

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

		experiment.setStatus(ExperimentStatus.NEW);

		//experiment info
		LinkedHashMap<String, Object> experimentInfo = (LinkedHashMap<String, Object>) map.get("experiment_info");
		String recommendedScenario = (String) experimentInfo.get("launch_file_path");
		experiment.setRecommendedScenario(recommendedScenario);

		StringTokenizer tokenizer = new StringTokenizer(recommendedScenario, "/");
		int numberOfTokens = tokenizer.countTokens();
		for (int i = 0; i < numberOfTokens; i++) {
			experiment.setName(tokenizer.nextToken());
		}

		LinkedHashMap<String, Object> amplifierParams = (LinkedHashMap<String, Object>) map.get("amplifier_params");
		LinkedHashMap<String, Object> channelsInfo = (LinkedHashMap<String, Object>) amplifierParams.get("channels_info");

		Amplifier amplifier = new Amplifier();
		experiment.setAmplifier(amplifier);

		Object additionalParams = amplifierParams.get("additional_params");
		amplifier.setAdditionalParameters(additionalParams);

		amplifier.setName((String) channelsInfo.get("name"));

		List<Integer> samplingFrequencies = (List<Integer>) channelsInfo.get("sampling_rates");
		amplifier.setSamplingFrequencies(new ArrayList<Float>());
		for (Integer sf: samplingFrequencies) {
			amplifier.getSamplingFrequencies().add(new Float(sf));
		}

		/*
		 * TODO: missing parameters in this response...
		 * LinkedHashMap<String, Object> amplifierParams = (LinkedHashMap<String, Object>) map.get("amplifier_params");
		Amplifier amplifier = new Amplifier();
		amplifier.setName((String) amplifierParams.get("amplifier_name"));
		amplifier.setAmplifierNull(Double.parseDouble((String) amplifierParams.get("amplifier_null")));
		amplifier.setSamplesPerPacket(Integer.parseInt((String) amplifierParams.get("samples_per_packet")));*/

		SignalParameters signalParameters = new SignalParameters();
		experiment.setSignalParameters(signalParameters);

		List<Object> channels = (List<Object>) channelsInfo.get("channels");
		amplifier.setChannels(new ArrayList<AmplifierChannel>());

		int i = 0;
		for (Object item: channels) {
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

		return experiment;
	}

}
