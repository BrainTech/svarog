package org.signalml.psychopy.view.panel;

import java.awt.Dimension;
import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;

import javax.swing.*;
import java.io.File;

public abstract class SelectFilePanel extends JPanel {
	final static int DEFAULT_PATH_COLUMNS_NUMBER = 18;
	private JFileChooser fileChooser;
	private JTextField path;
	private JLabel pathLabel;
	private JButton browseButton;

	SelectFilePanel() {
		this.fileChooser = createFileChooser();
		this.browseButton = createBrowseButton();
		this.path = new JTextField(DEFAULT_PATH_COLUMNS_NUMBER);
		this.pathLabel = createPathLabel();
		this.pathLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		initializeUI();
		connectListeners();
	}

	private void initializeUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(pathLabel);
		pathLabel.setPreferredSize(new Dimension(180,30));
		this.add(path);
		this.add(browseButton);
	}

	private void connectListeners() {
		this.browseButton.addActionListener(event -> {onBrowse();});
	}

	private void onBrowse() {
		int returnVal = this.fileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = this.fileChooser.getSelectedFile();
			this.path.setText(file.getPath());
		}
	}

	private JButton createBrowseButton() {
		Icon icon = IconUtils.loadClassPathIcon("org/signalml/app/icon/fileopen.png");
		JButton button = new JButton();
		button.setText(_("Browse"));
		button.setIcon(icon);
		return button;
	}

	abstract JLabel createPathLabel();

	abstract JFileChooser createFileChooser();

	public abstract void fillPanelFromModel(Object model);

	public abstract void fillModelFromPanel(Object model);

	public abstract void validate(ValidationErrors errors);

	public String selectedPath() {
		return this.path.getText();
	}

	public void clearPath() {
		this.path.setText("");
	}

	public void setPath(String path) { this.path.setText(path); }

}
