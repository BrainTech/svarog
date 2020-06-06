/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor.messages;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;

/**
 *
 * @author Marian Dovgialo <marian.dowgialo@braintech.pl>
 */
public class EEGAmplifiersMsg extends AbstractEEGExperimentsMsg{
	@JsonProperty("amplifier_list")
	private List<LinkedHashMap<String, Object>> amplifierList;
	
	@JsonIgnore
	@Override
	protected List<LinkedHashMap<String, Object>> getExperimentsList()
	{
		return amplifierList;
	}
		
	@JsonIgnore
	@Override
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
