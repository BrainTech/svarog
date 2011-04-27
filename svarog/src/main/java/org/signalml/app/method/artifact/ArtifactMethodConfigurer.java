/* ArtifactMethodConfigurer.java created 2007-11-02
 *
 */

package org.signalml.app.method.artifact;

import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodConfigurer;
import org.signalml.app.method.PresetEquippedMethodConfigurer;
import org.signalml.app.util.XMLUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.method.Method;
import org.signalml.plugin.export.SignalMLException;

import com.thoughtworks.xstream.XStream;

/** ArtifactMethodConfigurer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactMethodConfigurer implements InitializingMethodConfigurer, PresetEquippedMethodConfigurer {

	protected static final Logger logger = Logger.getLogger(ArtifactMethodConfigurer.class);

	private ViewerFileChooser fileChooser;
	private ArtifactMethodDialog dialog;
	private PresetManager presetManager;
	private Window dialogParent;
	private XStream streamer;

	private ApplicationConfiguration applicationConfig;
	private ArtifactToolConfigDialog configDialog;

	private ApplicationMethodManager manager;

	@Override
	public void initialize(ApplicationMethodManager manager) {
		this.manager = manager;

		dialogParent = manager.getDialogParent();
		dialog = new ArtifactMethodDialog(manager.getMessageSource(), presetManager, dialogParent);

		applicationConfig = manager.getApplicationConfig();

		dialog.setApplicationConfig(applicationConfig);

		fileChooser = manager.getFileChooser();
		streamer = manager.getStreamer();
	}

	public ArtifactToolConfigDialog getConfigDialog() {
		if (configDialog == null) {
			configDialog = new ArtifactToolConfigDialog(manager.getMessageSource(), dialogParent, true);
			configDialog.setFileChooser(fileChooser);
		}
		return configDialog;
	}

	@Override
	public boolean configure(Method method, Object methodDataObj) throws SignalMLException {

		boolean workingDirectoryOk = false;
		String workingDirectoryPath;
		File workingDirectory = null;

		ArtifactConfiguration artifactConfig = applicationConfig.getArtifactConfig();

		do {

			workingDirectoryPath = artifactConfig.getWorkingDirectoryPath();
			if (workingDirectoryPath != null) {

				workingDirectory = (new File(workingDirectoryPath)).getAbsoluteFile();

				if (workingDirectory.exists()) {
					if (workingDirectory.isDirectory() && workingDirectory.canRead() && workingDirectory.canWrite()) {
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

		ArtifactApplicationData data = (ArtifactApplicationData) methodDataObj;

		SignalDocument signalDocument = data.getSignalDocument();
		String name = signalDocument.getName();
		int dotIdx = name.lastIndexOf('.');
		if (dotIdx >= 0) {
			name = name.substring(0, dotIdx);
		}

		File projectDirectory = new File(workingDirectory, name);
		if (projectDirectory.exists()) {

			if (!projectDirectory.isDirectory()) {
				logger.warn("A file in artifact working directory is conflicting with project [" + projectDirectory.getAbsolutePath() + "]");
				OptionPane.showError(dialogParent, "error.artifact.failedToCreateProjectDirectory");
				return false;
			}

			File[] lockFiles = projectDirectory.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return(name.endsWith(".lock"));
				}

			});

			if (lockFiles.length > 0) {
				logger.warn("Failed to use directory [" + projectDirectory.getAbsolutePath() + "]");
				OptionPane.showError(dialogParent, "error.artifact.projectDirectoryLocked", new Object[] { projectDirectory.getAbsolutePath() });
				return false;
			}

			int ans = OptionPane.showArtifactProjectExists(dialogParent, name);
			if (ans == OptionPane.CANCEL_OPTION) {
				return false;
			}
			else if (ans == OptionPane.NO_OPTION) {
				File[] files = projectDirectory.listFiles();
				boolean deleteOk;
				for (File f : files) {
					logger.info("Deleting file [" + f.getAbsolutePath() + "]");
					deleteOk = f.delete();
					if (!deleteOk) {
						logger.warn("Failed to delete file [" + f.getAbsolutePath() + "]");
						OptionPane.showError(dialogParent, "error.artifact.failedToClearProjectDirectory");
						return false;
					}
				}
			}
			// on yes option do nothing

		} else {

			boolean createOk = projectDirectory.mkdirs();
			if (!createOk) {
				OptionPane.showError(dialogParent, "error.artifact.failedToCreateProjectDirectory");
				return false;
			}

		}

		boolean existingProject = false;
		data.setExistingProject(existingProject);

		File projectFile = new File(projectDirectory, "project.xml");
		if (projectFile.exists()) {
			try {
				XMLUtils.objectFromFile(data, projectFile, streamer);
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
			XMLUtils.objectToFile(data, projectFile, streamer);
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
