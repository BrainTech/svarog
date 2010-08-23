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
import static org.signalml.util.Util.capitalize;

/** ViewerFileChooser
 *
 *	Not thread safe! Use from event dispatcher thread only!
 *
 * @author Michał Dobaczewski © 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * @author Zbigniew Jędrzejewski-Szmek © 2010
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

		OptionSet.bookFilePreset.setFilters(book);
		OptionSet.bookSavePreset.setFilters(book);

		OptionSet.jarFilePreset.setFilters(jar);
		OptionSet.codeFilePreset.setFilters(jar_class);
	}

	public int showDialog(Component parent, OptionSet options) {
		options.use(this);
		return showDialog(parent, options.okButton);
	}

	public File chooseForReadOrWrite(Component parent, OptionSet options) {
		do {
			int result = showDialog(parent, options);
			if (result != APPROVE_OPTION)
				return null;

			File file = getSelectedFile();
			boolean good = options.operation.verify(parent, file);
			if (!good)
				continue;

			return file;
		} while (true);
	}

	public File[] chooseFilesOrDirectoriesForRead(Component parent, OptionSet options) {
		int result = showDialog(parent, options);
		if (result != APPROVE_OPTION)
			return null;

		File[] files = getSelectedFiles();
		File[] okFiles = new File[files.length];
		int cnt = 0;

		for (File file : files) {
			boolean good = options.operation.verify(parent, file);
			if (!good)
				continue;

			okFiles[cnt] = file;
			cnt++;
		}

		okFiles = Arrays.copyOf(okFiles, cnt);
		return okFiles;
	}

	public File chooseFile(Component parent,
			       OptionSet optionset,
			       File fileSuggestion) {
		String path = applicationConfig.getPath(optionset.path);
		if (path != null)
			setCurrentDirectory(new File(path));
		if (fileSuggestion == null)
			setSelectedFile(new File(""));
		else
			setSelectedFile(fileSuggestion);

		File file = chooseForReadOrWrite(parent, optionset);
		if (file != null)
			applicationConfig.setPath(optionset.path,
				getCurrentDirectory().getAbsolutePath());
		return file;
	}
	public File chooseFile(Component parent,
			       OptionSet optionset) {
		return chooseFile(parent, optionset, null);
	}

	public File[] chooseFiles(Component parent, OptionSet options) {
		setSelectedFile(new File(""));
		String path = applicationConfig.getLastLibraryPath();
		if (path != null)
			setCurrentDirectory(new File(path));

		File[] files = chooseFilesOrDirectoriesForRead(parent, options);
		if (files != null)
			applicationConfig.setLastLibraryPath(getCurrentDirectory().getAbsolutePath());
		return files;
	}

	public File chooseConsoleSaveAsTextFile(Component parent) {
		return chooseFile(parent, OptionSet.consoleSaveAsText);
	}

	public File chooseTableSaveAsTextFile(Component parent) {
		return chooseFile(parent, OptionSet.tableSaveAsText);
	}

	public File chooseSamplesSaveAsTextFile(Component parent) {
		return chooseFile(parent, OptionSet.samplesSaveAsText);
	}

	public File chooseSamplesSaveAsFloatFile(Component parent) {
		return chooseFile(parent, OptionSet.samplesSaveAsFloat);
	}

	public File chooseExportSignalFile(Component parent, File fileSuggestion) {
		return chooseFile(parent, OptionSet.exportSignal, fileSuggestion);
	}

	public File chooseExportBookFile(Component parent, File fileSuggestion) {
		return chooseFile(parent, OptionSet.exportBook, fileSuggestion);
	}

	public File chooseChartSaveAsPngFile(Component parent) {
		return chooseFile(parent, OptionSet.chartSaveAsPng);
	}

	public File chooseSaveMP5ConfigFile(Component parent) {
		return chooseFile(parent, OptionSet.saveMP5Config);
	}

	public File chooseSaveMP5SignalFile(Component parent) {
		File selFile = getSelectedFile();
		boolean resetDir = false;
		if (selFile == null || selFile.getName().isEmpty()) {
			String path = applicationConfig.getLastSaveMP5ConfigPath();
			if (path != null) {
				setCurrentDirectory(new File(path));
				resetDir = true;
			}
		}

		File file = chooseForReadOrWrite(parent, OptionSet.saveMP5Signal);
		if (file != null && resetDir)
			applicationConfig.setLastSaveMP5ConfigPath(getCurrentDirectory().getAbsolutePath());

		return file;
	}

	public File chooseSaveDocument(Component parent, FileFilter[] filters) {
		// XXX: filters?
		return chooseFile(parent, OptionSet.saveDocument);
	}

	public File chooseSaveTag(Component parent) {
		return chooseFile(parent, OptionSet.saveTag);
	}

	public File chooseOpenTag(Component parent) {
		return chooseFile(parent, OptionSet.openTag);
	}

	public File chooseExpertTag(Component parent) {
		return chooseFile(parent, OptionSet.expertTag);
	}

	public File chooseImportTag(Component parent) {
		return chooseFile(parent, OptionSet.importTag);
	}

	public File chooseExportTag(Component parent) {
		return chooseFile(parent, OptionSet.exportTag);
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
		return chooseFile(parent, OptionSet.savePreset);
	}

	public File chooseLoadPresetFile(Component parent) {
		return chooseFile(parent, OptionSet.loadPreset);
	}

	public File chooseArtifactProjectDirectory(Component parent) {
		return chooseFile(parent, OptionSet.artifactProjectPreset);
	}

	public File chooseExecutableFile(Component parent) {
		setSelectedFile(new File(""));
		setCurrentDirectory(new File(System.getProperty("user.dir")));

		return chooseForReadOrWrite(parent, OptionSet.executablePreset);
	}

	public File chooseBookFile(Component parent) {
		return chooseFile(parent, OptionSet.bookFilePreset);
	}

	public File chooseBookFileForWrite(Component parent) {
		return chooseFile(parent, OptionSet.bookSavePreset);
	}

	public File chooseWorkingDirectory(Component parent, File currentDirectory) {
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

		File file = chooseForReadOrWrite(parent, OptionSet.workingDirectoryPreset);
		return file;
	}

	public File[] chooseClassPathDirectories(Component parent) {
		return chooseFiles(parent, OptionSet.classPathDirectoryPreset);
	}

	public File[] chooseJarFiles(Component parent) {
		return chooseFiles(parent, OptionSet.classPathDirectoryPreset);
	}

	public File chooseCodeFile(Component parent) {
		return chooseFile(parent, OptionSet.codeFilePreset);
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
		consoleSaveAsText(Operation.save, "save", "LastSamplesSaveAsTextPath"),
		tableSaveAsText(Operation.save, "save"),
		samplesSaveAsText(Operation.save, "save"),
		samplesSaveAsFloat(Operation.save, "save"),
		chartSaveAsPng(Operation.save, "save"),
		saveMP5Config(Operation.save, "save"),
		saveMP5Signal(Operation.save, "save", "LastSaveMP5ConfigPath"),
		saveDocument(Operation.save, "save"),
		saveTag(Operation.save, "save"),
		openTag(Operation.open, "open"),
		expertTag(Operation.open, "choose"),
		importTag(Operation.open, "import"),
		exportTag(Operation.save, "export"),
		savePreset(Operation.save, "save"),
		loadPreset(Operation.open, "load"),
		executablePreset(Operation.open, "choose", null),
		bookFilePreset(Operation.open, "choose", "LastBookFilePath"),
		bookSavePreset(Operation.save, "save", "LastBookFilePath"),
		artifactProjectPreset(Operation.usedir, "choose",
				      "LastArtifactProjectPath",
				      false, false, FILES_ONLY),
		exportSignal(Operation.save, "export"),
		exportBook(Operation.save, "export"),
		readXMLManifest(Operation.open, "read", null),
		workingDirectoryPreset(Operation.usedir,
				       "choose", null,
				       false, false, DIRECTORIES_ONLY),
		classPathDirectoryPreset(Operation.open,
					 "choose", null,
					 false, true, DIRECTORIES_ONLY),
		jarFilePreset(Operation.open,
			      "choose", null,
			      true, true, FILES_ONLY),
		codeFilePreset(Operation.open, "choose", "LastLibraryPath");

		final Operation operation;
		final String okButtonMessage;
		String title, okButton; /* set through initializer */
		final String path;
		final boolean acceptAllUsed;
		final boolean multiSelectionEnabled;
		final int fileSelectionMode;
		FileFilter[] fileFilters;

		private OptionSet(Operation operation, String okMessage, String path,
			  boolean acceptAllUsed, boolean multiSelectionEnabled,
			  int fileSelectionMode) {
			this.operation = operation;
			this.okButtonMessage = okMessage;
			if ("_gen".equals(path))
				path = "Last" + capitalize(this.name()) + "Path";
			this.path = path;
			this.acceptAllUsed = acceptAllUsed;
			this.multiSelectionEnabled = multiSelectionEnabled;
			this.fileSelectionMode = fileSelectionMode;
			this.fileFilters = fileFilters;
			logger.debug("added OptionSet: " + this + " / " + operation.getClass().getSimpleName());;
		}
		private OptionSet(Operation operation, String okMessage, String path) {
			this(operation, okMessage, path, true, false, FILES_ONLY);
		}
		private OptionSet(Operation operation, String okMessage) {
			this(operation, okMessage, "_gen");
			/* note: "_gen" is used instead of e.g. null,
			   because  null is used for something else
			*/
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
			String name = "filechooser." + this.name() + ".title";
			this.title = messageSource.getMessage(name);
			this.okButton = messageSource.getMessage(okButtonMessage);
		}

		void setFilters(FileFilter ... fileFilters){
			this.fileFilters = fileFilters;
		}
	}
}
