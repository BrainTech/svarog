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

	public File chooseForReadOrWrite(Component parent, OptionSet options) {
		File file = null;
		boolean hasFile = false;

		do {
			int result = showDialog(parent, options);
			if (result == APPROVE_OPTION)
				file = getSelectedFile();
			else
				break;

			boolean good = options.operation.verify(parent, file);
			if (!good) {
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

	public File chooseFile(Component parent,
			       OptionSet optionset, String savename,
			       File fileSuggestion) {
		String path = applicationConfig.getPath(savename);
		if (path != null)
			setCurrentDirectory(new File(path));
		if (fileSuggestion == null)
			setSelectedFile(new File(""));
		else
			setSelectedFile(fileSuggestion);

		File file = chooseForReadOrWrite(parent, optionset);
		if (file != null)
			applicationConfig.setPath(savename,
				getCurrentDirectory().getAbsolutePath());
		return file;
	}
	public File chooseFile(Component parent,
			       OptionSet optionset, String savename) {
		return chooseFile(parent, optionset, savename, null);
	}

	public File chooseConsoleSaveAsTextFile(Component parent) {
		return chooseFile(parent, OptionSet.consoleSaveAsText,
				  "LastSamplesSaveAsTextPath");
	}

	public File chooseTableSaveAsTextFile(Component parent) {
		return chooseFile(parent, OptionSet.tableSaveAsText,
				  "LastTableSaveAsTextPath");
	}

	public File chooseSamplesSaveAsTextFile(Component parent) {
		return chooseFile(parent, OptionSet.samplesSaveAsText,
				  "LastSamplesSaveAsTextFile");
	}

	public File chooseSamplesSaveAsFloatFile(Component parent) {
		return chooseFile(parent, OptionSet.samplesSaveAsFloat,
				  "LastSamplesSaveAsFloatPath");
	}

	public File chooseExportSignalFile(Component parent, File fileSuggestion) {
		return chooseFile(parent, OptionSet.exportSignal,
				  "LastExportSignalPath");
	}

	public File chooseExportBookFile(Component parent, File fileSuggestion) {
		return chooseFile(parent, OptionSet.exportBook,
				  "LastExportBookPath");
	}

	public File chooseChartSaveAsPngFile(Component parent) {
		return chooseFile(parent, OptionSet.chartSaveAsPng,
				  "LastChartSaveAsPngPath");
	}

	public File chooseSaveMP5ConfigFile(Component parent) {
		return chooseFile(parent, OptionSet.saveMP5Config,
				  "LastSaveMP5ConfigPath");
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

		file = chooseForReadOrWrite(parent, OptionSet.saveMP5Signal);
		if (file != null && resetDir) {
			applicationConfig.setLastSaveMP5ConfigPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;
	}

	public File chooseSaveDocument(Component parent, FileFilter[] filters) {
		return chooseFile(parent, OptionSet.saveDocument,
				  "LastSaveDocumentPath");
	}

	public File chooseSaveTag(Component parent) {
		return chooseFile(parent, OptionSet.saveTag,
				  "LastSaveTagPath");
	}

	public File chooseOpenTag(Component parent) {
		return chooseFile(parent, OptionSet.openTag,
				  "LastOpenTagPath");
	}

	public File chooseExpertTag(Component parent) {
		return chooseFile(parent, OptionSet.expertTag,
				  "LastExpertTagPath");
	}

	public File chooseImportTag(Component parent) {
		return chooseFile(parent, OptionSet.importTag,
				  "LastImportTagPath");
	}

	public File chooseExportTag(Component parent) {
		return chooseFile(parent, OptionSet.exportTag,
				  "LastExportTagPath");
	}

	public File chooseReadXMLManifest(File directory, File fileSuggestion, Component parent) {

		File file = null;

		setCurrentDirectory(directory);
		if (fileSuggestion != null && fileSuggestion.exists()) {
			setSelectedFile(fileSuggestion);
		} else {
			setSelectedFile(new File(""));
		}

		file = chooseForReadOrWrite(parent, OptionSet.readXMLManifest);

		return file;

	}

	public File chooseSavePresetFile(Component parent) {
		return chooseFile(parent, OptionSet.savePreset,
				  "LastPresetPath");
	}

	public File chooseLoadPresetFile(Component parent) {
		return chooseFile(parent, OptionSet.loadPreset,
				  "LastPresetPath");
	}

	public File chooseArtifactProjectDirectory(Component parent) {
		return chooseFile(parent, OptionSet.artifactProjectPreset,
				  "LastArtifactProjectPath");
	}

	public File chooseExecutableFile(Component parent) {
		setSelectedFile(new File(""));
		setCurrentDirectory(new File(System.getProperty("user.dir")));

		return chooseForReadOrWrite(parent, OptionSet.executablePreset);
	}

	public File chooseBookFile(Component parent) {
		return chooseFile(parent, OptionSet.bookPreset,
				  "LastBookFilePath");
	}

	public File chooseBookFileForWrite(Component parent) {
		return chooseFile(parent, OptionSet.bookSavePreset,
				  "LastBookFilePath");
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

		file = chooseForReadOrWrite(parent, OptionSet.workingDirectoryPreset);

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
		return chooseFile(parent, OptionSet.codeFilePreset,
				  "LastLibraryPath");
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

	protected static abstract class Operation {
		abstract boolean verify(Component parent, File file);

		private static class Open extends Operation {
			@Override
			boolean verify(Component parent, File file) {
				boolean good = file.exists() && file.canRead();
				if (!good)
					OptionPane.showFileNotFound(parent, file);
				return good;
			}
		}

		private static class Save extends Operation {
			@Override
			boolean verify(Component parent, File file) {
				File dir = file.getParentFile();
				boolean good = dir != null &&
					dir.exists() && dir.canRead() && dir.canWrite();
				// XXX: when dir == null?
				if (!good)
					OptionPane.showDirectoryNotFound(parent, dir);
				return good;
			}
		}

		private static class Execute extends Operation {
			@Override
			boolean verify(Component parent, File file) {
				boolean good = file.exists() && file.canRead()
					&& file.canExecute();
				if (!good)
					OptionPane.showFileNotFound(parent, file);
				return good;
			}
		}

		private static class UseDirectory extends Operation {
			@Override
			boolean verify(Component parent, File file) {
				if (!file.exists()) {
					int ans = OptionPane.showDirectoryDoesntExistCreate(parent, file);
					if (ans != OptionPane.YES_OPTION)
						return false;

					boolean createOk = file.mkdirs();
					if (!createOk) {
						OptionPane.showDirectoryNotCreated(parent, file);
						return false;
					}
				}

				boolean good = file.canRead() && file.canWrite();
				if (!good)
					OptionPane.showDirectoryNotAccessible(parent, file);
				return good;
			}
		}

		static Operation
			open = new Open(),
			save = new Save(),
			execute = new Execute(),
			usedir = new UseDirectory();
	}

	protected enum OptionSet {
		consoleSaveAsText(Operation.save, "filechooser.consoleSaveAsText.title", "save"),
		tableSaveAsText(Operation.save, "filechooser.tableSaveAsText.title", "save"),
		samplesSaveAsText(Operation.save, "filechooser.samplesSaveAsText.title", "save"),
		samplesSaveAsFloat(Operation.save, "filechooser.samplesSaveAsFloat.title", "save"),
		chartSaveAsPng(Operation.save, "filechooser.chartSaveAsPng.title", "save"),
		saveMP5Config(Operation.save, "filechooser.saveMP5Config.title", "save"),
		saveMP5Signal(Operation.save, "filechooser.saveMP5Signal.title", "save"),
		saveDocument(Operation.save, "filechooser.saveDocument.title", "save"),
		saveTag(Operation.save, "filechooser.saveTag.title", "save"),
		openTag(Operation.open, "filechooser.openTag.title", "open"),
		expertTag(Operation.open, "filechooser.expertTag.title", "choose"),
		importTag(Operation.open, "filechooser.importTag.title", "import"),
		exportTag(Operation.save, "filechooser.exportTag.title", "export"),
		savePreset(Operation.save, "filechooser.savePreset.title", "save"),
		loadPreset(Operation.open, "filechooser.loadPreset.title", "load"),
		executablePreset(Operation.open, "filechooser.executablePreset.title", "choose"),
		bookPreset(Operation.open, "filechooser.bookFilePreset.title", "choose"),
		bookSavePreset(Operation.save, "filechooser.bookFilePreset.title", "save"),
		artifactProjectPreset(Operation.usedir, "filechooser.artifactProjectPreset.title", "choose",
				      false, false, FILES_ONLY),
		exportSignal(Operation.save, "filechooser.exportSignal.title", "export"),
		exportBook(Operation.save, "filechooser.exportBook.title", "export"),
		readXMLManifest(Operation.open, "filechooser.readXMLManifest.title", "read"),
		workingDirectoryPreset(Operation.usedir, "filechooser.workingDirectoryPreset.title", "choose",
				       false, false, DIRECTORIES_ONLY),
		classPathDirectoryPreset(Operation.open, "filechooser.classPathDirectoryPreset.title", "choose",
					 false, true, DIRECTORIES_ONLY),
		jarFilePreset(Operation.open, "filechooser.jarFilePreset.title", "choose",
			      true, true, FILES_ONLY),
		codeFilePreset(Operation.open, "filechooser.codeFilePreset.title", "choose");

		final Operation operation;
		final String titleMessage;
		final String okButtonMessage;
		String title, okButton; /* set through initializer */
		final boolean acceptAllUsed;
		final boolean multiSelectionEnabled;
		final int fileSelectionMode;
		FileFilter[] fileFilters;

		OptionSet(Operation operation,
			  String titleMessage, String okMessage,
			  boolean acceptAllUsed, boolean multiSelectionEnabled,
			  int fileSelectionMode){
			this.operation = operation;
			this.titleMessage = titleMessage;
			this.okButtonMessage = okMessage;
			this.acceptAllUsed = acceptAllUsed;
			this.multiSelectionEnabled = multiSelectionEnabled;
			this.fileSelectionMode = fileSelectionMode;
			this.fileFilters = fileFilters;
			logger.debug("added OptionSet: " + this + "/" + this.operation);
		}

		OptionSet(Operation operation,
			  String titleMessage, String okMessage){
			this(operation, titleMessage, okMessage, true, false, FILES_ONLY);
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
