package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.signalml.app.model.signal.SignalExportDescriptor;

/**
 * This class represents a panel which may be used to select a file. It contains
 * text fields where a file name can be entered, a label for this field (which
 * can be set in the constructor) and a button which opens a dialog using which
 * a file path can be selected more conveniently.
 */
public class EEGLabOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger
			.getLogger(ASCIIOptionsPanel.class);

	private JCheckBox tagExportField;

	private String selectTagPrompt;

	/**
	 * This is the default constructor
	 */
	public EEGLabOptionsPanel(String selectTagPrompt) {
		super();
		this.selectTagPrompt = selectTagPrompt;
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
		add(getTagExportField(), c);
	}

	protected JCheckBox getTagExportField() {
		if (tagExportField == null) {
			tagExportField = new JCheckBox(_(selectTagPrompt));
		}
		return tagExportField;
	}

	public void setTagExport(boolean checked) {
		this.tagExportField.setSelected(checked);
	}

	public boolean isTagExport() {
		return this.tagExportField.isSelected();
	}

	public void fillPanelFromModel(SignalExportDescriptor descriptor) {
		setTagExport(descriptor.isExportTag());
	}

	public void fillModelFromPanel(SignalExportDescriptor descriptor) {
		descriptor.setExportTag(isTagExport());
		descriptor.setSaveXML(false);
	}
}
