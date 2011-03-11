/* FileChooserPanel.java created 2011-03-11
 *
 */

package org.signalml.app.view.element;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.view.dialog.OpenDocumentDialog;
import org.signalml.plugin.export.signal.Document;
import org.springframework.context.support.MessageSourceAccessor;


/**
 *
 * @author Piotr Szachewicz
 */
public class FileChooserPanel extends JPanel {

	protected MessageSourceAccessor messageSource;
	private ManagedDocumentType[] managedDocumentTypes;

	private EmbeddedFileChooser fileChooser;

	public FileChooserPanel(MessageSourceAccessor messageSource, ManagedDocumentType[] managedDocumentTypes) {
		this.messageSource = messageSource;
		this.managedDocumentTypes = managedDocumentTypes.clone();
		createInterface();
	}

	public FileChooserPanel(MessageSourceAccessor messageSource, ManagedDocumentType singleManagedDocumentType) {
		this(messageSource, new ManagedDocumentType[] {singleManagedDocumentType});
	}

	private void createInterface() {
		setBorder(BorderFactory.createTitledBorder(messageSource.getMessage("openDocument.chooseFile")));
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
				filters = managedDocumentTypes[i].getFileFilters(messageSource);
				for (e=filters.length-1; e>=0; e--) {
					fileChooser.addChoosableFileFilter(filters[e]);
				}
			}

			fileChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			fileChooser.setPreferredSize(new Dimension(500,350));

			fileChooser.setInvokeDefaultButtonOnApprove(true);

			// remove escape key binding to allow for dialog closing
			KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
			fileChooser.getInputMap(JFileChooser.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escape, "none");

		}
		return fileChooser;
	}
}
