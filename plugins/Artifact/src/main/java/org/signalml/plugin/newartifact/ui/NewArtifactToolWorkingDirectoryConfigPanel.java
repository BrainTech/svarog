/* ArtifactToolWorkingDirectoryConfigPanel.java created 2008-02-08
 *
 */
package org.signalml.plugin.newartifact.ui;

import static org.signalml.plugin.i18n.PluginI18n._;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.newartifact.data.NewArtifactConfiguration;

/** ArtifactToolWorkingDirectoryConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewArtifactToolWorkingDirectoryConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField directoryTextField;
	private JButton chooseDirectoryButton;

	private FileChooser fileChooser;

	private File directory;

	public NewArtifactToolWorkingDirectoryConfigPanel(FileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Artifact working directory")),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel directoryLabel = new JLabel(_("Working directory"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(directoryLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getDirectoryTextField())
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getChooseDirectoryButton())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(directoryLabel)
			.addComponent(getDirectoryTextField())
			.addComponent(getChooseDirectoryButton())
		);

		layout.setVerticalGroup(vGroup);

	}

	public JTextField getDirectoryTextField() {
		if (directoryTextField == null) {
			directoryTextField = new JTextField();
			directoryTextField.setPreferredSize(new Dimension(150,25));
			directoryTextField.setEditable(false);
		}
		return directoryTextField;
	}

	public JButton getChooseDirectoryButton() {
		if (chooseDirectoryButton == null) {
			chooseDirectoryButton = new JButton(new ChooseDirectoryAction());
		}
		return chooseDirectoryButton;
	}

	public File getDirectory() {
		return directory;
	}

	public void fillPanelFromModel(NewArtifactConfiguration config) {

		String directoryPath = config.getWorkingDirectoryPath();
		if (directoryPath != null) {
			directory = new File(directoryPath);
			getDirectoryTextField().setText(directory.getAbsolutePath());
		} else {
			directory = null;
			getDirectoryTextField().setText("");
		}

	}

	public void fillModelFromPanel(NewArtifactConfiguration config) {

		if (directory != null) {
			config.setWorkingDirectoryPath(directory.getAbsolutePath());
		} else {
			config.setWorkingDirectoryPath(null);
		}

	}

	public void validatePanel(ValidationErrors errors) {

		// no validation

	}

	protected class ChooseDirectoryAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChooseDirectoryAction() {
			super(_("Choose..."));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/find.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Choose a directory for artifact projects"));
		}

		@Override
		public void actionPerformed(ActionEvent ev) {

			File file = fileChooser.chooseWorkingDirectory(NewArtifactToolWorkingDirectoryConfigPanel.this.getTopLevelAncestor(), directory);
			if (file == null) {
				return;
			}

			directory = file;

			getDirectoryTextField().setText(directory.getAbsolutePath());

		}

	}

}
