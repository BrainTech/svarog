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
	File chooseSaveTag(Component topLevelAncestor);
}
