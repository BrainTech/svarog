package org.signalml.app.view.element;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	 * This method initializes this
	 * 
	 * @return void
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
			browseButton = new JButton( messageSource.getMessage( "fileSelectPanel.browseButtonLabel"));
			browseButton.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					int returnVal = chooser.showOpenDialog( FileSelectPanel.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						try {
							getFileNameField().setText( chooser.getSelectedFile().getCanonicalPath());
						}
						catch (IOException e1) {
							getFileNameField().setText( "");
						}
					}
				}
			});
		}
		return browseButton;
	}

	public void setFileName(String fileName) {
		this.fileNameField.setText(fileName);
	}

	public String getFileName() {
		return this.fileNameField.getText();
	}

	public boolean isFileSelected() {
		String t = getFileNameField().getText();
		if (t != null && !"".equals( t))
			return true;
		else
			return false;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		selectFileLabel.setEnabled(enabled);
		fileNameField.setEnabled(enabled);
		browseButton.setEnabled(enabled);
	}

}
