/* ProfilePathTypePanel.java created 2007-09-14
 *
 */
package org.signalml.app.view.element;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import org.signalml.app.util.DirectoryFileFilter;
import org.springframework.context.support.MessageSourceAccessor;

/** ProfilePathTypePanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ProfilePathTypePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel welcomeLabel = null;

	private MessageSourceAccessor messageSource;
	private JRadioButton defaultRadio = null;
	private JRadioButton customRadio = null;
	private JLabel infoLabel = null;

	private ButtonGroup radioGroup;
	private EmbeddedFileChooser fileChooser = null;

	/**
	 * This is the default constructor
	 */
	public ProfilePathTypePanel(MessageSourceAccessor messageSource) {
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

		setBorder(BorderFactory.createTitledBorder(messageSource.getMessage("profilePath.title")));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		welcomeLabel = new JLabel();
		welcomeLabel.setText(messageSource.getMessage("profilePath.welcome"));
		welcomeLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(welcomeLabel);
		add(Box.createVerticalStrut(5));
		add(getInfoLabel());
		add(Box.createVerticalStrut(5));

		radioGroup = new ButtonGroup();

		add(getDefaultRadio());
		add(getCustomRadio());

		getDefaultRadio().setSelected(true);

		getFileChooser().setVisible(false);
		add(getFileChooser());

	}

	/**
	 * This method initializes defaultRadio
	 *
	 * @return javax.swing.JRadioButton
	 */
	public JRadioButton getDefaultRadio() {
		if (defaultRadio == null) {
			defaultRadio = new JRadioButton();
			defaultRadio.setText(messageSource.getMessage("profilePath.defaultRadio"));
			defaultRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(defaultRadio);
		}
		return defaultRadio;
	}

	/**
	 * This method initializes customRadio
	 *
	 * @return javax.swing.JRadioButton
	 */
	public JRadioButton getCustomRadio() {
		if (customRadio == null) {
			customRadio = new JRadioButton();
			customRadio.setText(messageSource.getMessage("profilePath.customRadio"));
			customRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(customRadio);
			customRadio.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					getFileChooser().setVisible(e.getStateChange() == ItemEvent.SELECTED);

					ProfilePathTypePanel.this.revalidate();
					Dimension d = ProfilePathTypePanel.this.getTopLevelAncestor().getPreferredSize();
					ProfilePathTypePanel.this.getTopLevelAncestor().setSize(d);
					ProfilePathTypePanel.this.repaint();
				}

			});
		}
		return customRadio;
	}

	private JLabel getInfoLabel() {
		if (infoLabel == null) {
			infoLabel = new JLabel();
			infoLabel.setFont(infoLabel.getFont().deriveFont(Font.PLAIN, 12));
			infoLabel.setText("<html><body><div style=\"width: 400px; text-align: justify;\">" + messageSource.getMessage("profilePath.info") + "</div></body></html>");
			infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//			infoLabel.setPreferredSize(new Dimension(500,90));
			infoLabel.setBorder(new EmptyBorder(3,0,3,0));
		}
		return infoLabel;
	}

	public EmbeddedFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new EmbeddedFileChooser();
			fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
			fileChooser.setFileHidingEnabled(false);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.resetChoosableFileFilters();
			fileChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setFileFilter(new DirectoryFileFilter(messageSource.getMessage("profilePath.directoryFilter")));
			fileChooser.setPreferredSize(new Dimension(500,350));

			fileChooser.setInvokeDefaultButtonOnApprove(true);

		}
		return fileChooser;
	}

}
