/* ArtifactMethodConfigurer.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.method;

import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.PresetEquippedMethodConfigurer;
import org.signalml.app.util.XMLUtils;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.method.Method;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.i18n.PluginMessageSourceManager;
import org.signalml.plugin.method.IPluginMethodConfigurer;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.newartifact.data.NewArtifactApplicationData;
import org.signalml.plugin.newartifact.data.NewArtifactConfiguration;
import org.signalml.plugin.newartifact.data.NewArtifactParameters;
import org.signalml.plugin.newartifact.ui.NewArtifactMethodDialog;
import org.signalml.plugin.newartifact.ui.NewArtifactToolConfigDialog;
import org.signalml.plugin.tool.PluginResourceRepository;
import org.springframework.context.support.MessageSourceAccessor;

import com.thoughtworks.xstream.XStream;

/**
 * ArtifactMethodConfigurer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewArtifactMethodConfigurer implements IPluginMethodConfigurer,
	PresetEquippedMethodConfigurer {

	protected static final Logger logger = Logger
					       .getLogger(NewArtifactMethodConfigurer.class);

	private ViewerFileChooser fileChooser;
	private NewArtifactMethodDialog dialog;
	private PresetManager presetManager;
	private Window dialogParent;

	private NewArtifactToolConfigDialog configDialog;

	@Override
	// TODO
	public void initialize(PluginMethodManager manager) {
		ViewerElementManager viewerManager = manager.getSvarogAccess()
						     .getGUIAccess().getManager();

		this.dialogParent = viewerManager.getDialogParent();

		this.createPresetManager(manager);
		this.fileChooser = manager.getSvarogAccess().getGUIAccess()
				   .getManager().getFileChooser();

		MessageSourceAccessor messageSource;
		try {
			messageSource = PluginMessageSourceManager.GetMessageSource();
		} catch (PluginException e) {
			this.dialog = null;
			return;
		}
		this.dialog = new NewArtifactMethodDialog(messageSource,
				this.presetManager, this.dialogParent);
		this.dialog.setApplicationConfig(viewerManager.getApplicationConfig());
	}

	private void createPresetManager(PluginMethodManager manager) {
		if (this.presetManager == null) {
			ViewerElementManager elementManager = manager.getSvarogAccess()
							      .getGUIAccess().getManager();
			MethodPresetManager presetManager = new MethodPresetManager(
				"newartifact", // TODO
				// methodName
				NewArtifactParameters.class);
			presetManager.setProfileDir(elementManager.getProfileDir());
			try {
				presetManager.setStreamer((XStream) PluginResourceRepository
							  .GetResource("streamer"));
			} catch (PluginException e) {
				manager.handleException(e);
				logger.error("Can't get proper streamer");
				return;
			}
			try {
				presetManager.readFromPersistence(null);
			} catch (IOException ex) {
				if (ex instanceof FileNotFoundException) {
					logger.debug("Seems like artifact preset configuration doesn't exist");
				} else {
					logger.error(
						"Failed to read artifact presets - presets lost",
						ex);
				}
			}
			this.presetManager = presetManager;
		}
	}

	public NewArtifactToolConfigDialog getConfigDialog() {
		if (configDialog == null) {
			MessageSourceAccessor messageSource;
			try {
				messageSource = PluginMessageSourceManager.GetMessageSource();
			} catch (PluginException e) {
				return null;
			}

			configDialog = new NewArtifactToolConfigDialog(messageSource,
					dialogParent, true);
			configDialog.setFileChooser(fileChooser);
		}
		return configDialog;
	}

	@Override
	public boolean configure(Method method, Object methodDataObj)
	throws SignalMLException {

		boolean workingDirectoryOk = false;
		String workingDirectoryPath;
		File workingDirectory = null;

		NewArtifactConfiguration artifactConfig = new NewArtifactConfiguration();
		// applicationConfig
		// .getNewArtifactConfig();

		do {
			workingDirectoryPath = artifactConfig.getWorkingDirectoryPath();
			if (workingDirectoryPath != null) {

				workingDirectory = (new File(workingDirectoryPath))
						   .getAbsoluteFile();

				if (workingDirectory.exists()) {
					if (workingDirectory.isDirectory()
							&& workingDirectory.canRead()
							&& workingDirectory.canWrite()) {
						workingDirectoryOk = true;
					}
				}
			}

			if (!workingDirectoryOk) {

				artifactConfig.setWorkingDirectoryPath(null);
				boolean ok = getConfigDialog().showDialog(artifactConfig, true);
				if (!ok) {
					return false;
				}

			}

		} while (!workingDirectoryOk);

		NewArtifactApplicationData data = (NewArtifactApplicationData) methodDataObj;

		ExportedSignalDocument signalDocument = data.getSignalDocument();
		String name = signalDocument.getName();
		int dotIdx = name.lastIndexOf('.');
		if (dotIdx >= 0) {
			name = name.substring(0, dotIdx);
		}

		File projectDirectory = new File(workingDirectory, name);
		if (projectDirectory.exists()) {

			if (!projectDirectory.isDirectory()) {
				logger.warn("A file in artifact working directory is conflicting with project ["
					    + projectDirectory.getAbsolutePath() + "]");
				OptionPane.showError(dialogParent,
						     "error.artifact.failedToCreateProjectDirectory");
				return false;
			}

			File[] lockFiles = projectDirectory.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return (name.endsWith(".lock"));
				}

			});

			if (lockFiles.length > 0) {
				logger.warn("Failed to use directory ["
					    + projectDirectory.getAbsolutePath() + "]");
				OptionPane.showError(dialogParent,
						     "error.artifact.projectDirectoryLocked",
						     new Object[] { projectDirectory.getAbsolutePath() });
				return false;
			}

			int ans = OptionPane.showArtifactProjectExists(dialogParent, name);
			if (ans == OptionPane.CANCEL_OPTION) {
				return false;
			} else if (ans == OptionPane.NO_OPTION) {
				File[] files = projectDirectory.listFiles();
				boolean deleteOk;
				for (File f : files) {
					logger.info("Deleting file [" + f.getAbsolutePath() + "]");
					deleteOk = f.delete();
					if (!deleteOk) {
						logger.warn("Failed to delete file ["
							    + f.getAbsolutePath() + "]");
						OptionPane.showError(dialogParent,
								     "error.artifact.failedToClearProjectDirectory");
						return false;
					}
				}
			}
			// on yes option do nothing

		} else {

			boolean createOk = projectDirectory.mkdirs();
			if (!createOk) {
				OptionPane.showError(dialogParent,
						     "error.artifact.failedToCreateProjectDirectory");
				return false;
			}

		}

		boolean existingProject = false;
		data.setExistingProject(existingProject);

		File projectFile = new File(projectDirectory, "project.xml");
		if (projectFile.exists()) {
			try {
				XMLUtils.objectFromFile(data, projectFile,
							(XStream) PluginResourceRepository
							.GetResource("streamer"));
				existingProject = data.isExistingProject();
			} catch (IOException ex) {
				logger.error("Failed to read project", ex);
				throw new SignalMLException(ex);
			}
		}

		data.setProjectPath(workingDirectory.getAbsolutePath());
		data.setPatientName(name);
		data.setProjectFile(projectFile);

		boolean dialogOk = dialog.showDialog(data, true);
		if (!dialogOk) {
			return false;
		}

		try {
			XMLUtils.objectToFile(data, projectFile,
					      (XStream) PluginResourceRepository.GetResource("streamer"));
		} catch (IOException ex) {
			logger.error("Failed to write project", ex);
			throw new SignalMLException(ex);
		}

		return true;
	}

	@Override
	public void setPresetManager(PresetManager presetManager) {
		this.presetManager = presetManager;
	}

}
