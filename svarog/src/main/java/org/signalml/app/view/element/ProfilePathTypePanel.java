/* ProfilePathTypePanel.java created 2007-09-14
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
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

/**
 * The panel which is displayed at the first use of the application (or if
 * profile directory was deleted). Contains:
 * <ul>
 * <li>the label with the welcome message,</li>
 * <li>the label with information that the profile directory is required and
 * where it can be located,</li>
 * <li>the group of radio buttons which allows to select if the profile
 * directory should be located in the {@link #getDefaultRadio() default
 * directory} inside user's home directory or in the {@link #getCustomRadio()
 * custom location},</li>
 * <li>the {@link #getFileChooser() file chooser} which allows to select
 * the directory to be used (this chooser is visible only if appropriate
 * radio button is selected.</li></ul> 
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ProfilePathTypePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	/**
	 * the label with the welcome message
	 */
	private JLabel welcomeLabel = null;

	/**
	 * the radio button which tells that the profile directory should
	 * be located in the default directory inside user's home directory
	 */
	private JRadioButton defaultRadio = null;
	/**
	 * the radio button which tells that the profile directory should
	 * be chosen by user
	 */
	private JRadioButton customRadio = null;
	/**
	 * the label with information that the profile directory is
	 * required and where it can be located
	 */
	private JLabel infoLabel = null;

	private ButtonGroup radioGroup;
	/**
	 * the {@link EmbeddedFileChooser chooser} which allows to select the
	 * profile directory
	 */
	private EmbeddedFileChooser fileChooser = null;

	/**
	 * Constructor. Initializes the panel.
	 */
	public ProfilePathTypePanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with BoxLayout and:
	 * <ul>
	 * <li>the {@link #welcomeLabel label} with the welcome message,</li>
	 * <li>the {@link #infoLabel label} with information that the profile
	 * directory is required and where it can be located,</li>
	 * <li>the {@link #radioGroup group} of buttons which allows to select
	 * if the profile directory should be located in the {@link
	 * #getDefaultRadio() default directory} inside user's home directory or in
	 * the {@link #getCustomRadio() custom location},</li>
	 * <li>the {@link #getFileChooser() file chooser} which allows to select
	 * the directory to be used (this chooser is visible only if appropriate
	 * radio button is selected).</li></ul>
	 */
	private void initialize() {

		setBorder(BorderFactory.createTitledBorder(_("Choose profile path")));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		welcomeLabel = new JLabel();
		welcomeLabel.setText(_("Welcome to Svarog"));
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
	 * Returns the radio button which tells that the profile directory should
	 * be located in the default directory inside user's home directory.
	 * If the button doesn't exist it is created.
	 * @return the radio button which tells that the profile directory should
	 * be located in the default directory inside user's home directory
	 */
	public JRadioButton getDefaultRadio() {
		if (defaultRadio == null) {
			defaultRadio = new JRadioButton();
			defaultRadio.setText(_("signalml directory inside default home"));
			defaultRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(defaultRadio);
		}
		return defaultRadio;
	}

	/**
	 * Returns the radio button which tells that the profile directory should
	 * be chosen by user.
	 * If the button doesn't exist it is created and the listener is added to
	 * it. If this button is selected the listener shows the {@link
	 * #getFileChooser() file chooser}, if it is not hides it.
	 * @return the radio button which tells that the profile directory should
	 * be chosen by user
	 */
	public JRadioButton getCustomRadio() {
		if (customRadio == null) {
			customRadio = new JRadioButton();
			customRadio.setText(_("custom location"));
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

	/**
	 * Returns the label with information that the profile directory is
	 * required and where it can be located.
	 * If the label doesn't exist it is created.
	 * @return the label with information that the profile directory is
	 * required and where it can be located
	 */
	private JLabel getInfoLabel() {
		if (infoLabel == null) {
			infoLabel = new JLabel();
			infoLabel.setFont(infoLabel.getFont().deriveFont(Font.PLAIN, 12));
			infoLabel.setText("<html><body><div style=\"width: 400px; text-align: justify;\">" + _("The signalml viewer needs a directory to store its configuration and files. This directory may reside inside the default user's home directory (system dependent), or you can select any directory on disk (remember that you need to select a concrete directory for SignalML files, not the directory in which you would like the later to be created - create the directory if necessary).") + "</div></body></html>");
			infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//			infoLabel.setPreferredSize(new Dimension(500,90));
			infoLabel.setBorder(new EmptyBorder(3,0,3,0));
		}
		return infoLabel;
	}

	/**
	 * Returns the {@link EmbeddedFileChooser chooser} which allows to select the
	 * profile directory.
	 * @return the chooser which allows to select the profile directory
	 */
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
			fileChooser.setFileFilter(new DirectoryFileFilter(_("Directories")));
			fileChooser.setPreferredSize(new Dimension(500,350));

			fileChooser.setInvokeDefaultButtonOnApprove(true);

		}
		return fileChooser;
	}

}
