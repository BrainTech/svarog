package org.signalml.plugin.export.view;

import java.awt.Component;
import java.io.File;

/**
 * {@link org.signalml.app.view.workspace.ViewerFileChooser} wrapper to be used by plugins.
 * 
 * @author Stanislaw Findeisen
 */
public interface FileChooser {
    File chooseWorkingDirectory(Component parent, File currentDirectory);
    File chooseExportSignalFile(Component parent, File signalFile);
    File chooseSaveTag(Component parent);
    File chooseOpenTag(Component parent);
	File chooseExpertTag(Component parent);
	File chooseImportTag(Component parent);
	File chooseExportTag(Component parent);
	File chooseBookFile(Component parent);
	File chooseSavePresetFile(Component parent);
	File chooseLoadPresetFile(Component parent);
	File chooseCodeFile(Component parent);
	File[] chooseClassPathDirectories(Component parent);
	File[] chooseJarFiles(Component parent);
	File chooseExecutableFile(Component parent);
}
