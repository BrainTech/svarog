package org.signalml.app.view.signal.export;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.*;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.common.components.panels.ComponentWithLabel;

/**
 * This class represents a panel which may be used to select a file. It contains
 * text fields where a file name can be entered, a label for this field (which
 * can be set in the constructor) and a button which opens a dialog using which
 * a file path can be selected more conveniently.
 */
public class CsvExportOptionsPanel extends AbstractExportOptionsPanel {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger
			.getLogger(CsvExportOptionsPanel.class);

	private static final String[] SEPARATORS_LABELS = new String[] {_("comma"), _("space"), _("tab")};
	private static final String[] SEPARATORS = new String[] {",", " ", "\t"};

	private JComboBox<String> separatorField;
	private JCheckBox addChannelNames;

	/**
	 * This is the default constructor
	 */
	public CsvExportOptionsPanel() {
		super();
	}

	private JComboBox getSeparatorField() {
		if (separatorField == null) {
			separatorField = new JComboBox<String>(SEPARATORS_LABELS);
		}
		return separatorField;
	}

	private JCheckBox getAddChannelNamesField() {
		if (addChannelNames == null) {
			addChannelNames = new JCheckBox();
		}
		return addChannelNames;
	}

	public void setSeparator(String separator) {
		int index = Arrays.asList(SEPARATORS).indexOf(separator);
		this.separatorField.setSelectedItem(SEPARATORS_LABELS[index]);
	}

	public String getSeparator() {
		int index = this.separatorField.getSelectedIndex();
		return SEPARATORS[index];
	}

	@Override
	public void fillPanelFromModel(SignalExportDescriptor descriptor) {
		this.setSeparator(descriptor.getSeparator());
		addChannelNames.setSelected(descriptor.isExportChannelNames());
	}

	@Override
	public void fillModelFromPanel(SignalExportDescriptor descriptor) {
		descriptor.setSeparator(this.getSeparator());
		descriptor.setExportChannelNames(addChannelNames.isSelected());
		descriptor.setSaveXML(false);
	}

	@Override
	protected List<ComponentWithLabel> createComponents() {
		List<ComponentWithLabel> components = new ArrayList<ComponentWithLabel>();

		components.add(new ComponentWithLabel(new JLabel(_("Separator: ")), getSeparatorField()));
		components.add(
				new ComponentWithLabel(
						new JLabel(_("Add channels names to csv file")),
						getAddChannelNamesField()
				)
		);
		return components;
	}

	@Override
	protected int getNumberOfColumns() {
		return 1;
	}
}
