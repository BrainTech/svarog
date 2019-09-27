package org.signalml.plugin.fftsignaltool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.change.listeners.PluginCloseListener;
import org.signalml.plugin.export.config.SvarogAccessConfig;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.fftsignaltool.actions.SaveToCSV;
import org.signalml.plugin.fftsignaltool.actions.ShowSettings;
import org.signalml.plugin.impl.ToolButtonParameters;

/**
 * Plug-in with the {@link SignalFFTTool FFT signal tool}.
 * Contains:
 * <ul>
 * <li>the tool,</li>
 * <li>the {@link SignalFFTSettingsPopupDialog dialogs} which allow to manage
 * the {@link SignalFFTSettings settings} of this tool,</li>
 * <li>the settings that are stored in the configuration file,</li>
 * </ul>
 * <p>
 *
 * @author Marcin Szumski
 */
public class FFTSignalPlugin implements Plugin, PluginCloseListener {
	protected static final Logger log = Logger.getLogger(FFTSignalPlugin.class);

	/**
	 * the {@link SvarogAccessGUI} access to Svarog GUI
	 */
	private SvarogAccessGUI guiAccess;

	/** Svarog configuration facade reference. */
	private SvarogAccessConfig configAccess;

	/**
	 * the tool that is registered by this plug-in
	 */
	private SignalFFTTool tool;

	/**
	 * the {@link SignalFFTSettings settings} how the power spectrum is
	 * displayed by {@link SignalFFTTool}
	 */
	private SignalFFTSettings signalFFTSettings;
	/**
	 * the file in which the settings are stored
	 */
	private File settingsFile;
	/**
	 * the file to which the resources of this plug-in are extracted
	 */
	private File resourceDirectory = null;

	/**
	 * the temporary files created by this plug-in, which should be removed
	 * when the application is closed
	 */
	private ArrayList<File> temporaryFiles = new ArrayList<File>();

	/**
	 * Registers this plug-in:
	 * <ul>
	 * <li>extracts the resources and creates the source of messages,</li>
	 * <li>reads or creates the {@link SignalFFTSettings FFT settings},</li>
	 * <li>creates and adds the {@link SignalFFTTool signal tool},</li>
	 * <li>creates and adds the {@link ShowSettings action}
	 * which shows the {@link SignalFFTSettingsPopupDialog}.</li></ul>
	 */
	@Override
	public void register(SvarogAccess access)
	throws IOException {

		guiAccess = access.getGUIAccess();
		configAccess = access.getConfigAccess();
		access.getChangeSupport().addCloseListener(this);

		signalFFTSettings = new SignalFFTSettings();
		settingsFile = new File(configAccess.getProfileDirectory(), "signalFFTSettings.xml");
		if (settingsFile.exists()) signalFFTSettings.readFromXMLFile(settingsFile);

		//creates and adds the signal tool
		SaveToCSV popupAction = new SaveToCSV();
		tool = new SignalFFTTool(popupAction);
		tool.setSettings(signalFFTSettings);
		tool.setSvarogAccess(access); 
		final ImageIcon icon = IconUtils.loadClassPathIcon("org/signalml/app/icon/fft.png");
		ShowSettings action = new ShowSettings(signalFFTSettings);

		ToolButtonParameters parameters = new ToolButtonParameters(_("Signal FFT (for settings press right mouse button)"), icon, null, action);
		guiAccess.addSignalTool(tool, parameters);

		//creates and adds the action which shows the

		guiAccess.addButtonToToolsMenu(action);
		
		guiAccess.addButtonToSignalPlotPopupMenu(popupAction);
	}

	/**
	 * Deletes the temporary files and stores the {@link SignalFFTSettings} in
	 * the configuration file.
	 */
	@Override
	public void applicationClosing() {
		for (File file: temporaryFiles)
			file.delete();
		signalFFTSettings.storeInXMLFile(settingsFile);
	}

}
