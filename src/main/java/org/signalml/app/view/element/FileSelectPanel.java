package org.signalml.app.view.element;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 */
public class FileSelectPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(FileSelectPanel.class);
	
	private MessageSourceAccessor messageSource;

	private JLabel selectFileLabel;
	private JTextField fileNameField;
	private JButton browseButton;

	/**
	 * This is the default constructor
	 */
	public FileSelectPanel(MessageSourceAccessor messageSource, String selectFilePrompt) {
		super();
		this.messageSource = messageSource;
		this.selectFileLabel = new JLabel(selectFilePrompt);
		initialize();
	}

	/**
	 * Initializes this panel.
	 */
	private void initialize() {
		setLayout( new FlowLayout());
		add(selectFileLabel);
		add( getFileNameField());
		add( getChangeButton());
	}

	protected JTextField getFileNameField() {
		if (fileNameField == null) {
			fileNameField = new JTextField( 20);
		}
		return fileNameField;
	}

	protected JButton getChangeButton() {
		if (browseButton == null) {
			browseButton = new JButton( messageSource.getMessage("fileSelectPanel.browseButtonLabel"));
			browseButton.addActionListener(new BrowseButtonAction());
		}
		return browseButton;
	}

	/**
	 * Sets the file name which shows in the file name text field.
	 * @param fileName a value of file name to be set
	 */
	public void setFileName(String fileName) {
		this.fileNameField.setText(fileName);
	}

	/**
	 * Returns the file name selected in this panel.
	 * @return a file name which was selected using this panel.
	 */
	public String getFileName() {
		return this.fileNameField.getText();
	}

	/**
	 * Returns whether a file was selected using this panel or not.
	 * @return true if the file is selected, false otherwise
	 */
	public boolean isFileSelected() {
		String t = getFileNameField().getText();
		if (t != null && !"".equals( t))
			return true;
		else
			return false;
	}

	/**
	 * Sets the state (enabled/disabled) of this component.
	 * @param enabled true if this panel should be enabled, false otherwise.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		selectFileLabel.setEnabled(enabled);
		fileNameField.setEnabled(enabled);
		browseButton.setEnabled(enabled);
	}

	/**
	 * Class responsible for the action performed after pressing Browse button.
	 * Shows a window in which the user can choose a file name's path and changes the
	 * text field containing the file name in the current FileSelectPanel.
	 */
	private class BrowseButtonAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();

			chooser.setApproveButtonText(messageSource.getMessage("ok"));
			int returnVal = chooser.showOpenDialog( FileSelectPanel.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();

				try {
					getFileNameField().setText( chooser.getSelectedFile().getCanonicalPath());
				}
				catch (IOException e1) {
					getFileNameField().setText("");
				}

			}

		}

	}

}
