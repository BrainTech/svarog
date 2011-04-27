/* ViewerFileChooser.java created 2007-09-13
 *
 */
package org.signalml.app.view;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;

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

	private OptionSet consoleSaveAsText;
	private OptionSet tableSaveAsText;
	private OptionSet samplesSaveAsText;
	private OptionSet samplesSaveAsFloat;
	private OptionSet chartSaveAsPng;
	private OptionSet saveMP5Config;
	private OptionSet saveMP5Signal;
	private OptionSet saveDocument;
	private OptionSet saveTag;
	private OptionSet openTag;
	private OptionSet expertTag;
	private OptionSet importTag;
	private OptionSet exportTag;
	private OptionSet savePreset;
	private OptionSet loadPreset;
	private OptionSet executablePreset;
	private OptionSet bookPreset;
	private OptionSet bookSavePreset;
	private OptionSet artifactProjectPreset;
	private OptionSet exportSignal;
	private OptionSet exportBook;
	private OptionSet readXMLManifest;
	private OptionSet workingDirectoryPreset;
	private OptionSet classPathDirectoryPreset;
	private OptionSet jarFilePreset;
	private OptionSet codeFilePreset;

	private MessageSourceAccessor messageSource;
	private ApplicationConfiguration applicationConfig;

	public void initialize() {

		Vector<FileFilter> filters = new Vector<FileFilter>(20);

		/* save console as text */
		consoleSaveAsText = new OptionSet();
		consoleSaveAsText.title = messageSource.getMessage("filechooser.consoleSaveAsText.title");
		consoleSaveAsText.okButton = messageSource.getMessage("save");
		consoleSaveAsText.acceptAllUsed = true;
		consoleSaveAsText.multiSelectionEnabled = false;
		consoleSaveAsText.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.textFiles"), "txt"));

		consoleSaveAsText.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		/* save table as text */
		tableSaveAsText = new OptionSet();
		tableSaveAsText.title = messageSource.getMessage("filechooser.tableSaveAsText.title");
		tableSaveAsText.okButton = messageSource.getMessage("save");
		tableSaveAsText.acceptAllUsed = true;
		tableSaveAsText.multiSelectionEnabled = false;
		tableSaveAsText.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.textFiles"), "txt"));

		tableSaveAsText.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		/* save samples as text */
		samplesSaveAsText = new OptionSet();
		samplesSaveAsText.title = messageSource.getMessage("filechooser.samplesSaveAsText.title");
		samplesSaveAsText.okButton = messageSource.getMessage("save");
		samplesSaveAsText.acceptAllUsed = true;
		samplesSaveAsText.multiSelectionEnabled = false;
		samplesSaveAsText.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.textFiles"), "txt"));

		samplesSaveAsText.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		/* save samples as text */
		samplesSaveAsFloat = new OptionSet();
		samplesSaveAsFloat.title = messageSource.getMessage("filechooser.samplesSaveAsFloat.title");
		samplesSaveAsFloat.okButton = messageSource.getMessage("save");
		samplesSaveAsFloat.acceptAllUsed = true;
		samplesSaveAsFloat.multiSelectionEnabled = false;
		samplesSaveAsFloat.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.binaryFiles"), "bin"));

		samplesSaveAsFloat.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		/* save chart as png */
		chartSaveAsPng = new OptionSet();
		chartSaveAsPng.title = messageSource.getMessage("filechooser.chartSaveAsPng.title");
		chartSaveAsPng.okButton = messageSource.getMessage("save");
		chartSaveAsPng.acceptAllUsed = true;
		chartSaveAsPng.multiSelectionEnabled = false;
		chartSaveAsPng.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.pngFiles"), "png"));

		chartSaveAsPng.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		/* save MP5 config */
		saveMP5Config = new OptionSet();
		saveMP5Config.title = messageSource.getMessage("filechooser.saveMP5Config.title");
		saveMP5Config.okButton = messageSource.getMessage("save");
		saveMP5Config.acceptAllUsed = true;
		saveMP5Config.multiSelectionEnabled = false;
		saveMP5Config.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.configFiles"), "cfg"));

		saveMP5Config.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		/* save MP5 config */
		saveMP5Signal = new OptionSet();
		saveMP5Signal.title = messageSource.getMessage("filechooser.saveMP5Signal.title");
		saveMP5Signal.okButton = messageSource.getMessage("save");
		saveMP5Signal.acceptAllUsed = true;
		saveMP5Signal.multiSelectionEnabled = false;
		saveMP5Signal.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.binaryFiles"), "bin"));

		saveMP5Signal.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		/* save document */
		saveDocument = new OptionSet();
		saveDocument.title = messageSource.getMessage("filechooser.saveDocument.title");
		saveDocument.okButton = messageSource.getMessage("save");
		saveDocument.acceptAllUsed = true;
		saveDocument.multiSelectionEnabled = false;
		saveDocument.fileSelectionMode = FILES_ONLY;

		/* save tag */
		saveTag = new OptionSet();
		saveTag.title = messageSource.getMessage("filechooser.saveTag.title");
		saveTag.okButton = messageSource.getMessage("save");
		saveTag.acceptAllUsed = true;
		saveTag.multiSelectionEnabled = false;
		saveTag.fileSelectionMode = FILES_ONLY;

		saveTag.fileFilters = ManagedDocumentType.TAG.getFileFilters(messageSource);

		/* open tag */
		openTag = new OptionSet();
		openTag.title = messageSource.getMessage("filechooser.openTag.title");
		openTag.okButton = messageSource.getMessage("open");
		openTag.acceptAllUsed = true;
		openTag.multiSelectionEnabled = false;
		openTag.fileSelectionMode = FILES_ONLY;

		openTag.fileFilters = ManagedDocumentType.TAG.getFileFilters(messageSource);

		/* expert tag */
		expertTag = new OptionSet();
		expertTag.title = messageSource.getMessage("filechooser.expertTag.title");
		expertTag.okButton = messageSource.getMessage("choose");
		expertTag.acceptAllUsed = true;
		expertTag.multiSelectionEnabled = false;
		expertTag.fileSelectionMode = FILES_ONLY;

		expertTag.fileFilters = ManagedDocumentType.TAG.getFileFilters(messageSource);

		/* import tag */
		importTag = new OptionSet();
		importTag.title = messageSource.getMessage("filechooser.importTag.title");
		importTag.okButton = messageSource.getMessage("import");
		importTag.acceptAllUsed = true;
		importTag.multiSelectionEnabled = false;
		importTag.fileSelectionMode = FILES_ONLY;

		/* read XML manifest */
		readXMLManifest = new OptionSet();
		readXMLManifest.title = messageSource.getMessage("filechooser.readXMLManifest.title");
		readXMLManifest.okButton = messageSource.getMessage("read");
		readXMLManifest.acceptAllUsed = true;
		readXMLManifest.multiSelectionEnabled = false;
		readXMLManifest.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.xmlFiles"), "xml"));

		readXMLManifest.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		/* export tag */
		exportTag = new OptionSet();
		exportTag.title = messageSource.getMessage("filechooser.exportTag.title");
		exportTag.okButton = messageSource.getMessage("export");
		exportTag.acceptAllUsed = true;
		exportTag.multiSelectionEnabled = false;
		exportTag.fileSelectionMode = FILES_ONLY;

		/* export signal */
		exportSignal = new OptionSet();
		exportSignal.title = messageSource.getMessage("filechooser.exportSignal.title");
		exportSignal.okButton = messageSource.getMessage("export");
		exportSignal.acceptAllUsed = true;
		exportSignal.multiSelectionEnabled = false;
		exportSignal.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.binaryFiles"), "bin"));

		exportSignal.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		/* export book */
		exportBook = new OptionSet();
		exportBook.title = messageSource.getMessage("filechooser.exportBook.title");
		exportBook.okButton = messageSource.getMessage("export");
		exportBook.acceptAllUsed = true;
		exportBook.multiSelectionEnabled = false;
		exportBook.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.bookFiles"), "b"));

		exportBook.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		/* save & load preset */
		savePreset = new OptionSet();
		savePreset.title = messageSource.getMessage("filechooser.savePreset.title");
		savePreset.okButton = messageSource.getMessage("save");
		savePreset.acceptAllUsed = true;
		savePreset.multiSelectionEnabled = false;
		savePreset.fileSelectionMode = FILES_ONLY;

		loadPreset = new OptionSet();
		loadPreset.title = messageSource.getMessage("filechooser.loadPreset.title");
		loadPreset.okButton = messageSource.getMessage("load");
		loadPreset.acceptAllUsed = true;
		loadPreset.multiSelectionEnabled = false;
		loadPreset.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.xmlFiles"), "xml"));

		savePreset.fileFilters = filters.toArray(new FileFilter[filters.size()]);
		loadPreset.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		executablePreset = new OptionSet();
		executablePreset.title = messageSource.getMessage("filechooser.executablePreset.title");
		executablePreset.okButton = messageSource.getMessage("choose");
		executablePreset.acceptAllUsed = true;
		executablePreset.multiSelectionEnabled = false;
		executablePreset.fileSelectionMode = FILES_ONLY;

		filters.clear();
		if (Pattern.matches(".*[Ww]indows.*", System.getProperty("os.name"))) {
			filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.exeFiles"), "exe"));
		}

		executablePreset.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		bookPreset = new OptionSet();
		bookPreset.title = messageSource.getMessage("filechooser.bookFilePreset.title");
		bookPreset.okButton = messageSource.getMessage("choose");
		bookPreset.acceptAllUsed = true;
		bookPreset.multiSelectionEnabled = false;
		bookPreset.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.bookFiles"), "b"));

		bookPreset.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		bookSavePreset = new OptionSet();
		bookSavePreset.title = messageSource.getMessage("filechooser.bookSavePreset.title");
		bookSavePreset.okButton = messageSource.getMessage("save");
		bookSavePreset.acceptAllUsed = true;
		bookSavePreset.multiSelectionEnabled = false;
		bookSavePreset.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.bookFiles"), "b"));

		bookSavePreset.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		artifactProjectPreset = new OptionSet();
		artifactProjectPreset.title = messageSource.getMessage("filechooser.artifactProjectPreset.title");
		artifactProjectPreset.okButton = messageSource.getMessage("choose");
		artifactProjectPreset.acceptAllUsed = false;
		artifactProjectPreset.multiSelectionEnabled = false;
		artifactProjectPreset.fileSelectionMode = DIRECTORIES_ONLY;

		workingDirectoryPreset = new OptionSet();
		workingDirectoryPreset.title = messageSource.getMessage("filechooser.workingDirectoryPreset.title");
		workingDirectoryPreset.okButton = messageSource.getMessage("choose");
		workingDirectoryPreset.acceptAllUsed = false;
		workingDirectoryPreset.multiSelectionEnabled = false;
		workingDirectoryPreset.fileSelectionMode = DIRECTORIES_ONLY;

		classPathDirectoryPreset = new OptionSet();
		classPathDirectoryPreset.title = messageSource.getMessage("filechooser.classPathDirectoryPreset.title");
		classPathDirectoryPreset.okButton = messageSource.getMessage("choose");
		classPathDirectoryPreset.acceptAllUsed = false;
		classPathDirectoryPreset.multiSelectionEnabled = true;
		classPathDirectoryPreset.fileSelectionMode = DIRECTORIES_ONLY;

		jarFilePreset = new OptionSet();
		jarFilePreset.title = messageSource.getMessage("filechooser.jarFilePreset.title");
		jarFilePreset.okButton = messageSource.getMessage("choose");
		jarFilePreset.acceptAllUsed = true;
		jarFilePreset.multiSelectionEnabled = true;
		jarFilePreset.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.jarFiles"), "jar"));

		jarFilePreset.fileFilters = filters.toArray(new FileFilter[filters.size()]);

		codeFilePreset = new OptionSet();
		codeFilePreset.title = messageSource.getMessage("filechooser.codeFilePreset.title");
		codeFilePreset.okButton = messageSource.getMessage("choose");
		codeFilePreset.acceptAllUsed = true;
		codeFilePreset.multiSelectionEnabled = false;
		codeFilePreset.fileSelectionMode = FILES_ONLY;

		filters.clear();
		filters.add(new FileNameExtensionFilter(messageSource.getMessage("filechooser.filter.codeFiles"), "java", "class"));

		codeFilePreset.fileFilters = filters.toArray(new FileFilter[filters.size()]);

	}

	public int showDialog(Component parent, OptionSet options) {
		options.use();
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

		file = chooseFileForWrite(parent, consoleSaveAsText);
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

		file = chooseFileForWrite(parent, tableSaveAsText);
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

		file = chooseFileForWrite(parent, samplesSaveAsText);
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

		file = chooseFileForWrite(parent, samplesSaveAsFloat);
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

		file = chooseFileForWrite(parent, exportSignal);
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

		file = chooseFileForWrite(parent, exportBook);
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

		file = chooseFileForWrite(parent, chartSaveAsPng);
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

		file = chooseFileForWrite(parent, saveMP5Config);
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

		file = chooseFileForWrite(parent, saveMP5Signal);
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
		saveDocument.fileFilters = filters;

		file = chooseFileForWrite(parent, saveDocument);
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

		file = chooseFileForWrite(parent, saveTag);
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

		file = chooseFileForRead(parent, openTag);
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

		file = chooseFileForRead(parent, expertTag);
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

		file = chooseFileForRead(parent, importTag);
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

		file = chooseFileForWrite(parent, exportTag);
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

		file = chooseFileForRead(parent, readXMLManifest);

		return file;

	}

	public File chooseSavePresetFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastPresetPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForWrite(parent, savePreset);
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

		file = chooseFileForRead(parent, loadPreset);
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

		file = chooseFileForWrite(parent, artifactProjectPreset);
		if (file != null) {
			applicationConfig.setLastArtifactProjectPath(getCurrentDirectory().getAbsolutePath());
		}

		return file;

	}

	public File chooseExecutableFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		setCurrentDirectory(new File(System.getProperty("user.dir")));

		file = chooseFileForExecute(parent, executablePreset);

		return file;

	}

	public File chooseBookFile(Component parent) {

		File file = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastBookFilePath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		file = chooseFileForRead(parent, bookPreset);
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

		file = chooseFileForWrite(parent, bookSavePreset);
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

		file = chooseDirectory(parent, workingDirectoryPreset);

		return file;

	}

	public File[] chooseClassPathDirectories(Component parent) {

		File[] files = null;

		setSelectedFile(new File(""));
		String path = applicationConfig.getLastLibraryPath();
		if (path != null) {
			setCurrentDirectory(new File(path));
		}

		files = chooseDirectoriesForRead(parent, classPathDirectoryPreset);
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

		files = chooseFilesForRead(parent, jarFilePreset);
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

		file = chooseFileForRead(parent, codeFilePreset);
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

	protected class OptionSet {
		String title;
		String okButton;
		boolean acceptAllUsed;
		boolean multiSelectionEnabled;
		int fileSelectionMode;
		FileFilter[] fileFilters;

		void use() {
			setDialogTitle(title);
			setApproveButtonText(okButton);
			setAcceptAllFileFilterUsed(acceptAllUsed);
			resetChoosableFileFilters();
			setMultiSelectionEnabled(multiSelectionEnabled);
			setFileSelectionMode(fileSelectionMode);
			if (fileFilters != null) {
				for (int i=fileFilters.length-1; i>= 0; i--) {
					addChoosableFileFilter(fileFilters[i]);
				}
			}
		}
	}


}
