/* StagerResultTagPanel.java created 2008-02-20
 *
 */
package org.signalml.plugin.newartifact.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import static org.signalml.plugin.newartifact.NewArtifactPlugin._;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.view.FileChooser;

import org.springframework.validation.Errors;

/** StagerResultTagPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewArtifactResultTagPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField tagTextField;
	private JButton chooseTagButton;

	private FileChooser fileChooser;

	private File tagFile;

	public NewArtifactResultTagPanel(FileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Choose primary result tag file")),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel tagFileLabel = new JLabel(_("Primary tag file"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(tagFileLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getTagTextField())
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getChooseTagButton())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(tagFileLabel)
			.addComponent(getTagTextField())
			.addComponent(getChooseTagButton())
		);

		layout.setVerticalGroup(vGroup);

	}

	public JTextField getTagTextField() {
		if (tagTextField == null) {
			tagTextField = new JTextField();
			tagTextField.setPreferredSize(new Dimension(300,25));
			tagTextField.setEditable(false);
		}
		return tagTextField;
	}

	public JButton getChooseTagButton() {
		if (chooseTagButton == null) {
			chooseTagButton = new JButton(new ChooseTagFileAction());
		}
		return chooseTagButton;
	}

	public void fillPanelFromModel(NewArtifactResultTargetDescriptor descriptor) {

		if (descriptor.isPrimarySaveToFile()) {
			tagFile = descriptor.getPrimaryTagFile();
			if (tagFile != null) {
				getTagTextField().setText(tagFile.getAbsolutePath());
			} else {
				getTagTextField().setText("");
			}
		} else {
			tagFile = null;
			getTagTextField().setText("");
		}

	}

	public void fillModelFromPanel(NewArtifactResultTargetDescriptor descriptor) {

		descriptor.setPrimaryTagFile(tagFile);

	}

	public void validatePanel(Errors errors) {

		if (tagFile == null) {
			errors.rejectValue("primaryTagFile", "error.newArtifact.result.badTagFile", _("Tag file not chosen"));
		} else {
			File parent = tagFile.getParentFile();
			if (parent == null || !parent.exists() || !parent.canWrite()) {
				errors.rejectValue("primaryTagFile", "error.newArtifact.result.tagFileNotWritable", _("Tag file parent directory doesn't exist or not writable"));
			}
		}

	}

	@Override
	public void setEnabled(boolean enabled) {
		getTagTextField().setEnabled(enabled);
		getChooseTagButton().setEnabled(enabled);
		super.setEnabled(enabled);
	}

	protected class ChooseTagFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChooseTagFileAction() {
			super(_("Choose..."));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/find.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Choose a tag file to save"));
		}

		public void actionPerformed(ActionEvent ev) {

			File file = fileChooser.chooseSaveTag(NewArtifactResultTagPanel.this.getTopLevelAncestor());
			if (file == null) {
				return;
			}

			tagFile = file;

			getTagTextField().setText(tagFile.getAbsolutePath());

		}

	}

}
