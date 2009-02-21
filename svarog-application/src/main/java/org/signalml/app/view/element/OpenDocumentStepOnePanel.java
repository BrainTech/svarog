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
import org.springframework.context.support.MessageSourceAccessor;

/** OpenDocumentStepOnePanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenDocumentStepOnePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(OpenDocumentStepOnePanel.class);
	
	private MessageSourceAccessor messageSource;
	
	private JRadioButton autodetectRadio = null;
	private JRadioButton chooseRadio = null;
	private JComboBox fileTypeCombo = null;
	
	private ButtonGroup radioGroup;
	private EmbeddedFileChooser fileChooser = null;
	
	private JPanel filePanel = null;
	private JPanel fileTypePanel = null;
	
	/**
	 * This is the default constructor
	 */
	public OpenDocumentStepOnePanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		setLayout(new BorderLayout());
		
		radioGroup = new ButtonGroup();

		add(getFilePanel(), BorderLayout.CENTER);
		add(getFileTypePanel(), BorderLayout.SOUTH);
		
		getAutodetectRadio().setSelected(true);
		getFileTypeCombo().setEnabled(false);

	}
	
	private JPanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new JPanel();
			filePanel.setBorder(BorderFactory.createTitledBorder(messageSource.getMessage("openDocument.chooseFile")));
			filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
			filePanel.add(getFileChooser());

		}

		return filePanel;
	}
	
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
	 * This method initializes defaultRadio	
	 * 	
	 * @return javax.swing.JRadioButton	
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
	 * This method initializes customRadio	
	 * 	
	 * @return javax.swing.JRadioButton	
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
			for( i=types.length-1; i>=0; i-- ) {
				filters = types[i].getFileFilters(messageSource);
				for( e=filters.length-1; e>=0; e-- ) {
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
