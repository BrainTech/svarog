package org.signalml.plugin.psychopy.ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

public class StartPsychopyExperimentDialog extends JDialog {
	private static final String TRANSLATIONS_BUNDLE = "org.signalml.plugin.psychopy.i18n.translations";
	private static ResourceBundle translations = getTranslations();
	private JFileChooser experimentFileChooser = getExperimentPathFileChooser();
	private JFileChooser outputDirectoryFileChooser = getOutputDirectoryFileChooser();

	private JPanel contentPane;
	private JButton buttonRun;
	private JButton buttonCancel;
	private JTextField experimentPath;
	private JButton selectExperimentPath;
	private JTextField outputDirectoryPath;
	private JButton selectOutputDirectoryPath;

	public StartPsychopyExperimentDialog() {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonRun);

		buttonRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		selectExperimentPath.addActionListener(event -> {onSelectExperimentPath();});
		selectOutputDirectoryPath.addActionListener(event -> {onSelectOutputDirectoryPath();});
	}

	private void onOK() {
		if (experimentPathIsValid() && outputDirectoryPathIsValid()) {
			dispose();
		}
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

	private void onSelectExperimentPath() {
		selectPathAndUpdateSelection(experimentFileChooser, experimentPath);
	}

	private void onSelectOutputDirectoryPath() {
		selectPathAndUpdateSelection(outputDirectoryFileChooser, outputDirectoryPath);
	}

	private void selectPathAndUpdateSelection(JFileChooser fileChooser, JTextField pathLabel) {
		int returnVal = fileChooser.showOpenDialog(
			StartPsychopyExperimentDialog.this
		);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			pathLabel.setText(file.getPath());
		}
	}

	private static JFileChooser getExperimentPathFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(
			new FileNameExtensionFilter(
				translations.getString("experimentPathFilterDescription"),
				"psyexp"
			)
		);
		return fileChooser;
	}

	private static JFileChooser getOutputDirectoryFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		return fileChooser;
	}

	private static ResourceBundle getTranslations() {
		Locale currentLocale = Locale.getDefault();
		return ResourceBundle.getBundle(TRANSLATIONS_BUNDLE, currentLocale);
	}

	private boolean experimentPathIsValid() {
		return isValidPath(
			experimentPath.getText(),
			"errorMessageEmptyPathDetailsExp"
		);
	}

	private boolean outputDirectoryPathIsValid() {
		boolean isValid = isValidPath(
			outputDirectoryPath.getText(),
			"errorMessageEmptyPathDetailsDir"
		);
		if (!isValid) {
			outputDirectoryPath.setText("");
			return false;
		} else if (!isEmptyDirectory(outputDirectoryPath.getText())) {
			showErrorMessage(
				"errorMessageNotEmptyDirectory",
				outputDirectoryPath.getText(),
				false
			);
			outputDirectoryPath.setText("");
			return false;
		} else {
			return true;
		}
	}

	private boolean isValidPath(String path, String detailsKey) {
		if (path == null || path.isEmpty()) {
			showErrorMessage("errorMessageEmptyPath", detailsKey, true);
			return false;
		} else if (!pathExists(path)) {
			showErrorMessage("errorMessageInvalidPath", path, false);
			return false;
		} else {
			return true;
		}
	}

	private void showErrorMessage(String messageKey, String detailsKey, boolean translateDetails) {
		String details = detailsKey;
		if (translateDetails) {
			details = translations.getString(detailsKey);
		}
		JOptionPane.showMessageDialog(
				StartPsychopyExperimentDialog.this,
				String.format(
					translations.getString(messageKey),
					details
				),
				translations.getString("errorDialogTitle"),
				JOptionPane.ERROR_MESSAGE
		);
	}

	private static boolean pathExists(String path) {
		File file = new File(path);
		return file.exists();
	}

	private static boolean isEmptyDirectory(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			String[] files = file.list();
			return files == null || files.length == 0;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		StartPsychopyExperimentDialog dialog = new StartPsychopyExperimentDialog();
		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}
}
