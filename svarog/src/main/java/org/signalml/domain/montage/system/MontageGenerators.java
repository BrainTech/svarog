package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.ArrayList;
import java.util.List;
import org.signalml.app.config.preset.Preset;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.generators.MontageGeneratorsConverter;

/**
 * This class holds {@link IMontageGenerator montage generators} for a single
 * EEG system.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("montageGenerators")
public class MontageGenerators implements Preset {

	/**
	 * The name of the EEG system for which these montage generators are
	 * intended.
	 */
	private String eegSystemName;

	/**
	 * The list of {@link IMontageGenerator} that can be used by the
	 * specified EEG system.
	 */
	@XStreamAlias("generators")
	@XStreamConverter(MontageGeneratorsConverter.class)
	private List<IMontageGenerator> generators = new ArrayList<IMontageGenerator>();

	/**
	 * Constructor.
	 */
	public MontageGenerators() {
		MontageGeneratorsConverter.addDefaultMontageGenerators(generators);
	}

	/**
	 * Returns the name of the EEG system for which the montage generators
	 * stored in this {@link MontageGenerators} are intended.
	 * @return the name of the EEG system
	 */
	public String getEegSystemName() {
		return eegSystemName;
	}

	/**
	 * Returns a montage generators at the given index.
	 * @param index the index of the montage generator on the montage
	 * generators list.
	 * @return a montage generator from the specified index
	 */
	public IMontageGenerator get(int index) {
		return generators.get(index);
	}

	/**
	 * Returns the number of montage generators defined.
	 * @return the number of montage generators defined
	 */
	public int size() {
		return generators.size();
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public void setName(String name) {
	}

}
