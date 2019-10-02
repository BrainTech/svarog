package org.signalml.plugin.bookreporter.method;

import java.awt.Window;
import org.apache.log4j.Logger;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.method.PresetEquippedMethodConfigurer;
import org.signalml.method.Method;
import org.signalml.plugin.bookreporter.data.BookReporterData;
import org.signalml.plugin.bookreporter.ui.BookReporterMethodDialog;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.method.SvarogMethodConfigurer;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.method.IPluginMethodConfigurer;
import org.signalml.plugin.method.PluginMethodManager;

/**
 * @author piotr@develancer.pl
 * (based on Michal Dobaczewski's NewStagerMethodConfigurer)
 */
public class BookReporterMethodConfigurer implements IPluginMethodConfigurer,
	SvarogMethodConfigurer, // FIXME
	PresetEquippedMethodConfigurer {

	protected static final Logger logger = Logger.getLogger(BookReporterMethodConfigurer.class);

	private BookReporterMethodDialog dialog;
	private PresetManager presetManager;

	@Override
	public void initialize(PluginMethodManager manager) {
		SvarogAccess access = manager.getSvarogAccess();
		SvarogAccessGUI guiAccess = access.getGUIAccess();

		Window dialogParent = guiAccess.getDialogParent();
		FileChooser fileChooser = guiAccess.getFileChooser();

		this.dialog = new BookReporterMethodDialog(this.presetManager, dialogParent);
		this.dialog.setFileChooser(fileChooser);
	}

	@Override
	public boolean configure(Method method, Object methodDataObj) throws SignalMLException {
		BookReporterData data = (BookReporterData) methodDataObj;
		return this.dialog.showDialog(data, true);
	}

	@Override
	public void setPresetManager(PresetManager presetManager) {
		this.presetManager = presetManager;
	}

}
