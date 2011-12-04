package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.ArrayList;
import java.util.List;
import org.signalml.app.config.preset.Preset;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.generators.MontageGeneratorsConverter;

/**
 * This class represents an EEG system (e.g. 'EEG 10_20' or 'EEG 10_10').
 * An EEG system is described by its name, the list of {@link EegElectrode}
 * it contains and the list of {@link IMontageGenerator montage generators}
 * that can be used to generate montages for signals using this EEG system.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("eegSystem")
public class EegSystem implements Preset {

	/**
	 * The name of the EEG system.
	 */
	private String name;

	/**
	 * The list of {@link EegElectrode EEG electrodes} that belong to the
	 * system.
	 */
	private List<EegElectrode> electrodes = new ArrayList<EegElectrode>();

	/**
	 * The list of {@link IMontageGenerator montage generators} defined
	 * for this EEG system.
	 */
	@XStreamAlias("montageGenerators")
	@XStreamConverter(MontageGeneratorsConverter.class)
	private List<IMontageGenerator> montageGenerators = new ArrayList<IMontageGenerator>();

	/**
	 * Constructor. Creates an empty EEG system without any name.
	 */
	public EegSystem() {
		MontageGeneratorsConverter.addDefaultMontageGenerators(montageGenerators);
	}

	/**
	 * Creates an empty EEG system with the given name.
	 * @param name the name for the EEG system
	 */
	public EegSystem(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the number of electrodes that are defined for this EEG system.
	 * @return the number of electrodes that are defined for this EEG system
	 */
	public int getNumberOfElectrodes() {
		return electrodes.size();
	}

	/**
	 * Returns the electrode of a given index from this EEG system.
	 * @param index the index of the electrode
	 * @return
	 */
	public EegElectrode getElectrodeAt(int index) {
		return electrodes.get(index);
	}

	/**
	 * Adds an {@link EegElectrode} to this EEG system.
	 * @param electrode an electrode to be added
	 */
	public void addElectrode(EegElectrode electrode) {
		electrodes.add(electrode);
	}

	/**
	 * Returns an {@link EegElectrode} from this EEG system
	 * having the name specified.
	 * @param electrodeName the name of the electrode to be returned
	 * @return the electrode of the given name. Null if no such
	 * electrode can be found
	 */
	public EegElectrode getElectrode(String electrodeName) {
		for (EegElectrode electrode: electrodes) {
			if (electrode.getLabel().equals(electrodeName)) {
				return electrode;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		if ( !(obj instanceof EegSystem) ) return false;

		EegSystem other = (EegSystem) obj;
		if (other.getName().compareTo(this.name) == 0)
			return true;
		return false;

	}

	/**
	 * Returns a {@link IMontageGenerator} defined for this EEG system
	 * of the given index.
	 * @param i the index of the montage generator
	 * @return the montage generator of the given index
	 */
	public IMontageGenerator getMontageGeneratorAt(int i) {
		return montageGenerators.get(i);
	}

	/**
	 * Returns the number of montage generators defined for this EEG system.
	 * @return the number of montage generators defined for this EEG system
	 */
	public int getNumberOfMontageGenerators() {
		return montageGenerators.size();
	}

}
