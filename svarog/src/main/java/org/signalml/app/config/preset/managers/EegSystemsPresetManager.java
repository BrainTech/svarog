package org.signalml.app.config.preset.managers;

import java.io.File;
import java.io.IOException;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.system.EegSystemName;
import org.signalml.domain.montage.system.MontageGenerators;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This {@link PresetManager} manages the available EEG systems definitions
 * which are stored in the 'eegSystems' directory inside the profile directory.
 * It reads the EEG electrodes definitions from the 'eegSystems/electrodes' and
 * montage generators definitions from the 'eegSystems/generators' directory and
 * connects them appropriately to form EEG systems.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("eegSystems")
public class EegSystemsPresetManager extends AbstractPresetManager {

	/**
	 * The preset manager that handles the EEG electrodes definitions.
	 */
	private EegElectrodesPresetManager eegElectrodesPresetManager = new EegElectrodesPresetManager();
	/**
	 * The preset manager that handles the
	 * {@link IMontageGenerator montage generators} definitions.
	 */
	private MontageGeneratorsPresetManager montageGeneratorsPresetManager = new MontageGeneratorsPresetManager();

	@Override
	public void readFromPersistence(File file) throws IOException {
		eegElectrodesPresetManager.readFromPersistence(null);
		this.copyFrom(eegElectrodesPresetManager);
		montageGeneratorsPresetManager.readFromPersistence(null);

		matchMontageGeneratorsWithEegSystems(this.getPresets(), montageGeneratorsPresetManager.getPresets());
	}

	/**
	 * Matches the right {@link IMontageGenerator} with the correct EEG system
	 * taking into account the name of the EEG system.
	 *
	 * @param eegSystemsPresets the list of EEG systems presets to which montage
	 * generators should be connected
	 * @param montageGeneratorsPresets montage generators to be connected to the
	 * right EEG systems
	 */
	protected void matchMontageGeneratorsWithEegSystems(Preset[] eegSystemsPresets, Preset[] montageGeneratorsPresets) {
		for (Preset preset : eegSystemsPresets) {
			EegSystem eegSystem = (EegSystem) preset;
			EegSystemName eegSystemName = ((EegSystem) preset).getEegSystemName();

			for (Preset generatorPreset : montageGeneratorsPresets) {
				MontageGenerators montageGenerators = (MontageGenerators) generatorPreset;
				EegSystemName generatorEegSystem = montageGenerators.getEegSystemName();

				if (generatorEegSystem == null || generatorEegSystem.getSymbol() == null) {
					continue;
				}

				String symbol = generatorEegSystem.getSymbol();
				String type = generatorEegSystem.getType() == null ? null : generatorEegSystem.getType();

				if (eegSystemName.getSymbol().equalsIgnoreCase(symbol)
						&& (type == null || type.isEmpty()
						|| type.equalsIgnoreCase(eegSystemName.getType()))) {
					//if type of the EEG system in the montage generator is NULL then
					//it is not taken into account while connecting montage generators
					//with EEG systems - this makes sense for montage generators that fit for
					//every type of an EEG system.
					eegSystem.setMontageGenerators(montageGenerators);
				}
			}
		}
	}

	@Override
	public void setProfileDir(File profileDir) {
		super.setProfileDir(profileDir);
		eegElectrodesPresetManager.setProfileDir(profileDir);
		montageGeneratorsPresetManager.setProfileDir(profileDir);
	}

	/**
	 * Creates the 'eegSystems/electrodes' and 'eegSystems/generators'
	 * directories in the user directory, so that the user knows where is the
	 * place to put his electrode and generators definitions.
	 */
	public void createProfileDirectoriesIfNecessary() {
		eegElectrodesPresetManager.createProfileDirectoryIfNecessary();
		montageGeneratorsPresetManager.createProfileDirectoryIfNecessary();
	}

	@Override
	public String getStandardFilename() {
		throw new UnsupportedOperationException("This opperation is not supported by this preset manager.");
	}

	@Override
	public Class<?> getPresetClass() {
		throw new UnsupportedOperationException("This opperation is not supported by this preset manager.");
	}

	public EegSystem getEegSystem(EegSystemName eegSystemName) {
		for (Preset preset : presets) {
			EegSystem eegSystem = (EegSystem) preset;
			if (eegSystem.getEegSystemName().equals(eegSystemName)) {
				return eegSystem;
			}
		}
		return null;
	}

	@Override
	public Preset getDefaultPreset() {
		for (Preset preset : presets) {
			EegSystem eegSystem = (EegSystem) preset;
			if (eegSystem.isDefault()) {
				return eegSystem;
			}
		}
		return null;
	}

}
