package org.signalml.domain.montage.generators;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.system.MontageGenerators;

/**
 * A {@link Converter} for unmarshalling the list of {@link IMontageGenerator
 * montage generators} definitions from XML.
 *
 * @author Piotr Szachewicz
 */
public class MontageGeneratorsConverter implements Converter {

	/**
	 * Montage generators that are added to all EEG systems by default.
	 */
	private static final List<IMontageGenerator> defaultMontageGenerators = new ArrayList<IMontageGenerator>()
		{{
			add(new RawMontageGenerator());
			add(new CommonAverageMontageGenerator());
			add(new LeftEarMontageGenerator());
			add(new RightEarMontageGenerator());
			add(new LinkedEarsMontageGenerator());
		}};

	/**
	 * Logger for recording the history of execution.
	 */
	protected static final Logger logger = Logger.getLogger(MontageGeneratorsConverter.class);

	@Override
	public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * Adds the default montage generators to the list passed as an argument.
	 * @param montageGenerators the list to which default montage generators
	 * will be added.
	 */
	public static void addDefaultMontageGenerators(List<IMontageGenerator> montageGenerators) {
		montageGenerators.addAll(defaultMontageGenerators);
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {

		List<IMontageGenerator> montageGenerators = new ArrayList<IMontageGenerator>();

		addDefaultMontageGenerators(montageGenerators);

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

	/**
	 * Unmarshalls a {@link IMontageGenerator} from XML.
	 * @param reader the reader reading the current XML stream
	 * @return the montage generator that has been read (null if an error
	 * occured)
	 */
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

	/**
	 * Unmarshalls the single channel (used for {@link SingleReferenceMontageGenerator}.
	 * @param reader the reader reading the current XML stream
	 * @return the reference channel name
	 */
	private String unmarshallSingleChannel(HierarchicalStreamReader reader) {
		reader.moveDown();
		String channelName = null;
		if ("channel".equals(reader.getNodeName())) {
			channelName = reader.getValue();
		}
		reader.moveUp();
		return channelName;
	}

	/**
	 * Unmarshalls a vector of channel names from XML file.
	 * @param reader the reader reading the current XML stream
	 * @return the vector of channels
	 */
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

	/**
	 * Unmarshalls pairs of channel labels using the XML reader.
	 * @param reader the reader reading the current XML stream
	 * @return the pairs of channel labels.
	 */
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
