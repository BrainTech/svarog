/* ViewerFileChooser.java created 2007-09-13
 *
 */
package org.signalml.app.view;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.view.dialog.OptionPane;
import org.springframework.context.support.MessageSourceAccessor;

/** ViewerFileChooser
 *
 *	Not thread safe! Use from event dispatcher thread only!
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(ViewerFileChooser.class);

	private MessageSourceAccessor messageSource;
	private ApplicationConfiguration applicationConfig;

	private FileFilter _getFilter(String message, String...extensions){
		return new FileNameExtensionFilter(messageSource.getMessage(message), extensions);
	}

	public void initialize() {
		messageSource.getMessage("filechooser.consoleSaveAsText.title");
		messageSource.getMessage("filechooser.tableSaveAsText.title");

		for(OptionSet options : OptionSet.values())
			options.initialize(messageSource);

		FileFilter text = _getFilter("filechooser.filter.textFiles", "txt");
		FileFilter binary = _getFilter("filechooser.filter.binaryFiles", "bin");
		FileFilter xml = _getFilter("filechooser.filter.xmlFiles", "xml");
		FileFilter book = _getFilter("filechooser.filter.bookFiles", "b");
		FileFilter png = _getFilter("filechooser.filter.pngFiles", "png");
		FileFilter config = _getFilter("filechooser.filter.configFiles", "cfg");
		FileFilter exe = _getFilter("filechooser.filter.exeFiles", "exe");
		FileFilter jar = _getFilter("filechooser.filter.jarFiles", "jar");
		FileFilter jar_class = _getFilter("filechooser.filter.codeFiles",
						  "java", "class");

		OptionSet.consoleSaveAsText.setFilters(text);
		OptionSet.tableSaveAsText.setFilters(text);
		OptionSet.samplesSaveAsText.setFilters(text);
		OptionSet.samplesSaveAsFloat.setFilters(binary);
		OptionSet.chartSaveAsPng.setFilters(png);
		OptionSet.saveMP5Config.setFilters(config);
		OptionSet.saveMP5Signal.setFilters(binary);

		FileFilter managed[] = ManagedDocumentType.TAG.getFileFilters(messageSource);
		OptionSet.saveTag.setFilters(managed);
		OptionSet.openTag.setFilters(managed);
		OptionSet.expertTag.setFilters(managed);

		OptionSet.readXMLManifest.setFilters(xml);
		OptionSet.exportSignal.setFilters(binary);
		OptionSet.exportBook.setFilters(book);
		OptionSet.savePreset.setFilters(xml);
		OptionSet.loadPreset.setFilters(xml);

		if (Pattern.matches(".*[Ww]indows.*", System.getProperty("os.name")))
			OptionSet.executablePreset.setFilters(exe);

		OptionSet.bookPreset.setFilters(book);
		OptionSet.bookSavePreset.setFilters(book);

		OptionSet.jarFilePreset.setFilters(jar);
		OptionSet.codeFilePreset.setFilters(jar_class);
	}

	public int showDialog(Component parent, OptionSet options) {
		options.use(this);
		return showDialog(parent, options.okButton);
	}

	public File chooseFileForRead(Component parent, OptionSet options) {

		File file = null;

		boolean hasFile = false;

		do {

			int result = showDialog(parent, options);
			if (result == APPROVE_OPTION) {
				file = getSelectedFile();
			} else {
				break;
			}

			if (!file.exists() || !file.canRead()) {

				OptionPane.showFileNotFound(parent, file);
				file = null;
				continue;

			}

			hasFile = true;

		} while (!hasFile);

		return file;

	}

	public File[] chooseFilesForRead(Component parent, OptionSet options) {

		File[] files = null;

		int result = showDialog(parent, options);
		if (result == APPROVE_OPTION) {
			files = getSelectedFiles();
		} else {
			return null;
		}

		File[] okFiles = new File[files.length];
		int cnt = 0;

		for (File file : files) {

			if (!file.exists() || !file.canRead()) {

				OptionPane.showFileNotFound(parent, file);
				continue;

			} else {
				okFiles[cnt] = file;
				cnt++;
			}

		}

		okFiles = Arrays.copyOf(okFiles, cnt);

		return okFiles;

	}

	public File[] chooseDirectoriesForRead(Component parent, OptionSet options) {

		File[] files = null;

		int result = showDialog(parent, options);
		if (result == APPROVE_OPTION) {
			files = getSelectedFiles();
		} else {
			return null;
		}

		File[] okFiles = new File[files.length];
		int cnt = 0;

		for (File file : files) {

			if (!file.exists() || !file.isDirectory() || !file.canRead()) {

				OptionPane.showDirectoryNotFound(parent, file);
				continue;

			} else {
				okFiles[cnt] = file;
				cnt++;
			}

		}

		okFiles = Arrays.copyOf(okFiles, cnt);

		return okFiles;

	}

	public File chooseFileForExecute(Component parent, OptionSet options) {

		File file = null;

		boolean hasFile = false;

		do {

			int result = showDialog(parent, options);
			if (result == APPROVE_OPTION) {
				file = getSelectedFile();
			} else {
				break;
			}

			if (!file.exists() || !file.canRead() || !file.canExecute()) {

				OptionPane.showFileNotFound(parent, file);
				file = null;
				continue;

			}

			hasFile = true;

		} while (!hasFile);

		return file;

	}

	public File chooseFileForWrite(Component parent, OptionSet options) {

		File file = null;

		boolean hasFile = false;

		do {

			int result = showDialog(parent, options);
			if (result == APPROVE_OPTION) {
				file = getSelectedFile();
			} else {
				break;
			}

			File dir = file.getParentFile();

			if (dir != null && (!dir.exists() || !dir.canRead() || !dir.canWrite())) {

				OptionPane.showDirectoryNotFound(parent, dir);
				file = null;
				continue;

			}

			hasFile = true;

		} while (!hasFile);

		return file;

	}

	public File chooseDirectory(Component parent, OptionSet options) {

		File file;

		boolean hasFile = false;

		do {

			file = null;

			int result = showDialog(parent, options);
			if (result == APPROVE_OPTION) {
				file = getSelectedFile();
			} else {
				break;
			}

			if (!file.exists()) {

				int ans = OptionPane.showDirectoryDoesntExistCreate(parent, file);
				if (ans != OptionPane.YES_OPTION) {
					continue;
				}

				boolean createOk = file.mkdirs();
				if (!createOk) {
					OptionPane.showDirectoryNotCreated(parent, file);
					continue;
				}

			}

			if (!file.canRead() || !file.canWrite()) {

				OptionPane.showDirectoryNotAccessible(parent, file);
				continue;

			}

			hasFile = true;

		} while (!hasFile);

		return file;

	}

	public File chooseConsoleSaveAsTextFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastConsoleSaveAsTextPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.consoleSaveAsText);
		if (file != null) {
			applicationConfig.setLastConsoleSaveAsTextPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseTableSaveAsTextFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastTableSaveAsTextPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.tableSaveAsText);
		if (file != null) {
			applicationConfig.setLastTableSaveAsTextPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseSamplesSaveAsTextFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastSamplesSaveAsTextPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.samplesSaveAsText);
		if (file != null) {
			applicationConfig.setLastSamplesSaveAsTextPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseSamplesSaveAsFloatFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastSamplesSaveAsFloatPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.samplesSaveAsFloat);
		if (file != null) {
			applicationConfig.setLastSamplesSaveAsFloatPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseExportSignalFile(Component parent, File fileSuggestion) {

		File file = null;

		String path = applicationConfig.getLastExportSignalPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}
		if (fileSuggestion == null) {
			setSelectedFile(new File(""));
		} else {
			setSelectedFile(fileSuggestion);
		}

		file = chooseFileForWrite(parent, OptionSet.exportSignal);
		if (file != null) {
			applicationConfig.setLastExportSignalPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseExportBookFile(Component parent, File fileSuggestion) {

		File file = null;

		String path = applicationConfig.getLastExportBookPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}
		if (fileSuggestion == null) {
			setSelectedFile(new File(""));
		} else {
			setSelectedFile(fileSuggestion);
		}

		file = chooseFileForWrite(parent, OptionSet.exportBook);
		if (file != null) {
			applicationConfig.setLastExportBookPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseChartSaveAsPngFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastChartSaveAsPngPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.chartSaveAsPng);
		if (file != null) {
			applicationConfig.setLastChartSaveAsPngPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseSaveMP5ConfigFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastSaveMP5ConfigPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.saveMP5Config);
		if (file != null) {
			applicationConfig.setLastSaveMP5ConfigPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseSaveMP5SignalFile(Component parent) {

		File file = null;

		File selFile = getSelectedFile();
		boolean resetDir = false;
		if (selFile == null || selFile.getName().isEmpty()) {
			String path = applicationConfig.getLastSaveMP5ConfigPath();
			if (path != null) {
				setCurrentDirectory(new File(path));
				resetDir = true;
			}
		}

		file = chooseFileForWrite(parent, OptionSet.saveMP5Signal);
		if (file != null && resetDir) {
			applicationConfig.setLastSaveMP5ConfigPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseSaveDocument(Component parent, FileFilter[] filters) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastSaveDocumentPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}
		//XXX		OptionSet.saveDocument.fileFilters = filters;

		file = chooseFileForWrite(parent, OptionSet.saveDocument);
		if (file != null) {
			applicationConfig.setLastSaveDocumentPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseSaveTag(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastSaveTagPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.saveTag);
		if (file != null) {
			applicationConfig.setLastSaveTagPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseOpenTag(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastOpenTagPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForRead(parent, OptionSet.openTag);
		if (file != null) {
			applicationConfig.setLastOpenTagPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseExpertTag(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastExpertTagPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForRead(parent, OptionSet.expertTag);
		if (file != null) {
			applicationConfig.setLastExpertTagPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseImportTag(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastImportTagPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForRead(parent, OptionSet.importTag);
		if (file != null) {
			applicationConfig.setLastImportTagPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseExportTag(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastExportTagPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.exportTag);
		if (file != null) {
			applicationConfig.setLastExportTagPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseReadXMLManifest(File directory, File fileSuggestion, Component parent) {

		File file = null;

		setCurrentDirectory(directory);
		if (fileSuggestion != null && fileSuggestion.exists()) {
			setSelectedFile(fileSuggestion);
		} else {
			setSelectedFile(new File(""));
		}

		file = chooseFileForRead(parent, OptionSet.readXMLManifest);

		return file;

	}

	public File chooseSavePresetFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastPresetPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.savePreset);
		if (file != null) {
			applicationConfig.setLastPresetPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseLoadPresetFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastPresetPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForRead(parent, OptionSet.loadPreset);
		if (file != null) {
			applicationConfig.setLastPresetPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseArtifactProjectDirectory(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastArtifactProjectPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.artifactProjectPreset);
		if (file != null) {
			applicationConfig.setLastArtifactProjectPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseExecutableFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		setCurrentDirectory(new File(System.getProperty("user.dir")));

		file = chooseFileForExecute(parent, OptionSet.executablePreset);

		return file;

	}

	public File chooseBookFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastBookFilePath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForRead(parent, OptionSet.bookPreset);
		if (file != null) {
			applicationConfig.setLastBookFilePath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseBookFileForWrite(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastBookFilePath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, OptionSet.bookSavePreset);
		if (file != null) {
			applicationConfig.setLastBookFilePath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseWorkingDirectory(Component parent, File currentDirectory) {

		File file = null;

		boolean dirSet = false;
		if (currentDirectory != null) {
			if (currentDirectory.exists()) {
				setSelectedFile(currentDirectory);
				dirSet = true;
			} else {
				File parentDir = currentDirectory.getParentFile();
				if (parentDir.exists()) {
					dirSet = true;
					setSelectedFile(new File(""));
					setCurrentDirectory(parentDir);
				}
			}
		}

		if (!dirSet) {
			setSelectedFile(new File(""));
			setCurrentDirectory(new File(System.getProperty("user.dir")));
		}

		file = chooseDirectory(parent, OptionSet.workingDirectoryPreset);

		return file;

	}

	public File[] chooseClassPathDirectories(Component parent) {

		File[] files = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastLibraryPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		files = chooseDirectoriesForRead(parent, OptionSet.classPathDirectoryPreset);
		if (files != null) {
			applicationConfig.setLastLibraryPath(getCurrentDirectory().getAbsolutePath());
		}

		return files;

	}

	public File[] chooseJarFiles(Component parent) {

		File[] files = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastLibraryPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		files = chooseFilesForRead(parent, OptionSet.jarFilePreset);
		if (files != null) {
			applicationConfig.setLastLibraryPath(getCurrentDirectory().getAbsolutePath());
		}

		return files;

	}

	public File chooseCodeFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastLibraryPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForRead(parent, OptionSet.codeFilePreset);
		if (file != null) {
			applicationConfig.setLastLibraryPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	protected enum OptionSet {
		consoleSaveAsText("filechooser.consoleSaveAsText.title", "save"),
		tableSaveAsText("filechooser.tableSaveAsText.title", "save"),
		samplesSaveAsText("filechooser.samplesSaveAsText.title", "save"),
		samplesSaveAsFloat("filechooser.samplesSaveAsFloat.title", "save"),
		chartSaveAsPng("filechooser.chartSaveAsPng.title", "save"),
		saveMP5Config("filechooser.saveMP5Config.title", "save"),
		saveMP5Signal("filechooser.saveMP5Signal.title", "save"),
		saveDocument("filechooser.saveDocument.title", "save"),
		saveTag("filechooser.saveTag.title", "save"),
		openTag("filechooser.openTag.title", "open"),
		expertTag("filechooser.expertTag.title", "choose"),
		importTag("filechooser.importTag.title", "import"),
		exportTag("filechooser.exportTag.title", "export"),
		savePreset("filechooser.savePreset.title", "save"),
		loadPreset("filechooser.loadPreset.title", "load"),
		executablePreset("filechooser.executablePreset.title", "choose"),
		bookPreset("filechooser.bookFilePreset.title", "choose"),
		bookSavePreset("filechooser.bookFilePreset.title", "save"),
		artifactProjectPreset("filechooser.artifactProjectPreset.title", "choose",
				      false, false, FILES_ONLY),
		exportSignal("filechooser.exportSignal.title", "export"),
		exportBook("filechooser.exportBook.title", "export"),
		readXMLManifest("filechooser.readXMLManifest.title", "read"),
		workingDirectoryPreset("filechooser.workingDirectoryPreset.title", "choose",
				       false, false, DIRECTORIES_ONLY),
		classPathDirectoryPreset("filechooser.classPathDirectoryPreset.title", "choose",
					 false, true, DIRECTORIES_ONLY),
		jarFilePreset("filechooser.jarFilePreset.title", "choose",
			      true, true, FILES_ONLY),
		codeFilePreset("filechooser.codeFilePreset.title", "choose");

		final String titleMessage;
		final String okButtonMessage;
		String title, okButton; /* set through initializer */
		final boolean acceptAllUsed;
		final boolean multiSelectionEnabled;
		final int fileSelectionMode;
		FileFilter[] fileFilters;

		OptionSet(String titleMessage, String okMessage,
			  boolean acceptAllUsed, boolean multiSelectionEnabled,
			  int fileSelectionMode){
			this.titleMessage = titleMessage;
			this.okButtonMessage = okMessage;
			this.acceptAllUsed = acceptAllUsed;
			this.multiSelectionEnabled = multiSelectionEnabled;
			this.fileSelectionMode = fileSelectionMode;
			this.fileFilters = fileFilters;

			logger.debug("added OptionSet: " + this);
		}

		OptionSet(String titleMessage, String okMessage){
			this(titleMessage, okMessage, true, false, FILES_ONLY);
		}

		void use(ViewerFileChooser chooser) {
			chooser.setDialogTitle(title);
			chooser.setApproveButtonText(okButton);
			chooser.setAcceptAllFileFilterUsed(acceptAllUsed);
			chooser.resetChoosableFileFilters();
			chooser.setMultiSelectionEnabled(multiSelectionEnabled);
			chooser.setFileSelectionMode(fileSelectionMode);
			if (fileFilters != null)
				for (int i=fileFilters.length-1; i>= 0; i--)
					chooser.addChoosableFileFilter(fileFilters[i]);
		}

		void initialize(MessageSourceAccessor messageSource) {
			this.title = messageSource.getMessage(titleMessage);
			this.okButton = messageSource.getMessage(okButtonMessage);
		}

		void setFilters(FileFilter ... fileFilters){
			this.fileFilters = fileFilters;
		}
	}
}
