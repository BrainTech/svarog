/* MP5ResultBookPanel.java created 2008-02-14
 *
 */
package org.signalml.app.method.mp5;

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
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.workspace.ViewerFileChooser;

/** MP5ResultBookPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ResultBookPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField bookTextField;
	private JButton chooseBookButton;

	private ViewerFileChooser fileChooser;

	private File bookFile;

	public MP5ResultBookPanel(ViewerFileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Choose book file")),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel bookFileLabel = new JLabel(_("Book file"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(bookFileLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getBookTextField())
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getChooseBookButton())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(bookFileLabel)
			.addComponent(getBookTextField())
			.addComponent(getChooseBookButton())
		);

		layout.setVerticalGroup(vGroup);

	}

	public JTextField getBookTextField() {
		if (bookTextField == null) {
			bookTextField = new JTextField();
			bookTextField.setPreferredSize(new Dimension(300,25));
			bookTextField.setEditable(false);
		}
		return bookTextField;
	}

	public JButton getChooseBookButton() {
		if (chooseBookButton == null) {
			chooseBookButton = new JButton(new ChooseBookFileAction());
		}
		return chooseBookButton;
	}

	public void fillPanelFromModel(MP5ResultTargetDescriptor descriptor) {

		if (descriptor.isSaveToFile()) {
			bookFile = descriptor.getBookFile();
			if (bookFile != null) {
				getBookTextField().setText(bookFile.getAbsolutePath());
			} else {
				getBookTextField().setText("");
			}
		} else {
			bookFile = null;
			getBookTextField().setText("");
		}

	}

	public void fillModelFromPanel(MP5ResultTargetDescriptor descriptor) {

		descriptor.setBookFile(bookFile);

	}

	public void validatePanel(ValidationErrors errors) {

		if (bookFile == null) {
			errors.addError(_("Book file not chosen"));
		} else {
			File parent = bookFile.getParentFile();
			if (parent == null || !parent.exists() || !parent.canWrite()) {
				errors.addError(_("Book file parent directory doesn't exist or not writable"));
			}
		}

	}

	@Override
	public void setEnabled(boolean enabled) {
		getBookTextField().setEnabled(enabled);
		getChooseBookButton().setEnabled(enabled);
		super.setEnabled(enabled);
	}

	protected class ChooseBookFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChooseBookFileAction() {
			super(_("Choose..."));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/find.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Choose a book file to save"));
		}

		public void actionPerformed(ActionEvent ev) {

			File file = fileChooser.chooseBookFileForWrite(MP5ResultBookPanel.this.getTopLevelAncestor());
			if (file == null) {
				return;
			}

			bookFile = file;

			getBookTextField().setText(bookFile.getAbsolutePath());

		}

	}

}
