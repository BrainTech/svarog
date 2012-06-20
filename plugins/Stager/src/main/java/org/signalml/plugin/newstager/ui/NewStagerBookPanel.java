/* StagerBookPanel.java created 2008-02-14
 *
 */
package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.newstager.NewStagerPlugin._;

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
import org.signalml.plugin.newstager.data.NewStagerParameters;

/**
 * StagerBookPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerBookPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField bookTextField;
	private JButton chooseBookButton;

	private FileChooser fileChooser;

	private File bookFile;

	public NewStagerBookPanel(FileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(new TitledBorder(
					_("Choose book file")), new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel bookFileLabel = new JLabel(_("Book file"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup()
						.addComponent(bookFileLabel));

		hGroup.addGroup(layout.createParallelGroup().addComponent(
							getBookTextField()));

		hGroup.addGroup(layout.createParallelGroup().addComponent(
							getChooseBookButton()));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(bookFileLabel).addComponent(getBookTextField())
						.addComponent(getChooseBookButton()));

		layout.setVerticalGroup(vGroup);

	}

	public JTextField getBookTextField() {
		if (bookTextField == null) {
			bookTextField = new JTextField();
			bookTextField.setPreferredSize(new Dimension(300, 25));
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

	public void fillPanelFromModel(NewStagerParameters parameters) {
		String path = parameters.bookFilePath;
		if (path != null) {
			bookFile = new File(path);
			getBookTextField().setText(path);
		} else {
			bookFile = null;
			getBookTextField().setText("");
		}
	}

	public void fillModelFromPanel(NewStagerParameters parameters) {
		parameters.bookFilePath = bookFile == null ? null : bookFile
								  .getAbsolutePath();
	}

	public void validatePanel(ValidationErrors errors) {
		if (bookFile == null || !bookFile.exists() || !bookFile.canRead()) {
			errors.addError(_("Book file not chosen, doesn't exist or unreadable"));
		}
	}

	protected class ChooseBookFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChooseBookFileAction() {
			super(_("Choose..."));
			putValue(
				AbstractAction.SMALL_ICON,
				IconUtils
				.loadClassPathIcon("org/signalml/app/icon/find.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,
					 _("Choose a book file for this signal"));
		}

		public void actionPerformed(ActionEvent ev) {

			File file = fileChooser.chooseBookFile(NewStagerBookPanel.this
												   .getTopLevelAncestor());
			if (file == null) {
				return;
			}

			bookFile = file;

			getBookTextField().setText(bookFile.getAbsolutePath());

		}

	}

}
