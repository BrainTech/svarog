/* FileChooserPanel.java created 2011-03-11
 *
 */

package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.signalml.app.document.ManagedDocumentType;

/**
 * A panel containing an EmbeddedFileChooser. Allows to select a given type of
 * file documents.
 *
 * @author Piotr Szachewicz
 */
public class FileChooserPanel extends JPanel {

	/**
	 * The document types managed by this file chooser panel.
	 */
	private ManagedDocumentType[] managedDocumentTypes;

	/**
	 * The file chooser embedded in this panel.
	 */
	private EmbeddedFileChooser fileChooser;

	/**
	 * Creates a new file chooser panel.
	 * @param managedDocumentTypes the types of documents which will be
	 * chosen using this panel
	 */
	public FileChooserPanel(ManagedDocumentType[] managedDocumentTypes) {
		this.managedDocumentTypes = managedDocumentTypes.clone();
		createInterface();
	}

	/**
	 * Creates this file chooser panel.
	 * @param singleManagedDocumentType the type of document which will
	 * be chosen using this panel
	 */
	public FileChooserPanel(ManagedDocumentType singleManagedDocumentType) {
		this(new ManagedDocumentType[] {singleManagedDocumentType});
	}

	/**
	 * Creates the GUI for this panel.
	 */
	private void createInterface() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(getFileChooser());
	}

	/**
	 * Returns the {@link EmbeddedFileChooser embedded file chooser}.
	 * If it doesn't exist it is created:
	 * <ul>
	 * <li>as open dialog,</li>
	 * <li>without multi-selection and hiding files,</li>
	 * <li>to open files of given {@link ManagedDocumentType#
	 * getFileFilterExtensions() types} or all,</li>
	 * <li>with the user directory as the current directory.</li>
	 * </ul>
	 * @return the embedded file chooser
	 */
	public EmbeddedFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new EmbeddedFileChooser();
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setFileHidingEnabled(false);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setAcceptAllFileFilterUsed(true);

			fileChooser.resetChoosableFileFilters();

			FileFilter[] filters;
			int i;
			int e;
			for (i=managedDocumentTypes.length-1; i>=0; i--) {
				filters = managedDocumentTypes[i].getFileFilters();
				for (e=filters.length-1; e>=0; e--) {
					fileChooser.addChoosableFileFilter(filters[e]);
					fileChooser.setFileFilter(filters[e]);
				}
			}

			fileChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			fileChooser.setPreferredSize(new Dimension(500,280));
			fileChooser.setMinimumSize(new Dimension(500, 150));

			// remove escape key binding to allow for dialog closing
			KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
			fileChooser.getInputMap(JFileChooser.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escape, "none");

		}
		return fileChooser;
	}

	/**
	 * Returns the file selected by this file chooser.
	 * @return the selected file
	 */
	public File getSelectedFile() {
		return fileChooser.getSelectedFile();
	}

	/**
	 * Returns the directory currently shown by this file chooser.
	 * @return directory shown by this file chooser
	 */
	public File getCurrentDirectory() {
		return fileChooser.getCurrentDirectory();
	}

}
