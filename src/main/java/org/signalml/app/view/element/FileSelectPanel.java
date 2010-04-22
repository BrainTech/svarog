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

	private JTextField fileNameField;
	private JButton changeButton;

	/**
	 * This is the default constructor
	 */
	public FileSelectPanel( MessageSourceAccessor messageSource) {
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
        JLabel label = new JLabel( messageSource.getMessage( "openMonitor.saveDataLabel"));
        setLayout( new FlowLayout());
        add( label);
        add( getFileNameField());
        add( getChangeButton());
	}

    public JTextField getFileNameField() {
        if (fileNameField == null) {
            fileNameField = new JTextField( 20);
        }
        return fileNameField;
    }

    public JButton getChangeButton() {
        if (changeButton == null) {
            changeButton = new JButton( messageSource.getMessage( "openMonitor.browseButtonLabel"));
            changeButton.addActionListener( new ActionListener() {
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
        return changeButton;
    }

    public boolean isFileSelected() {
        String t = getFileNameField().getText();
        if (t != null && !"".equals( t))
            return true;
        else
            return false;
    }

}
