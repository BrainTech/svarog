package org.signalml.app.worker.monitor.messages.parsing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;

public class FindEEGAmplifiersResponseJSonReader extends AbstractResponseJSonReader {

	protected static final Logger logger = Logger.getLogger(FindEEGAmplifiersResponseJSonReader.class);

	@Override
	protected String getExperimentsListFieldName() {
		return "amplifier_list";
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

		Amplifier amplifier = experiment.getAmplifier();

		Object additionalParams = amplifierParams.get("additional_params");
		amplifier.setAdditionalParameters(additionalParams);

		amplifier.setName((String) channelsInfo.get("name"));

		readSamplingFrequencies(channelsInfo, experiment);

		List<Object> listOfChannels = (List<Object>) channelsInfo.get("channels");
		readChannelsList(listOfChannels, experiment);

		return experiment;
	}

}
