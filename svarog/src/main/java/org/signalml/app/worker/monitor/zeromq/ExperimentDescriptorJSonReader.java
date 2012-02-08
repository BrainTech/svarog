package org.signalml.app.worker.monitor.zeromq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.signalml.app.model.document.opensignal.Amplifier;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentStatus;
import org.signalml.app.model.document.opensignal.SignalParameters;
import org.signalml.app.view.document.opensignal.elements.AmplifierChannel;

public class ExperimentDescriptorJSonReader {

	public List<ExperimentDescriptor> parseExperiments(String s) {
		
		ObjectMapper mapper = new ObjectMapper();
		List<LinkedHashMap<String, Object>> list = null;
		try {
			list = mapper.readValue(new File("/home/kret/listexp"), 
					new TypeReference<List<LinkedHashMap<String, Object>>>() {});
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<ExperimentDescriptor> experiments = new ArrayList<ExperimentDescriptor>();
		for (LinkedHashMap<String, Object> exp: list) {
			ExperimentDescriptor descriptor = parseSingleExperiment(exp);
			experiments.add(descriptor);
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
		
		//amplifier
		LinkedHashMap<String, Object> amplifierParams = (LinkedHashMap<String, Object>) map.get("amplifier_params");
		Amplifier amplifier = new Amplifier();
		amplifier.setName((String) amplifierParams.get("amplifier_name"));
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
			AmplifierChannel channel = new AmplifierChannel(i, (String) channelInfo.get("name"));
			channel.setSelected(false);
			amplifier.getChannels().add(channel);
			i++;
		}
		
		signalParameters.setChannelCount(i);
		
		String activeChannels = (String) amplifierParams.get("active_channels");
		
		StringTokenizer tokenizer = new StringTokenizer(activeChannels, ";");
		while(tokenizer.hasMoreTokens()) {
		      String channelNumberStr = tokenizer.nextToken();

		      try {
		    	  int channelNumber = Integer.parseInt(channelNumberStr);
		    	  amplifier.getChannels().get(channelNumber).setSelected(true);
		      }
		      catch (NumberFormatException ex) {
		    	  continue;
		      }
		}

		experiment.setAmplifier(amplifier);

		experiment.setExperimentAddress((String) map.get("rep_addr"));
		
		return experiment;
	}
	
}
