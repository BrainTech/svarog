/* ArtifactMethodConfigurer.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.method;

import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.method.PresetEquippedMethodConfigurer;
import org.signalml.app.util.XMLUtils;
import org.signalml.app.view.components.dialogs.OptionPane;
import org.signalml.method.Method;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.method.SvarogMethodConfigurer;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.view.AbstractPluginDialog;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.method.IPluginMethodConfigurer;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.method.helper.PluginMethodWorkingDirConfigurer;
import org.signalml.plugin.newartifact.NewArtifactPlugin;
import org.signalml.plugin.newartifact.data.NewArtifactApplicationData;
import org.signalml.plugin.newartifact.data.NewArtifactConfiguration;
import org.signalml.plugin.newartifact.ui.NewArtifactMethodDialog;
import org.signalml.plugin.newartifact.ui.NewArtifactToolConfigDialog;
import org.signalml.plugin.tool.PluginResourceRepository;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

/**
 * ArtifactMethodConfigurer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewArtifactMethodConfigurer implements IPluginMethodConfigurer,
	SvarogMethodConfigurer, // FIXME
	PresetEquippedMethodConfigurer {

	protected static final Logger logger = Logger
										   .getLogger(NewArtifactMethodConfigurer.class);

	private FileChooser fileChooser;
	private NewArtifactMethodDialog dialog;
	private PresetManager presetManager;
	private Window dialogParent;

	private NewArtifactToolConfigDialog configDialog;

	private PluginMethodWorkingDirConfigurer workDirConfigurer;

	@Override
	public void initialize(PluginMethodManager manager) {
		SvarogAccessGUI guiAccess = manager.getSvarogAccess().getGUIAccess();
		this.dialogParent = guiAccess.getDialogParent();

		this.fileChooser = guiAccess.getFileChooser();
		this.dialog = new NewArtifactMethodDialog(this.presetManager,
				this.dialogParent);

		this.workDirConfigurer = new PluginMethodWorkingDirConfigurer(
			this.presetManager,
			new NewArtifactConfiguration(),
		new PluginMethodWorkingDirConfigurer.PluginWorkingDirDialogGetter() {

			@Override
			public AbstractPluginDialog getDialog() {
				if (configDialog == null) {
					configDialog = new NewArtifactToolConfigDialog(
						dialogParent, true);
					configDialog.setFileChooser(fileChooser);
				}
				return configDialog;
			}
		});
	}

	@Override
	public boolean configure(Method method, Object methodDataObj)
	throws SignalMLException {

		NewArtifactApplicationData data = (NewArtifactApplicationData) methodDataObj;

		File workingDirectory = this.workDirConfigurer.configureWorkDir();
		if (workingDirectory == null) {
			return false;
		}

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

			int ans = OptionPane.showReuseReplaceOption(
						  dialogParent,
						  NewArtifactPlugin.i18n()._R(
							  "Project [{0}] exists. Reuse or replace?", name));
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
										(XStream) PluginResourceRepository.GetResource(
											"streamer", NewArtifactPlugin.class));
				existingProject = data.isExistingProject();
			} catch (XStreamException ex) {
				logger.warn("Incompatible project data", ex);
			} catch (IOException ex) {
				logger.warn("Failed to read project", ex);
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
								  (XStream) PluginResourceRepository.GetResource("streamer",
										  NewArtifactPlugin.class));
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
