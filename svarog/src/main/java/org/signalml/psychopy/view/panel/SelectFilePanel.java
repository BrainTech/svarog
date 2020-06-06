package org.signalml.psychopy.view.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;

public abstract class SelectFilePanel extends JPanel {
	final static int DEFAULT_PATH_COLUMNS_NUMBER = 18;
	protected final JFileChooser fileChooser;
	private JTextField path;
	private JLabel pathLabel;
	private JButton browseButton;
	private JPanel mainPanel;

	SelectFilePanel() {
		this.mainPanel = new JPanel();
		this.fileChooser = createFileChooser();
		this.browseButton = createBrowseButton();
		this.path = new JTextField(DEFAULT_PATH_COLUMNS_NUMBER);
		this.pathLabel = createPathLabel();
		this.pathLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		initializeUI();
		connectListeners();
	}

	abstract protected JLabel createInfoLabel();
	abstract protected String createBorderTitle();
	abstract protected int showDialog();

	private void initializeUI() {
		
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.weightx = 100;
		c.insets = new Insets(0, 2, 0, 2);
		this.mainPanel.add(pathLabel, c);
		c.weightx = 1;
		c.gridx = 1;
		this.mainPanel.add(path, c);
		c.weightx = 0;
		c.gridx = 2;
		this.mainPanel.add(browseButton, c);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		CompoundBorder border = new CompoundBorder(new TitledBorder(createBorderTitle()),
							   new EmptyBorder(3, 3, 3, 3));
		this.setBorder(border);
		
		JLabel infoLabel = createInfoLabel();
		
		Box infoLabelBox = Box.createHorizontalBox();
		infoLabelBox.add(infoLabel );
		infoLabelBox.add( Box.createHorizontalGlue() );
		
		infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
		this.add(infoLabelBox);
		this.add(this.mainPanel);
	}

	private void connectListeners() {
		this.browseButton.addActionListener(event -> {onBrowse();});
	}

	private void onBrowse() {
		int returnVal = this.showDialog();
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

	public void setPath(String path) { this.path.setText(path); }

	void makePathAbsolute() {
		String home = System.getProperty("user.home");
		if (
				!this.selectedPath().startsWith("~")
						&& !this.selectedPath().startsWith("/")
				) {
			File file = new File(home + "/" + this.selectedPath());
			this.setPath(file.getAbsolutePath());
		} else if (this.selectedPath().startsWith("~")) {
			File file = new File(home + this.selectedPath().substring(1));
			this.setPath(file.getAbsolutePath());
		}
	}

}
