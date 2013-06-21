package org.signalml.app.worker.monitor.messages.parsing;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;

public abstract class AbstractResponseJSonReader {

	private static final Logger logger = Logger.getLogger(AbstractResponseJSonReader.class);
	private StringBuilder log = new StringBuilder();

	public List<ExperimentDescriptor> parseExperiments(String s) {

		ObjectMapper mapper = new ObjectMapper();

		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			map = mapper.readValue(s.getBytes(), new TypeReference<LinkedHashMap<String, Object>>() {});
		} catch (JsonParseException e1) {
			log.append(_("ERROR - JsonParseException while parsing the received message!\n"));
			logger.error("", e1);
		} catch (JsonMappingException e1) {
			log.append(_("ERROR - JsonMappingException while parsing the received message!\n"));
			logger.error("", e1);
		} catch (IOException e1) {
			log.append(_("ERROR - IOException while parsing the received message!\n"));
			logger.error("", e1);
		}

		if (map == null)
			return null;

		List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) map.get(getExperimentsListFieldName());

		boolean errorAlreadyOcurred = false;
		List<ExperimentDescriptor> experiments = new ArrayList<ExperimentDescriptor>();
		for (LinkedHashMap<String, Object> exp: list) {
			try {
				ExperimentDescriptor descriptor = parseSingleExperiment(exp);
				descriptor.setCorrectlyRead(true);
				experiments.add(descriptor);
			} catch (Exception e) {
				if (!errorAlreadyOcurred) {
					//we want this message to be appended only once!
					log.append(_("ERROR - there was an error while parsing experiment data! (Bad message format?)\n"));
					errorAlreadyOcurred = true;
				}
				logger.error("There was an error parsing an experiment: " + e.getMessage());
				logger.error("", e);
			}
		}

		return experiments;
	}

	protected abstract String getExperimentsListFieldName();

	public abstract ExperimentDescriptor parseSingleExperiment(LinkedHashMap<String, Object> map);

	protected void readChannelsList(List<Object> listOfChannels, ExperimentDescriptor experiment) {
		Amplifier amplifier = experiment.getAmplifier();

		amplifier.setChannels(new ArrayList<AmplifierChannel>());

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

	protected void readSamplingFrequencies(LinkedHashMap<String, Object> parameters, ExperimentDescriptor experiment) {
		Amplifier amplifier = experiment.getAmplifier();

		List<Integer> samplingFrequencies = (List<Integer>) parameters.get("sampling_rates");
		amplifier.setSamplingFrequencies(new ArrayList<Float>());

		for (Integer sf: samplingFrequencies) {
			amplifier.getSamplingFrequencies().add(new Float(sf));
		}
	}

	public String getLog() {
		return log.toString();
	}
}
