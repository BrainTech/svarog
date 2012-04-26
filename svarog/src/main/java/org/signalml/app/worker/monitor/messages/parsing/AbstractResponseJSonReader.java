package org.signalml.app.worker.monitor.messages.parsing;

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

public abstract class AbstractResponseJSonReader {

	private static final Logger logger = Logger.getLogger(AbstractResponseJSonReader.class);

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

		List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) map.get(getExperimentsListFieldName());

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

	protected abstract String getExperimentsListFieldName();

	public abstract ExperimentDescriptor parseSingleExperiment(LinkedHashMap<String, Object> map);
}
