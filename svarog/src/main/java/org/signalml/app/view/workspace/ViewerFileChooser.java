/* ViewerFileChooser.java created 2007-09-13
 *
 */
package org.signalml.app.view.workspace;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.html.Option;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.view.components.EmbeddedFileChooser;
import org.signalml.app.view.components.EmbeddedFileChooserFavorites;
import org.signalml.app.view.components.dialogs.OptionPane;

import static org.signalml.util.Util.capitalize;

/** ViewerFileChooser
 *
 *	Not thread safe! Use from event dispatcher thread only!
 *
 * @author Michał Dobaczewski © 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * @author Zbigniew Jędrzejewski-Szmek © 2010
 */
public class ViewerFileChooser extends JFileChooser implements org.signalml.plugin.export.view.FileChooser {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(ViewerFileChooser.class);

	private ApplicationConfiguration applicationConfig;
	
	public ViewerFileChooser() {
		super();
		this.setPreferredSize(new Dimension(500, 380));
	}

	public void initialize() {
		FileFilter text = new FileNameExtensionFilter(_("Text files (*.txt)"), "txt");
		FileFilter binary = new FileNameExtensionFilter(_("Binary files (*.bin)"), "bin");
		FileFilter xml = new FileNameExtensionFilter(_("XML files (*.xml)"), "xml");
		FileFilter book = new FileNameExtensionFilter(_("Book files (*.b)"), "b");
		FileFilter png = new FileNameExtensionFilter(_("PNG graphic files (*.png)"), "png");
		FileFilter config = new FileNameExtensionFilter(_("Config files (*.cfg)"), "cfg");
		FileFilter exe = new FileNameExtensionFilter(_("Executable files (*.exe)"), "exe");
		FileFilter jar = new FileNameExtensionFilter(_("Jar files (*.jar)"), "jar");
		FileFilter jar_class = new FileNameExtensionFilter(_("Code files (*.java, *.class)"),
								   "java", "class");

		OptionSet.consoleSaveAsText.setFilters(text);
		OptionSet.tableSaveAsText.setFilters(text);
		OptionSet.samplesSaveAsText.setFilters(text);
		OptionSet.samplesSaveAsFloat.setFilters(binary);
		OptionSet.chartSaveAsPng.setFilters(png);
		OptionSet.saveMP5Config.setFilters(config);
		OptionSet.saveMP5Signal.setFilters(binary);

		FileFilter managed[] = ManagedDocumentType.TAG.getFileFilters();
		OptionSet.saveTag.setFilters(managed);
		OptionSet.openTag.setFilters(managed);
		OptionSet.expertTag.setFilters(managed);

		OptionSet.readXMLManifest.setFilters(xml);
		OptionSet.exportSignal.setFilters(binary);
		OptionSet.exportBook.setFilters(book);
		OptionSet.openBook.setFilters(book);
		OptionSet.savePreset.setFilters(xml);
		OptionSet.loadPreset.setFilters(xml);

		if (Pattern.matches(".*[Ww]indows.*", System.getProperty("os.name")))
			OptionSet.executablePreset.setFilters(exe);

		OptionSet.bookFilePreset.setFilters(book);
		OptionSet.bookSavePreset.setFilters(book);

		OptionSet.jarFilePreset.setFilters(jar);
		OptionSet.codeFilePreset.setFilters(jar_class);
	}

	protected synchronized int showDialog(Component parent, OptionSet options) {
		options.use(this);
		return showDialog(parent, options.buttonLabel);
	}

	public synchronized File chooseForReadOrWrite(Component parent, OptionSet options) {
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

	public synchronized File[] chooseFilesOrDirectoriesForRead(Component parent, OptionSet options) {
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

	public synchronized File chooseFile(Component parent, OptionSet optionset, File fileSuggestion) {
		String path = applicationConfig.getPath(optionset.path);
		if (path != null)
			setCurrentDirectory(new File(path));
		if (fileSuggestion == null)
			setSelectedFile(new File(""));
		else
			setSelectedFile(fileSuggestion);

		File file = chooseForReadOrWrite(parent, optionset);
		if (file != null) {
			applicationConfig.setPath(optionset.path,
				getCurrentDirectory().getAbsolutePath());
			if (file.exists() && getAccessory() != null)
				getAccessory().lastDirectoryChanged(file.getParent());
		}
		return file;
	}
	public synchronized File chooseFile(Component parent, OptionSet optionset,
			       File directory, File fileSuggestion) {
		return chooseFile(parent, optionset, fileSuggestion);
	}
	public synchronized File chooseFile(Component parent, OptionSet optionset) {
		return chooseFile(parent, optionset, null);
	}

	public synchronized File[] chooseFiles(Component parent, OptionSet options) {
		setSelectedFile(new File(""));
		String path = applicationConfig.getLastLibraryPath();
		if (path != null)
			setCurrentDirectory(new File(path));

		File[] files = chooseFilesOrDirectoriesForRead(parent, options);
		if (files != null)
			applicationConfig.setLastLibraryPath(getCurrentDirectory().getAbsolutePath());
		return files;
	}

	public synchronized File chooseConsoleSaveAsTextFile(Component parent) {
		return chooseFile(parent, OptionSet.consoleSaveAsText);
	}

	public synchronized File chooseTableSaveAsTextFile(Component parent) {
		return chooseFile(parent, OptionSet.tableSaveAsText);
	}

	public synchronized File chooseSamplesSaveAsTextFile(Component parent) {
		return chooseFile(parent, OptionSet.samplesSaveAsText);
	}

	public synchronized File chooseSamplesSaveAsFloatFile(Component parent) {
		return chooseFile(parent, OptionSet.samplesSaveAsFloat);
	}

	public synchronized File chooseExportSignalFile(Component parent, File fileSuggestion) {
		return chooseFile(parent, OptionSet.exportSignal, fileSuggestion);
	}

	public synchronized File chooseExportBookFile(Component parent, File fileSuggestion) {
		return chooseFile(parent, OptionSet.exportBook, fileSuggestion);
	}

	public synchronized File chooseChartSaveAsPngFile(Component parent) {
		return chooseFile(parent, OptionSet.chartSaveAsPng);
	}

	public synchronized File chooseSaveMP5ConfigFile(Component parent) {
		return chooseFile(parent, OptionSet.saveMP5Config);
	}

	public synchronized File chooseSaveMP5SignalFile(Component parent) {
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

	public synchronized File chooseSaveDocument(Component parent, FileFilter[] filters) {
		// XXX: filters?
		return chooseFile(parent, OptionSet.saveDocument);
	}

	public synchronized File chooseSaveTag(Component parent) {
		return chooseFile(parent, OptionSet.saveTag);
	}

	public synchronized File chooseOpenTag(Component parent) {
		return chooseFile(parent, OptionSet.openTag);
	}

	public synchronized File chooseExpertTag(Component parent) {
		return chooseFile(parent, OptionSet.expertTag);
	}

	public synchronized File chooseImportTag(Component parent) {
		return chooseFile(parent, OptionSet.importTag);
	}

	public synchronized File chooseExportTag(Component parent) {
		return chooseFile(parent, OptionSet.exportTag);
	}
	
	public synchronized File chooseOpenBook(Component parent) {
		return chooseFile(parent, OptionSet.openBook);
	}

	public synchronized File chooseReadXMLManifest(File directory, File fileSuggestion, Component parent) {
		return chooseFile(parent, OptionSet.readXMLManifest,
				  fileSuggestion, directory);
	}

	public synchronized File chooseSavePresetFile(Component parent) {
		return chooseFile(parent, OptionSet.savePreset);
	}

	public synchronized File chooseLoadPresetFile(Component parent) {
		return chooseFile(parent, OptionSet.loadPreset);
	}

	public synchronized File chooseArtifactProjectDirectory(Component parent) {
		return chooseFile(parent, OptionSet.artifactProjectPreset);
	}

	public synchronized File chooseExecutableFile(Component parent) {
		setSelectedFile(new File(""));
		setCurrentDirectory(new File(System.getProperty("user.dir")));

		return chooseForReadOrWrite(parent, OptionSet.executablePreset);
	}

	public synchronized File chooseBookFile(Component parent) {
		return chooseFile(parent, OptionSet.bookFilePreset);
	}

	public synchronized File chooseBookFileForWrite(Component parent) {
		return chooseFile(parent, OptionSet.bookSavePreset);
	}

	public synchronized File chooseWorkingDirectory(Component parent, File currentDirectory) {
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

	public synchronized File[] chooseClassPathDirectories(Component parent) {
		return chooseFiles(parent, OptionSet.classPathDirectoryPreset);
	}

	public synchronized File[] chooseJarFiles(Component parent) {
		return chooseFiles(parent, OptionSet.classPathDirectoryPreset);
	}

	public synchronized File chooseCodeFile(Component parent) {
		return chooseFile(parent, OptionSet.codeFilePreset);
	}

	public synchronized ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	protected synchronized void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
		EmbeddedFileChooserFavorites f = new EmbeddedFileChooserFavorites(this);
		this.setAccessory(f);
	}

	@Override
	public EmbeddedFileChooserFavorites getAccessory(){
		return (EmbeddedFileChooserFavorites) super.getAccessory();
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
		consoleSaveAsText(Operation.save, _("Choose text file to save"),
				  null, _("Save")),
		tableSaveAsText(Operation.save, _("Choose text file to save"),
				null, _("Save")),
		samplesSaveAsText(Operation.save, _("Choose text file to save"),
				  null, _("Save")),
		samplesSaveAsFloat(Operation.save, _("Choose binary file to save"),
				   null, _("Save")),
		chartSaveAsPng(Operation.save, _("Choose png file to save"),
			       null, _("Save")),
		saveMP5Config(Operation.save, _("Choose MP5 config file to save"),
			      null, _("Save")),
		saveMP5Signal(Operation.save, _("Choose MP5 signal file to save"),
			      null, _("Save")),
		saveDocument(Operation.save, _("Save document"),
			     null, _("Save")),
		saveTag(Operation.save, _("Save tag"),
			null, _("Save")),
		openTag(Operation.open, _("Open tag"),
			null, _("Open")),
		expertTag(Operation.open, _("Choose expert tag"),
			  null, _("Choose")),
		importTag(Operation.open, _("Import legacy tag"),
			  null, _("Import")),
		exportTag(Operation.save, _("Export legacy tag"),
			  null, _("Export")),
		openBook(Operation.open, _("Choose a book file"),
		      null, _("Choose")),
		savePreset(Operation.save, _("Save preset to file"),
			   "LastPresetPath", _("Save")),
		loadPreset(Operation.open, _("Load preset from file"),
			   "LastPresetPath", _("Load")),
		executablePreset(Operation.open, _("Choose executable"),
				 null, _("Choose")),
		bookFilePreset(Operation.open, _("Choose a book file"),
			       null, _("Choose")),
		bookSavePreset(Operation.save, _("Choose a book file"),
			       null, _("Save")),
		artifactProjectPreset(Operation.usedir, "filechooser.artifactProjectPreset.title",
				      null, _("Choose"),
				      false, false, FILES_ONLY),
		exportSignal(Operation.save, _("Export binary signal"),
			     null, _("Export")),
		exportBook(Operation.save, _("Export book"),
			   null, _("Export")),
		readXMLManifest(Operation.open, _("Read XML manifest"),
				null, _("Read")),
		workingDirectoryPreset(Operation.usedir, _("Choose working directory"),
				       null, _("Choose"),
				       false, false, DIRECTORIES_ONLY),
		classPathDirectoryPreset(Operation.open, _("Choose class path directories"),
					 null, _("Choose"),
					 false, true, DIRECTORIES_ONLY),
		jarFilePreset(Operation.open, _("Choose jar files"),
			      null, _("Choose"),
			      true, true, FILES_ONLY),
		codeFilePreset(Operation.open, _("Choose code file"),
			       "LastLibraryPath" /* sic */, _("Choose"));

		final Operation operation;
		final String title, buttonLabel;
		final String path;
		final boolean acceptAllUsed;
		final boolean multiSelectionEnabled;
		final int fileSelectionMode;
		FileFilter[] fileFilters;

		private OptionSet(Operation operation,
				  String title, String path, String buttonLabel,
				  boolean acceptAllUsed, boolean multiSelectionEnabled,
				  int fileSelectionMode) {
			this.operation = operation;
			this.title = title;
			this.buttonLabel = buttonLabel;
			this.path = path;
			this.acceptAllUsed = acceptAllUsed;
			this.multiSelectionEnabled = multiSelectionEnabled;
			this.fileSelectionMode = fileSelectionMode;
			this.fileFilters = fileFilters;
			logger.debug("added OptionSet \"" + title + "\" (" +
				     operation.getClass().getSimpleName() +
				     " \"" + buttonLabel + "\" / " + path + ")");
		}
		private OptionSet(Operation operation,
				  String title, String path, String buttonLabel) {
			this(operation, title, path, buttonLabel, true, false, FILES_ONLY);
		}

		void use(ViewerFileChooser chooser) {
			chooser.setDialogTitle(this.title);
			chooser.setApproveButtonText(this.buttonLabel);
			chooser.setAcceptAllFileFilterUsed(this.acceptAllUsed);
			chooser.resetChoosableFileFilters();
			chooser.setMultiSelectionEnabled(this.multiSelectionEnabled);
			chooser.setFileSelectionMode(this.fileSelectionMode);
			if (this.fileFilters != null)
				for (int i=this.fileFilters.length-1; i>= 0; i--)
					chooser.addChoosableFileFilter(this.fileFilters[i]);
		}

		void setFilters(FileFilter ... fileFilters){
			this.fileFilters = fileFilters;
		}
	}
}
