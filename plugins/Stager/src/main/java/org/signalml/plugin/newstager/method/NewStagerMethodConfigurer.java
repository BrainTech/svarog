/* StagerMethodConfigurer.java created 2007-11-02
 *
 */

package org.signalml.plugin.newstager.method;

import java.awt.Window;
import java.io.File;

import org.apache.log4j.Logger;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.method.PresetEquippedMethodConfigurer;
import org.signalml.app.view.components.dialogs.OptionPane;
import org.signalml.method.Method;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.method.SvarogMethodConfigurer;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.view.AbstractPluginDialog;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.method.IPluginMethodConfigurer;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.method.helper.PluginMethodWorkingDirConfigurer;
import org.signalml.plugin.newstager.data.NewStagerApplicationData;
import org.signalml.plugin.newstager.data.NewStagerConfiguration;
import org.signalml.plugin.newstager.ui.NewStagerMethodDialog;
import org.signalml.plugin.newstager.ui.NewStagerToolConfigDialog;

/**
 * StagerMethodConfigurer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerMethodConfigurer implements IPluginMethodConfigurer,
	SvarogMethodConfigurer, // FIXME
	PresetEquippedMethodConfigurer {

	protected static final Logger logger = Logger
										   .getLogger(NewStagerMethodConfigurer.class);

	private FileChooser fileChooser;
	private NewStagerMethodDialog dialog;
	private PresetManager presetManager;
	private Window dialogParent;

	private NewStagerToolConfigDialog configDialog;

	private PluginMethodWorkingDirConfigurer workDirConfigurer;

	@Override
	public void initialize(PluginMethodManager manager) {
		SvarogAccess access = manager.getSvarogAccess();
		SvarogAccessGUI guiAccess = access.getGUIAccess();

		this.dialogParent = guiAccess.getDialogParent();

		this.fileChooser = guiAccess.getFileChooser();

		this.dialog = new NewStagerMethodDialog(this.presetManager, dialogParent);
		this.dialog.setFileChooser(this.fileChooser);

		this.workDirConfigurer = new PluginMethodWorkingDirConfigurer(this.presetManager, new NewStagerConfiguration(), new PluginMethodWorkingDirConfigurer.PluginWorkingDirDialogGetter() {

			@Override
			public AbstractPluginDialog getDialog() {
				if (configDialog == null) {
					configDialog = new NewStagerToolConfigDialog(dialogParent, true);
					configDialog.setFileChooser(fileChooser);
				}
				return configDialog;
			}
		});
	}

	@Override
	public boolean configure(Method method, Object methodDataObj)
	throws SignalMLException {

		File workingDirectory = this.workDirConfigurer.configureWorkDir();
		if (workingDirectory == null) {
			return false;
		}

		NewStagerApplicationData data = (NewStagerApplicationData) methodDataObj;

		ExportedSignalDocument signalDocument = data.getSignalDocument();
		String name = signalDocument.getName();
		int dotIdx = name.lastIndexOf('.');
		if (dotIdx >= 0) {
			name = name.substring(0, dotIdx);
		}

		File projectDirectory = new File(workingDirectory, name);
		if (projectDirectory.exists()) {

			if (!projectDirectory.isDirectory()) {
				logger.warn("A file in stager working directory is conflicting with project ["
							+ projectDirectory.getAbsolutePath() + "]");
				OptionPane.showError(dialogParent,
									 "error.stager.failedToCreateProjectDirectory");
				return false;
			}

			int ans = OptionPane.showStagerProjectExists(dialogParent, name);
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
											 "error.stager.failedToClearProjectDirectory");
						return false;
					}
				}
			}
			// on yes option do nothing

		} else {

			boolean createOk = projectDirectory.mkdirs();
			if (!createOk) {
				OptionPane.showError(dialogParent,
									 "error.stager.failedToCreateProjectDirectory");
				return false;
			}

		}

		data.setProjectPath(workingDirectory.getAbsolutePath());
		data.setPatientName(name);

		boolean dialogOk = this.dialog.showDialog(data, true);
		if (!dialogOk) {
			return false;
		}

		return true;

	}

	@Override
	public void setPresetManager(PresetManager presetManager) {
		this.presetManager = presetManager;
	}

}
