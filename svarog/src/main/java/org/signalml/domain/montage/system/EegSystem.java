package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.List;
import org.signalml.app.config.preset.Preset;
import org.signalml.domain.montage.generators.IMontageGenerator;

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
	 * The unique name of this EEG system.
	 */
	private EegSystemName eegSystemName;

	/**
	 * This variable holds if this eeg system is the default EEG system that should
	 * be used.
	 */
	private boolean isDefault;

	/**
	 * The list of {@link EegElectrode EEG electrodes} that belong to the
	 * system.
	 */
	private List<EegElectrode> electrodes = new ArrayList<EegElectrode>();

	/**
	 * Contains the list of {@link IMontageGenerator montage generators} defined
	 * for this EEG system.
	 */
	private transient MontageGenerators montageGenerators = new MontageGenerators();

	/**
	 * Constructor. Creates an empty EEG system without any name.
	 */
	public EegSystem() {
	}

	@Override
	public String getName() {
		return eegSystemName.getFullName();
	}

	@Override
	public void setName(String name) {
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
			if (electrode.getLabel().toUpperCase().equals(electrodeName.toUpperCase())) {
				return electrode;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return eegSystemName.getFullName();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof EegSystem)) return false;

		EegSystem other = (EegSystem) obj;
		if (this.eegSystemName.equals(other.eegSystemName))
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

	/**
	 * Sets the montage generators that can be used with this EEG system.
	 * @param montageGenerators the montage generators for this EEG system
	 */
	public void setMontageGenerators(MontageGenerators montageGenerators) {
		this.montageGenerators = montageGenerators;
	}

	/**
	 * Returns the name of the EEG system, uniquely identifying
	 * this EEG system.
	 * @return the name of this EEG system
	 */
	public EegSystemName getEegSystemName() {
		return eegSystemName;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

}
