/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor.messages;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 *
 * @author Marian Dovgialo ...
 */
public abstract class AbstractEEGExperimentsMsg extends LauncherMessage{
	@JsonIgnore
	private static final Logger logger = Logger.getLogger(AbstractEEGExperimentsMsg.class);
	@JsonIgnore
	private StringBuilder log = new StringBuilder();

	@JsonIgnore
	public List<ExperimentDescriptor> getExperiments() {

		List<LinkedHashMap<String, Object>> list = getExperimentsList();

		boolean errorAlreadyOcurred = false;
		List<ExperimentDescriptor> experiments = new ArrayList<>();
		for (LinkedHashMap<String, Object> exp: list) {
			try {
				ExperimentDescriptor descriptor = parseSingleExperiment(exp);
				descriptor.setCorrectlyRead(true);
				experiments.add(descriptor);
			} catch (Exception e) {
				if (!errorAlreadyOcurred) {
					//we want this message to be appended only once!
					log.append(_("ERROR: there was an error while parsing experiment data! (Bad message format?)\n"));
					errorAlreadyOcurred = true;
				}
				logger.error("There was an error parsing an experiment: " + e.getMessage());
				logger.error("", e);
			}
		}

		return experiments;
	}

	@JsonIgnore
	protected abstract List<LinkedHashMap<String, Object>> getExperimentsList();

	@JsonIgnore
	public abstract ExperimentDescriptor parseSingleExperiment(LinkedHashMap<String, Object> map);

	@JsonIgnore
	protected void readChannelsList(List<Object> listOfChannels, ExperimentDescriptor experiment) {
		Amplifier amplifier = experiment.getAmplifier();

		amplifier.setChannels(new ArrayList<>());

		int i = 0;
		for (Object item: listOfChannels) {
			LinkedHashMap<String, Object> channelInfo = (LinkedHashMap<String, Object>) item;
			String channelName = (String) channelInfo.get("name");
			AmplifierChannel channel = new AmplifierChannel(i+1, channelName);
			channel.setOriginalName(channelName);
			double gain = (Double) channelInfo.get("gain");
			double offset = (Double) channelInfo.get("offset");
			channel.setCalibrationGain((float)gain);
			channel.setCalibrationOffset((float)offset);

			/*
			 * Sometimes the idle field is interpreted as Long, sometimes - Integer.
			 * We actually need it to be double, so it's safer to trasform it to String
			 * and then parse.
			 */
			String idleString = channelInfo.get("idle").toString();
			channel.setIdle(Double.parseDouble(idleString));

			channel.setSelected(false);
			amplifier.getChannels().add(channel);
			i++;
		}

		experiment.getSignalParameters().setChannelCount(i);
	}
	
	@JsonIgnore
	protected void readSamplingFrequencies(LinkedHashMap<String, Object> parameters, ExperimentDescriptor experiment) {
		Amplifier amplifier = experiment.getAmplifier();

		List<Double> samplingFrequencies = (List<Double>) parameters.get("sampling_rates");
		amplifier.setSamplingFrequencies(new ArrayList<>(samplingFrequencies));
	}
}
