package org.signalml.app.config;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.signalml.app.config.preset.managers.BookFilterPresetManager;
import org.signalml.app.config.preset.managers.EegSystemsPresetManager;
import org.signalml.app.config.preset.managers.ExperimentsSettingsPresetManager;
import org.signalml.app.config.preset.managers.FFTSampleFilterPresetManager;
import org.signalml.app.config.preset.managers.PredefinedTimeDomainFiltersPresetManager;
import org.signalml.app.config.preset.managers.SignalExportPresetManager;
import org.signalml.app.config.preset.managers.StyledTagSetPresetManager;
import org.signalml.app.config.preset.managers.TimeDomainSampleFilterPresetManager;
import org.signalml.app.model.montage.MontagePresetManager;

public class ManagerOfPresetManagers {

	protected static final Logger logger = Logger.getLogger(ManagerOfPresetManagers.class);

	private File profileDir = null;

	private MontagePresetManager montagePresetManager = null;
	private BookFilterPresetManager bookFilterPresetManager = null;
	private SignalExportPresetManager signalExportPresetManager = null;
	private FFTSampleFilterPresetManager fftFilterPresetManager = null;
	private ExperimentsSettingsPresetManager experimentsSettingsPresetManager = null;
	private TimeDomainSampleFilterPresetManager timeDomainSampleFilterPresetManager = null;
	private PredefinedTimeDomainFiltersPresetManager predefinedTimeDomainSampleFilterPresetManager = null;
	private StyledTagSetPresetManager styledTagSetPresetManager = null;
	private EegSystemsPresetManager eegSystemsPresetManager = null;

	public ManagerOfPresetManagers(File profileDir) {
		this.profileDir = profileDir;
	}

	public void loadPresetsFromPersistence() {

		eegSystemsPresetManager = new EegSystemsPresetManager();
		eegSystemsPresetManager.setProfileDir(profileDir);
		eegSystemsPresetManager.createProfileDirectoriesIfNecessary();

		try {
			eegSystemsPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("EEG systems configuration not found!");
		} catch (Exception ex) {
			logger.error("Failed to read eeg systems configuration", ex);
		}

		montagePresetManager = new MontagePresetManager(eegSystemsPresetManager);
		montagePresetManager.setProfileDir(profileDir);

		try {
			montagePresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Montage preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read montage configuration - will use defaults", ex);
		}

		bookFilterPresetManager = new BookFilterPresetManager();
		bookFilterPresetManager.setProfileDir(profileDir);

		try {
			bookFilterPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Book filter preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read book filter configuration - will use defaults", ex);
		}

		signalExportPresetManager = new SignalExportPresetManager();
		signalExportPresetManager.setProfileDir(profileDir);

		try {
			signalExportPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Signal export preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read signal export configuration - will use defaults", ex);
		}

		fftFilterPresetManager = new FFTSampleFilterPresetManager();
		fftFilterPresetManager.setProfileDir(profileDir);

		try {
			fftFilterPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("FFT sample filter preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read FFT sample filter configuration - will use defaults", ex);
		}

		experimentsSettingsPresetManager = new ExperimentsSettingsPresetManager();
		experimentsSettingsPresetManager.setProfileDir(profileDir);
		try {
			experimentsSettingsPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Experiments settings not found - will start off with an empty list");
		} catch (Exception ex) {
			logger.error("Failed to read OpenBCI modules configuration - will start with an empty list", ex);
		}

		timeDomainSampleFilterPresetManager = new TimeDomainSampleFilterPresetManager();
		timeDomainSampleFilterPresetManager.setProfileDir(profileDir);

		try {
			timeDomainSampleFilterPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Time domain sample filter preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read time domain sample filter configuration - will use defaults", ex);
		}

		predefinedTimeDomainSampleFilterPresetManager = new PredefinedTimeDomainFiltersPresetManager();

		try {
			predefinedTimeDomainSampleFilterPresetManager.loadDefaults();
		} catch (FileNotFoundException ex) {
			logger.error("Failed to read predefined time domain sample filters - file not found", ex);
		} catch (Exception ex) {
			logger.error("Failed to read predefined time domain sample filters", ex);
		}

		styledTagSetPresetManager = new StyledTagSetPresetManager();
		styledTagSetPresetManager.setProfileDir(profileDir);

		try {
			styledTagSetPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Styled tag set preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read styled tag set configuration - will use defaults", ex);
		}
	}

	public void writePresetsToPersistence() {
		try {
			montagePresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write montage configuration", ex);
		}

		try {
			bookFilterPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write book filter configuration", ex);
		}

		try {
			signalExportPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write signal export configuration", ex);
		}

		try {
			fftFilterPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write FFT sample filter configuration", ex);
		}

		try {
			experimentsSettingsPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write new experiments settings presets to file", ex);
		}

		try {
			timeDomainSampleFilterPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write time domain sample filter configuration", ex);
		}

		/*TODO: if predefined filters should be ever edited and saved
		  as presets, this lines should be uncommented.

		  try {
		  predefinedTimeDomainSampleFilterPresetManager.writeToPersistence(null);
		  } catch (Exception ex) {
		  logger.error("Failed to write predefined time domain sample filters configuration", ex);
		  }*/

		try {
			styledTagSetPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write styled tag set configuration", ex);
		}
	}

	public MontagePresetManager getMontagePresetManager() {
		return montagePresetManager;
	}

	public void setMontagePresetManager(MontagePresetManager montagePresetManager) {
		this.montagePresetManager = montagePresetManager;
	}

	public BookFilterPresetManager getBookFilterPresetManager() {
		return bookFilterPresetManager;
	}

	public void setBookFilterPresetManager(BookFilterPresetManager bookFilterPresetManager) {
		this.bookFilterPresetManager = bookFilterPresetManager;
	}

	public SignalExportPresetManager getSignalExportPresetManager() {
		return signalExportPresetManager;
	}

	public void setSignalExportPresetManager(SignalExportPresetManager signalExportPresetManager) {
		this.signalExportPresetManager = signalExportPresetManager;
	}

	public FFTSampleFilterPresetManager getFftFilterPresetManager() {
		return fftFilterPresetManager;
	}

	public void setFftFilterPresetManager(FFTSampleFilterPresetManager fftFilterPresetManager) {
		this.fftFilterPresetManager = fftFilterPresetManager;
	}

	public ExperimentsSettingsPresetManager getExperimentsSettingsPresetManager() {
		return experimentsSettingsPresetManager;
	}

	public void setExperimentsSettingsPresetManager(ExperimentsSettingsPresetManager experimentsSettingsPresetManager) {
		this.experimentsSettingsPresetManager = experimentsSettingsPresetManager;
	}

	public TimeDomainSampleFilterPresetManager getTimeDomainSampleFilterPresetManager() {
		return timeDomainSampleFilterPresetManager;
	}

	public void setTimeDomainSampleFilterPresetManager(TimeDomainSampleFilterPresetManager timeDomainSampleFilterPresetManager) {
		this.timeDomainSampleFilterPresetManager = timeDomainSampleFilterPresetManager;
	}

	public PredefinedTimeDomainFiltersPresetManager getPredefinedTimeDomainSampleFilterPresetManager() {
		return predefinedTimeDomainSampleFilterPresetManager;
	}

	public void setPredefinedTimeDomainSampleFilterPresetManager(PredefinedTimeDomainFiltersPresetManager predefinedTimeDomainSampleFilterPresetManager) {
		this.predefinedTimeDomainSampleFilterPresetManager = predefinedTimeDomainSampleFilterPresetManager;
	}

	public StyledTagSetPresetManager getStyledTagSetPresetManager() {
		return styledTagSetPresetManager;
	}

	public void setStyledTagSetPresetManager(StyledTagSetPresetManager styledTagSetPresetManager) {
		this.styledTagSetPresetManager = styledTagSetPresetManager;
	}

	public EegSystemsPresetManager getEegSystemsPresetManager() {
		return eegSystemsPresetManager;
	}

	public void setEegSystemsPresetManager(EegSystemsPresetManager eegSystemsPresetManager) {
		this.eegSystemsPresetManager = eegSystemsPresetManager;
	}

}
