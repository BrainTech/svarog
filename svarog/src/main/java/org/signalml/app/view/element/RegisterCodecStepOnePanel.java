/* RegisterCodecStepOnePanel.java created 2007-09-18
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogI18n._;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.signalml.app.view.dialog.RegisterCodecDialog;
import org.signalml.codec.SignalMLCodec;

/**
 * Panel for the first step of {@link RegisterCodecDialog}.
 * Contains only one {@link #getFilePanel() panel}, which allows to
 * select the file with the {@link SignalMLCodec codec}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RegisterCodecStepOnePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link #getFileChooser() embedded file chooser}
	 */
	private EmbeddedFileChooser fileChooser = null;

	/**
	 * the panel with the {@link #getFileChooser() file chooser}.
	 */
	private JPanel filePanel = null;

	/**
	 * Constructor. Initializes the panel.
	 */
	public RegisterCodecStepOnePanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with the {@link BorderLayout} and
	 * adds to it the {@link #getFilePanel() panel} with the
	 * {@link #getFileChooser() file chooser}.
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		add(getFilePanel(), BorderLayout.CENTER);

	}

	/**
	 * Returns the panel with the {@link #getFileChooser() file chooser}.
	 * If the panel doens't exist it is created.
	 * @return the panel with the file chooser.
	 */
	private JPanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new JPanel();
			filePanel.setBorder(BorderFactory.createTitledBorder(_("Choose XML definition file")));
			filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
			filePanel.add(getFileChooser());

		}

		return filePanel;
	}

	/**
	 * Returns the embedded file chooser.
	 * If it doesn't exist it is created:
	 * <ul>
	 * <li>as open dialog,</li>
	 * <li>without multi-selection and hiding files,</li>
	 * <li>to open files: {@code XML} or all,</li>
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
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(_("XML files (*.xml)"), "xml"));
			fileChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir"),"specs"));
			fileChooser.setPreferredSize(new Dimension(500,350));

			fileChooser.setInvokeDefaultButtonOnApprove(true);

			// remove escape key binding to allow for dialog closing
			KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
			fileChooser.getInputMap(JFileChooser.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escape, "none");

		}
		return fileChooser;
	}

}
