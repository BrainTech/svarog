/* StagerMethodConfigurer.java created 2007-11-02
 *
 */

package org.signalml.app.method.stager;

import java.awt.Window;
import java.io.File;

import org.apache.log4j.Logger;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodConfigurer;
import org.signalml.app.method.PresetEquippedMethodConfigurer;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.method.Method;
import org.signalml.plugin.export.SignalMLException;

/** StagerMethodConfigurer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerMethodConfigurer implements InitializingMethodConfigurer, PresetEquippedMethodConfigurer {

	protected static final Logger logger = Logger.getLogger(StagerMethodConfigurer.class);

	private ViewerFileChooser fileChooser;
	private StagerMethodDialog dialog;
	private PresetManager presetManager;
	private Window dialogParent;

	private ApplicationConfiguration applicationConfig;
	private StagerToolConfigDialog configDialog;

	private ApplicationMethodManager manager;

	@Override
	public void initialize(ApplicationMethodManager manager) {
		this.manager = manager;

		dialogParent = manager.getDialogParent();
		dialog = new StagerMethodDialog(manager.getMessageSource(),presetManager,dialogParent);
		dialog.setFileChooser(manager.getFileChooser());

		applicationConfig = manager.getApplicationConfig();

		dialog.setApplicationConfig(applicationConfig);

		fileChooser = manager.getFileChooser();
	}

	public StagerToolConfigDialog getConfigDialog() {
		if (configDialog == null) {
			configDialog = new StagerToolConfigDialog(manager.getMessageSource(), dialogParent, true);
			configDialog.setFileChooser(fileChooser);
		}
		return configDialog;
	}

	@Override
	public boolean configure(Method method, Object methodDataObj) throws SignalMLException {

		boolean workingDirectoryOk = false;
		String workingDirectoryPath;
		File workingDirectory = null;

		StagerConfiguration stagerConfig = applicationConfig.getStagerConfig();

		do {

			workingDirectoryPath = stagerConfig.getWorkingDirectoryPath();
			if (workingDirectoryPath != null) {

				workingDirectory = (new File(workingDirectoryPath)).getAbsoluteFile();

				if (workingDirectory.exists()) {
					if (workingDirectory.isDirectory() && workingDirectory.canRead() && workingDirectory.canWrite()) {
						workingDirectoryOk = true;
					}
				}

			}

			if (!workingDirectoryOk) {

				stagerConfig.setWorkingDirectoryPath(null);
				boolean ok = getConfigDialog().showDialog(stagerConfig, true);
				if (!ok) {
					return false;
				}

			}

		} while (!workingDirectoryOk);

		StagerApplicationData data = (StagerApplicationData) methodDataObj;

		SignalDocument signalDocument = data.getSignalDocument();
		String name = signalDocument.getName();
		int dotIdx = name.lastIndexOf('.');
		if (dotIdx >= 0) {
			name = name.substring(0, dotIdx);
		}

		File projectDirectory = new File(workingDirectory, name);
		if (projectDirectory.exists()) {

			if (!projectDirectory.isDirectory()) {
				logger.warn("A file in stager working directory is conflicting with project [" + projectDirectory.getAbsolutePath() + "]");
				OptionPane.showError(dialogParent, "error.stager.failedToCreateProjectDirectory");
				return false;
			}

			int ans = OptionPane.showStagerProjectExists(dialogParent, name);
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
						OptionPane.showError(dialogParent, "error.stager.failedToClearProjectDirectory");
						return false;
					}
				}
			}
			// on yes option do nothing

		} else {

			boolean createOk = projectDirectory.mkdirs();
			if (!createOk) {
				OptionPane.showError(dialogParent, "error.stager.failedToCreateProjectDirectory");
				return false;
			}

		}

		data.setProjectPath(workingDirectory.getAbsolutePath());
		data.setPatientName(name);

		boolean dialogOk = dialog.showDialog(data, true);
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
