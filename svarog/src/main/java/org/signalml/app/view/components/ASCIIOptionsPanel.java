package org.signalml.app.view.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.signalml.app.model.signal.SignalExportDescriptor;

/**
 * This class represents a panel which may be used to select a file. It contains
 * text fields where a file name can be entered, a label for this field (which
 * can be set in the constructor) and a button which opens a dialog using which
 * a file path can be selected more conveniently.
 */
public class ASCIIOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger
			.getLogger(ASCIIOptionsPanel.class);

	private JLabel selectSeparatorLabel;

	private JTextField separatorField;

	/**
	 * This is the default constructor
	 */
	public ASCIIOptionsPanel(String selectSeparatorPrompt) {
		super();
		this.selectSeparatorLabel = new JLabel(selectSeparatorPrompt);
		initialize();
	}

	/**
	 * Initializes this panel.
	 */
	private void initialize() {

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.weightx = 100;
		c.insets = new Insets(0, 2, 0, 2);
		add(selectSeparatorLabel, c);
		;
		c.weightx = 1;
		c.gridx = 1;
		add(getSeparatorField(), c);
	}

	protected JTextField getSeparatorField() {
		if (separatorField == null) {
			separatorField = new JTextField(18);
		}
		return separatorField;
	}

	public void setSeparator(String separator) {
		this.separatorField.setText(separator);
	}

	public String getSeparator() {
		return this.separatorField.getText();
	}

	public boolean isSeparatorSelected() {
		String t = getSeparatorField().getText();
		return t != null && !"".equals(t);
	}

	public void fillPanelFromModel(SignalExportDescriptor descriptor) {
		separatorField.setText(descriptor.getSeparator());
	}

	public void fillModelFromPanel(SignalExportDescriptor descriptor) {
		descriptor.setSeparator(separatorField.getText());
		descriptor.setSaveXML(false);
	}
}
