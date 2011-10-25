package org.signalml.domain.montage.generators;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 *
 * @author Piotr Szachewicz
 */
public class MontageGeneratorsConverter implements Converter {

	protected static final Logger logger = Logger.getLogger(MontageGeneratorsConverter.class);
	private static final RawMontageGenerator rawMontageGenerator = new RawMontageGenerator();
	private static final CommonAverageMontageGenerator commonAverageMontageGenerator = new CommonAverageMontageGenerator();

	@Override
	public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {

		List<IMontageGenerator> montageGenerators = new ArrayList<IMontageGenerator>();

		//add default montage generators (for all systems).
		montageGenerators.add(rawMontageGenerator);
		montageGenerators.add(commonAverageMontageGenerator);

		while (reader.hasMoreChildren()) {
			reader.moveDown();

			if ("montageGenerator".equals(reader.getNodeName())) {
				IMontageGenerator montageGenerator = unmarshallMontageGenerator(reader);
				if (montageGenerator != null) {
					montageGenerators.add(montageGenerator);
				}
			}
			reader.moveUp();
		}

		return montageGenerators;
	}

	private IMontageGenerator unmarshallMontageGenerator(HierarchicalStreamReader reader) {
		String montageGeneratorName = "";
		GeneratorType generatorType = null;
		IMontageGenerator montageGenerator = null;

		while (reader.hasMoreChildren()) {
			reader.moveDown();

			if ("name".equals(reader.getNodeName())) {
				montageGeneratorName = reader.getValue();
			} else if ("type".equals(reader.getNodeName())) {
				//generator type must be specified before the channels
				//because the converter differently parses the channels
				//depending on which type is specified.
				try {
					generatorType = GeneratorType.valueOf(reader.getValue());
				} catch (IllegalArgumentException ex) {
					logger.error("No such generator type: " + reader.getValue() + ". Montage generator will be omitted.");
					generatorType = null;
					montageGenerator = null;
				}
			} else if ("channels".equals(reader.getNodeName()) && generatorType != null) {
				switch (generatorType) {
					case SINGLE_REFERENCE:
						String singleReferenceChannel = unmarshallSingleChannel(reader);
						montageGenerator = new SingleReferenceMontageGenerator(singleReferenceChannel);
						break;
					case BIPOLAR_REFERENCE:
						String[][] bipolarReferenceChannels = unmarshallPairsOfChannels(reader);
						montageGenerator = new BipolarReferenceMontageGenerator(bipolarReferenceChannels);
						break;
					case AVERAGE_REFERENCE:
						String[] averageReferenceChannels = unmarshallVectorOfChannels(reader);
						montageGenerator = new AverageReferenceMontageGenerator(averageReferenceChannels);
						break;
				}
				if (montageGenerator != null) {
					montageGenerator.setCode(montageGeneratorName);
				}
			}

			reader.moveUp();
		}

		return montageGenerator;
	}

	private String unmarshallSingleChannel(HierarchicalStreamReader reader) {
		reader.moveDown();
		String channelName = null;
		if ("channel".equals(reader.getNodeName())) {
			channelName = reader.getValue();
		}
		reader.moveUp();
		return channelName;
	}

	private String[] unmarshallVectorOfChannels(HierarchicalStreamReader reader) {

		List<String> channels = new ArrayList<String>();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if ("channel".equals(reader.getNodeName())) {
				channels.add(reader.getValue());
			}
			reader.moveUp();
		}

		String[] result = channels.toArray(new String[channels.size()]);
		return result;
	}

	private String[][] unmarshallPairsOfChannels(HierarchicalStreamReader reader) {
		List<String[]> channels = new ArrayList<String[]>();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if ("pair".equals(reader.getNodeName())) {

				int i = 0;
				String[] pair = new String[2];
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if ("channel".equals(reader.getNodeName())) {
						pair[i] = reader.getValue();
						i++;
					}
					reader.moveUp();
				}
				channels.add(pair);

			}
			reader.moveUp();
		}

		String[][] result = new String[channels.size()][2];
		int i = 0;
		for (String[] pair : channels) {
			result[i][0] = pair[0];
			result[i][1] = pair[1];
			i++;
		}
		return result;
	}

	@Override
	public boolean canConvert(Class clazz) {
		return (ArrayList.class.equals(clazz));
	}
}
