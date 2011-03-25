package org.signalml.plugin.fftsignaltool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.ImageIcon;

import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.change.SvarogCloseListener;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.fft.FFT;
import org.signalml.plugin.fftsignaltool.dialogs.SignalFFTSettingsDialog;
import org.signalml.plugin.fftsignaltool.dialogs.SignalFFTSettingsDialogAction;
import org.signalml.plugin.fftsignaltool.dialogs.SignalFFTToolButtonMouseListener;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

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
public class FFTSignalTool implements Plugin, SvarogCloseListener {

	/**
	 * the {@link SvarogAccessSignal access} to Svarog logic
	 */
	private SvarogAccessSignal signalAccess;
	/**
	 * the {@link SvarogAccessGUI access} to Svarog GUI
	 */
	private SvarogAccessGUI guiAccess;
	/**
	 * the tool that is registered by this plug-in
	 */
	private SignalFFTTool tool;
	/**
	 * the source of messages (labels)
	 */
	private MessageSourceAccessor messageSource;
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
	 * Extracts the files from the specified jar archive (from {@code
	 * resources/} sub-directory) to the specified directory.
	 * @param destination the directory to which the files will be extracted
	 * @param archive the jar archive
	 * @throws IOException if I/O error occurs
	 */
	private void extractFiles(File destination, File archive) throws IOException{
		JarFile jarFile = new JarFile(archive);
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()){
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			String prefix = "resources/";
			if (entryName.startsWith(prefix) && !entry.isDirectory()){
				entryName = entryName.substring(prefix.length());
				File temporaryFile = new File (destination, entryName);
				temporaryFiles.add(temporaryFile);
				temporaryFile.createNewFile();
				OutputStream out = new FileOutputStream(temporaryFile);
				InputStream in = jarFile.getInputStream(entry);
				byte[] buffer = new byte[1024];
				int dataLength;
				while((dataLength = in.read(buffer)) > 0) {
				out.write(buffer, 0, dataLength);
				}
				out.close();
				in.close();
			}
			
		}
	}
	
	/**
	 * Extracts the resources to the temporary directory and creates the
	 * {@link #messageSource source of messages} using the extracted data.
	 */
	private void createMessageSource() {
		File[] directories = signalAccess.getPluginDirectories();
		try {
			resourceDirectory = signalAccess.getTemporaryFile("");
			resourceDirectory.delete();
			resourceDirectory.mkdir();
			File file = null;
			for (File directory : directories){
				file = new File(directory, "FFTSignalTool.jar");
				if (file.exists()){
					extractFiles(resourceDirectory, file);
					break;
				}
			}
			temporaryFiles.add(resourceDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setCacheSeconds(-1);
		messageSource.setUseCodeAsDefaultMessage(true);
		if (resourceDirectory != null)
			messageSource.setBasename("file:"+resourceDirectory.getAbsolutePath()+File.separator+"message");

		this.messageSource = new MessageSourceAccessor(messageSource, Locale.getDefault());
	}
	
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
	public void register(SvarogAccess access){
		
		guiAccess = access.getGUIAccess();
		signalAccess = access.getSignalAccess();
		access.getChangeSupport().addCloseListener(this);
		createMessageSource();
		
		signalFFTSettings = new SignalFFTSettings();
		settingsFile = new File(signalAccess.getProfileDirectory(), "signalFFTSettings.xml");
		if (settingsFile.exists()) signalFFTSettings.readFromXMLFile(settingsFile);
		
		//creates and adds the signal tool
		tool = new SignalFFTTool(messageSource);
		tool.setSettings(signalFFTSettings);
		tool.setSvarogAccess(access);
		listener = new SignalFFTToolButtonMouseListener(messageSource);
		ImageIcon icon = new ImageIcon(resourceDirectory.getAbsolutePath()+File.separator + "fft.png");
		guiAccess.addSignalTool(tool, icon,	messageSource.getMessage("signalView.signalFFTToolToolTip"), listener);
		
		//creates and adds the action which shows the 
		SignalFFTSettingsDialogAction action = new SignalFFTSettingsDialogAction(messageSource, signalFFTSettings);
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

}
