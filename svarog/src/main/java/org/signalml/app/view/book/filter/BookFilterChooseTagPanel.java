/* BookFilterChooseTagPanel.java created 2008-03-05
 *
 */
package org.signalml.app.view.book.filter;

import static org.signalml.app.SvarogApplication._;
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

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.domain.book.filter.TagBasedAtomFilter;

import org.springframework.validation.Errors;

/** BookFilterChooseTagPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookFilterChooseTagPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField tagTextField;
	private JButton chooseTagButton;

	private ViewerFileChooser fileChooser;

	private File tagFile;

	public BookFilterChooseTagPanel(ViewerFileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(_("Choose tag file")),
		        new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel tagFileLabel = new JLabel(_("Tag file"));

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

	public void fillPanelFromModel(TagBasedAtomFilter filter) {

		String tagFilePath = filter.getTagFilePath();
		if (tagFilePath == null) {
			tagFile = null;
			getTagTextField().setText("");
		} else {
			tagFile = new File(tagFilePath);
			getTagTextField().setText(tagFile.getAbsolutePath());
		}

	}

	public void fillModelFromPanel(TagBasedAtomFilter filter) {

		if (tagFile == null) {
			filter.setTagFilePath(null);
		} else {
			filter.setTagFilePath(tagFile.getAbsolutePath());
		}

	}

	public void validatePanel(Errors errors) {

		if (tagFile == null) {
			errors.rejectValue("tagFilePath", "tagBasedFilter.error.badTagFile");
		} else {
			if (!tagFile.exists() || !tagFile.canRead()) {
				errors.rejectValue("tagFilePath", "tagBasedFilter.error.tagFileNotReadable");
			}
		}

	}

	public File getTagFile() {
		return tagFile;
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
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Choose the tag file to filter by"));
		}

		public void actionPerformed(ActionEvent ev) {

			File file = fileChooser.chooseOpenTag(BookFilterChooseTagPanel.this.getTopLevelAncestor());
			if (file == null) {
				return;
			}

			tagFile = file;

			getTagTextField().setText(tagFile.getAbsolutePath());

		}

	}

}
