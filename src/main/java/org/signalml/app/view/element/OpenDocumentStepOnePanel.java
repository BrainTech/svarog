/* OpenDocumentStepOnePanel.java created 2007-09-17
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
 * Panel for the first step of the {@link OpenDocumentDialog}.
 * Allows to:
 * <ul>
 * <li>{@link #getFilePanel() select} the file to open,</li>
 * <li>{@link #getFileTypePanel() select} the {@link ManagedDocumentType type}
 * of the {@link Document} to open.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenDocumentStepOnePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(OpenDocumentStepOnePanel.class);

	/**
	 * the source of messages (labels)
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * the radio button which tells that the {@link ManagedDocumentType type}
	 * of the document should be autodetected
	 */
	private JRadioButton autodetectRadio = null;
	/**
	 * the radio button which tells that the {@link ManagedDocumentType type}
	 * of the document will be chosen by user from the {@link #fileTypeCombo
	 * list} 
	 */
	private JRadioButton chooseRadio = null;
	/**
	 * the combo-box with possible {@link ManagedDocumentType types} of
	 * documents
	 */
	private JComboBox fileTypeCombo = null;

	/**
	 * the group of radio buttons with {@link #autodetectRadio} and
	 * {@link #chooseRadio}
	 */
	private ButtonGroup radioGroup;
	/**
	 * the {@link EmbeddedFileChooser chooser} of files, which is used to
	 * select the file to open
	 */
	private EmbeddedFileChooser fileChooser = null;

	/**
	 * the panel with {@link #fileChooser}, which allows to select the
	 * file that should be opened
	 */
	private JPanel filePanel = null;
	/**
	 * the panel which allows to select the {@link ManagedDocumentType type}
	 * of the document or to tell that this type should be autodetected
	 */
	private JPanel fileTypePanel = null;

	/**
	 * Constructor. Sets the message source and initializes this panel.
	 * @param messageSource the source of messages (labels)
	 */
	public OpenDocumentStepOnePanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * Initializes this panel with two sub-panels:
	 * <ul><li>the {@link #getFilePanel() panel} with which allows to select
	 * the file that should be opened,</li>
	 * <li>the {@link #getFileTypePanel() panel} the panel which allows to
	 * select the {@link ManagedDocumentType type} of the document or to tell
	 * that this type should be autodetected.</li></ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		radioGroup = new ButtonGroup();

		add(getFilePanel(), BorderLayout.CENTER);
		add(getFileTypePanel(), BorderLayout.SOUTH);

		getAutodetectRadio().setSelected(true);
		getFileTypeCombo().setEnabled(false);

	}

	/**
	 * Returns the panel with {@link #getFileChooser() file chooser}, which
	 * allows to select the file that should be opened.
	 * If the panel doesn't exist it is created.
	 * @return the panel which allows to select the file that should be opened
	 */
	private JPanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new JPanel();
			filePanel.setBorder(BorderFactory.createTitledBorder(messageSource.getMessage("openDocument.chooseFile")));
			filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
			filePanel.add(getFileChooser());

		}

		return filePanel;
	}

	/**
	 * Returns the panel which allows to select the {@link ManagedDocumentType
	 * type} of the document or to tell that this type should be autodetected.
	 * If the panel doesn't exist it is created.
	 * @return the panel which allows to select the type of the document or to
	 * tell that this type should be autodetected
	 */
	private JPanel getFileTypePanel() {
		if (fileTypePanel == null) {
			fileTypePanel = new JPanel();
			fileTypePanel.setBorder(BorderFactory.createTitledBorder(messageSource.getMessage("openDocument.chooseFileType")));
			fileTypePanel.setLayout(new BoxLayout(fileTypePanel, BoxLayout.Y_AXIS));
			fileTypePanel.add(getAutodetectRadio());
			fileTypePanel.add(getChooseRadio());
			fileTypePanel.add(getFileTypeCombo());
		}

		return fileTypePanel;
	}

	/**
	 * Returns the radio button which tells that the {@link ManagedDocumentType
	 * type} of the document should be autodetected.
	 * If the button doesn't exist it is created and added to radio group.
	 * @return the radio button which tells that the type of the document
	 * should be autodetected
	 */
	public JRadioButton getAutodetectRadio() {
		if (autodetectRadio == null) {
			autodetectRadio = new JRadioButton();
			autodetectRadio.setText(messageSource.getMessage("openDocument.fileType.autodetect"));
			autodetectRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(autodetectRadio);
		}
		return autodetectRadio;
	}

	/**
	 * Returns the radio button which tells that the {@link ManagedDocumentType
	 * type} of the document will be chosen by user from the
	 * {@link #getFileTypeCombo() list}.
	 * If the button doesn't exist it is created, added to radio group and
	 * the listener is added to id.
	 * The listener enables or disables the {@link #getFileTypeCombo()
	 * combo-box} based on the state of the button.
	 * @return the radio button which tells that the type of the document will
	 * be chosen by user
	 */
	public JRadioButton getChooseRadio() {
		if (chooseRadio == null) {
			chooseRadio = new JRadioButton();
			chooseRadio.setText(messageSource.getMessage("openDocument.fileType.choose"));
			chooseRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(chooseRadio);
			chooseRadio.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					getFileTypeCombo().setEnabled(chooseRadio.isSelected());
				}

			});
		}
		return chooseRadio;
	}

	/**
	 * Returns the combo-box with possible {@link ManagedDocumentType types} of
	 * documents.
	 * If the combo-box doesn't exist it is created and filled with types.
	 * @return the combo-box with possible types of documents
	 */
	public JComboBox getFileTypeCombo() {
		if (fileTypeCombo == null) {
			fileTypeCombo = new JComboBox();
			fileTypeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
			fileTypeCombo.addItem(messageSource.getMessage("openDocument.fileType.signal"));
			fileTypeCombo.addItem(messageSource.getMessage("openDocument.fileType.book"));
			fileTypeCombo.addItem(messageSource.getMessage("openDocument.fileType.tag"));
		}

		return fileTypeCombo;
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

			ManagedDocumentType[] types = ManagedDocumentType.getAll();
			FileFilter[] filters;
			int i;
			int e;
			for (i=types.length-1; i>=0; i--) {
				filters = types[i].getFileFilters(messageSource);
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
