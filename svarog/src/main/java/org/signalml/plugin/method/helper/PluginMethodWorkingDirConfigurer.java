package org.signalml.plugin.method.helper;

import java.io.File;
import org.apache.log4j.Logger;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.plugin.data.method.PluginMethodWithWorkDirConfiguration;
import org.signalml.plugin.export.view.AbstractPluginDialog;

public class PluginMethodWorkingDirConfigurer {

	public interface PluginWorkingDirDialogGetter {
		public AbstractPluginDialog getDialog();
	}

	protected static final Logger logger = Logger
										   .getLogger(PluginMethodWorkingDirConfigurer.class);

	private PresetManager presetManager;
	private PluginMethodWithWorkDirConfiguration defaultConfig;
	private PluginWorkingDirDialogGetter dialogGetter;

	private boolean firstRunFlag;

	public PluginMethodWorkingDirConfigurer(PresetManager presetManager,
											PluginMethodWithWorkDirConfiguration defaultConfig,
											PluginWorkingDirDialogGetter dialogGetter) {
		this.defaultConfig = defaultConfig;
		this.presetManager = presetManager;
		this.dialogGetter = dialogGetter;
		this.firstRunFlag = true;
	}

	public File configureWorkDir() {
		File workingDirectory = null;
		String workingDirectoryPath;

		boolean workingDirectoryOk = false;
		boolean needsPreset = false;

		PluginMethodWithWorkDirConfiguration config = null;

		if (this.presetManager != null) {
			try {
				config = (PluginMethodWithWorkDirConfiguration) this.presetManager
						 .getPresetByName(this.defaultConfig.getName());
			} catch (ClassCastException e) {
				logger.warn("Incorrect config type", e);

			}
		}

		if (config == null) {
			config = this.defaultConfig;
			needsPreset = true;
		}

		do {
			workingDirectoryPath = config.getWorkingDirectoryPath();
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

			if (!workingDirectoryOk || this.firstRunFlag) {

				if (!this.firstRunFlag) {
					config.setWorkingDirectoryPath(null);
				}
				boolean ok = this.dialogGetter.getDialog().showDialog(config, true);
				if (!ok) {
					return null;
				}
				needsPreset = true;
			}

			this.firstRunFlag = false;

		} while (!workingDirectoryOk);

		if (needsPreset && this.presetManager != null) {
			this.presetManager.setPreset(config);
		}

		return workingDirectory;
	}

}
