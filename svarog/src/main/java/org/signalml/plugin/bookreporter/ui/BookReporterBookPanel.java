package org.signalml.plugin.bookreporter.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.book.DefaultBookBuilder;
import org.signalml.domain.book.StandardBook;
import org.signalml.plugin.bookreporter.data.BookReporterParameters;
import org.signalml.plugin.export.view.FileChooser;

/**
 * @author piotr@develancer.pl
 * (based on Michal Dobaczewski's NewStagerBookPanel)
 */
public class BookReporterBookPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BookReporterBookPanel.class);

	private JTextField bookTextField;
	private JButton chooseBookButton;
	private JSpinner bookChannelField;

	private final FileChooser fileChooser;

	private File bookFile;

	public BookReporterBookPanel(FileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;
		updateBookChannels();
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
		JLabel bookChannelLabel = new JLabel(_("Channel"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup()
			.addComponent(bookFileLabel));

		hGroup.addGroup(layout.createParallelGroup().addComponent(
			getBookTextField()));

		hGroup.addGroup(layout.createParallelGroup().addComponent(
			getChooseBookButton()));

		hGroup.addGroup(layout.createParallelGroup()
			.addComponent(bookChannelLabel));

		hGroup.addGroup(layout.createParallelGroup().addComponent(
			getBookChannelField()));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(bookFileLabel).addComponent(getBookTextField())
			.addComponent(getChooseBookButton())
			.addComponent(bookChannelLabel).addComponent(getBookChannelField()));

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

	public JSpinner getBookChannelField() {
		if (bookChannelField == null) {
			bookChannelField = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
		}
		return bookChannelField;
	}

	public void fillPanelFromModel(BookReporterParameters parameters) {
		String path = parameters.bookFilePath;
		if (path.isEmpty()) {
			bookFile = null;
			getBookTextField().setText("");
		} else {
			bookFile = new File(path);
			getBookTextField().setText(path);
		}
		updateBookChannels();
		getBookChannelField().setValue(parameters.channel);
	}

	public void fillModelFromPanel(BookReporterParameters parameters) {
		parameters.bookFilePath = (bookFile == null) ? ""
			: bookFile.getAbsolutePath();
		parameters.channel = (Integer) getBookChannelField().getValue();
	}

	public void validatePanel(ValidationErrors errors) {
		if (bookFile == null || !bookFile.exists() || !bookFile.canRead()) {
			errors.addError(_("Book file not chosen, doesn't exist or unreadable"));
		}
	}

	private void updateBookChannels() {
		int channelCount = 1;
		if (bookFile != null) {
			DefaultBookBuilder bookBuilder = DefaultBookBuilder.getInstance();
			try {
				StandardBook book = bookBuilder.readBook(bookFile);
				channelCount = book.getChannelCount();
				book.close();
			} catch (Exception ex) {
				logger.error("cannot access book file", ex);
			}
		}
		getBookChannelField().setModel(createModelForSpinner(channelCount));
	}

	private static SpinnerNumberModel createModelForSpinner(int max) {
		return new SpinnerNumberModel(1, 1, max, 1);
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

		@Override
		public void actionPerformed(ActionEvent ev) {
			File file = fileChooser.chooseBookFile(BookReporterBookPanel.this
				.getTopLevelAncestor());
			if (file == null) {
				return;
			}

			bookFile = file;
			getBookTextField().setText(bookFile.getAbsolutePath());
			updateBookChannels();
		}

	}

}
