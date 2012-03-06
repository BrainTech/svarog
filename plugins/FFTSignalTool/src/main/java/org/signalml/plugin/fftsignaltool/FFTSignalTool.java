package org.signalml.plugin.fftsignaltool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.change.listeners.PluginCloseListener;
import org.signalml.plugin.export.config.SvarogAccessConfig;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.fft.FFT;
import org.signalml.plugin.fftsignaltool.dialogs.SignalFFTSettingsDialog;
import org.signalml.plugin.fftsignaltool.dialogs.SignalFFTSettingsDialogAction;
import org.signalml.plugin.fftsignaltool.dialogs.SignalFFTToolButtonMouseListener;

/**
 * Plug-in with the {@link SignalFFTTool FFT signal tool}.
 * Contains:
 * <ul>
 * <li>the tool,</li>
 * <li>the {@link SignalFFTSettingsDialog dialogs} which allow to manage
 * the {@link SignalFFTSettings settings} of this tool,</li>
 * <li>the settings that are stored in the configuration file,</li>
 * </ul>
 * <p>
 * To calculate the FFT, the {@link FFT} plug-in is used.
 * 
 * @author Marcin Szumski
 */
public class FFTSignalTool implements Plugin, PluginCloseListener {
	protected static final Logger log = Logger.getLogger(FFTSignalTool.class);
	private static FFTSignalToolI18nDelegate i18nDelegate;

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
	 * the {@link SignalFFTToolButtonMouseListener listener} that is set on
	 * the button which activates {@link SignalFFTTool}
	 */
	private SignalFFTToolButtonMouseListener listener;

	/**
	 * Registers this plug-in:
	 * <ul>
	 * <li>extracts the resources and creates the source of messages,</li>
	 * <li>reads or creates the {@link SignalFFTSettings FFT settings},</li>
	 * <li>creates and adds the {@link SignalFFTTool signal tool},</li>
	 * <li>creates and adds the {@link SignalFFTSettingsDialogAction action}
	 * which shows the {@link SignalFFTSettingsDialog}.</li></ul>
	 */
	@Override
	public void register(SvarogAccess access)
		throws IOException {

		i18nDelegate = new FFTSignalToolI18nDelegate(access);
		guiAccess = access.getGUIAccess();
		configAccess = access.getConfigAccess();
		access.getChangeSupport().addCloseListener(this);

		signalFFTSettings = new SignalFFTSettings();
		settingsFile = new File(configAccess.getProfileDirectory(), "signalFFTSettings.xml");
		if (settingsFile.exists()) signalFFTSettings.readFromXMLFile(settingsFile);
		
		//creates and adds the signal tool
		tool = new SignalFFTTool();
		tool.setSettings(signalFFTSettings);
		tool.setSvarogAccess(access);
		listener = new SignalFFTToolButtonMouseListener();
		final ImageIcon icon = access.getResourcesAccess().loadClassPathIcon("/icon/fft.png");
		guiAccess.addSignalTool(tool, icon, _("Signal FFT (for settings press and hold the mouse button here)"), listener);
		
		//creates and adds the action which shows the 
		SignalFFTSettingsDialogAction action = new SignalFFTSettingsDialogAction(signalFFTSettings);
		guiAccess.addButtonToToolsMenu(action);
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

	/**
	 * I18n shortcut.
	 * 
	 * @param msgKey message to translate (English version)
	 * @return
	 */
	public static String _(String msgKey) {
		return i18nDelegate._(msgKey);
	}

	/**
	 * I18n shortcut.
	 * 
	 * @param msgKey message to translate (English version)
	 * @param arguments the values to render
	 * @return
	 */
	public static String _R(String msgKey, Object ... arguments) {
		return i18nDelegate._R(msgKey, arguments);
	}

	/**
	 * Svarog i18n delegate getter.
	 * @return the shared delegate instance
	 */
	public static FFTSignalToolI18nDelegate i18n() {
		return i18nDelegate;
	}

}
