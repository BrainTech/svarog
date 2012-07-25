package org.signalml.app.view.signal.export;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;

import org.signalml.app.model.signal.SignalExportDescriptor;

/**
 * This class represents a panel which may be used to select a file. It contains
 * text fields where a file name can be entered, a label for this field (which
 * can be set in the constructor) and a button which opens a dialog using which
 * a file path can be selected more conveniently.
 */
public class EEGLabExportOptionsPanel extends AbstractExportOptionsPanel {

	private JCheckBox exportTagsCheckbox;

	/**
	 * This is the default constructor
	 */
	public EEGLabExportOptionsPanel() {
		super();
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
		if (exportTagsCheckbox == null) {
			exportTagsCheckbox = new JCheckBox(_("Export tags"));
		}
		return exportTagsCheckbox;
	}

	@Override
	public void fillPanelFromModel(SignalExportDescriptor descriptor) {
		exportTagsCheckbox.setSelected(descriptor.isExportTags());
	}

	@Override
	public void fillModelFromPanel(SignalExportDescriptor descriptor) {
		descriptor.setExportTags(exportTagsCheckbox.isSelected());
		descriptor.setSaveXML(false);
	}
}
