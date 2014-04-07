package org.signalml.plugin.bookreporter.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.bookreporter.data.BookReporterParameters;
import org.signalml.plugin.export.view.FileChooser;
import static org.signalml.plugin.i18n.PluginI18n._;

/**
 * @author piotr@develancer.pl
 * (based on Michal Dobaczewski's NewStagerBookPanel)
 */
public class BookReporterDirPanel extends JPanel {

	private JTextField dirTextField;
	private JButton chooseDirButton;

	private final FileChooser fileChooser;

	private File dirFile;

	public BookReporterDirPanel(FileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(new TitledBorder(
			_("Choose output directory")), new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel outDirLabel = new JLabel(_("Output directory"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup()
			.addComponent(outDirLabel));

		hGroup.addGroup(layout.createParallelGroup().addComponent(
			getDirTextField()));

		hGroup.addGroup(layout.createParallelGroup().addComponent(
			getChooseDirButton()));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(outDirLabel).addComponent(getDirTextField())
			.addComponent(getChooseDirButton()));

		layout.setVerticalGroup(vGroup);

	}

	public JTextField getDirTextField() {
		if (dirTextField == null) {
			dirTextField = new JTextField();
			dirTextField.setPreferredSize(new Dimension(300, 25));
			dirTextField.setEditable(false);
		}
		return dirTextField;
	}

	public JButton getChooseDirButton() {
		if (chooseDirButton == null) {
			chooseDirButton = new JButton(new ChooseOutDirAction());
		}
		return chooseDirButton;
	}

	public void fillPanelFromModel(BookReporterParameters parameters) {
		String path = parameters.outputDirPath;
		if (path.isEmpty()) {
			dirFile = null;
			getDirTextField().setText("");
		} else {
			dirFile = new File(path);
			getDirTextField().setText(path);
		}
	}

	public void fillModelFromPanel(BookReporterParameters parameters) {
		parameters.outputDirPath = (dirFile == null) ? ""
			: dirFile.getAbsolutePath();
	}

	public void validatePanel(ValidationErrors errors) {
		String outputDirPath = getDirTextField().getText();
		if (dirFile == null || !dirFile.exists() || !dirFile.isDirectory() || !dirFile.canWrite()) {
			errors.addError(_("Directory not chosen, doesn't exist or unreadable"));
		}
	}

	protected class ChooseOutDirAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChooseOutDirAction() {
			super(_("Choose..."));
			putValue(
				AbstractAction.SMALL_ICON,
				IconUtils
				.loadClassPathIcon("org/signalml/app/icon/clamp.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,
				_("Choose an output directory for PNG charts"));
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			File file = fileChooser.chooseWorkingDirectory(
				BookReporterDirPanel.this.getTopLevelAncestor(),
				BookReporterDirPanel.this.dirFile
			);
			if (file == null) {
				return;
			}

			dirFile = file;
			getDirTextField().setText(dirFile.getAbsolutePath());
		}

	}

}
