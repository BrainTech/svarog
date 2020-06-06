package org.signalml.app.view.signal.export;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.signalml.app.model.signal.SignalExportDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.panels.ComponentWithLabel;

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
	}

	protected JCheckBox getTagExportField() {
		if (exportTagsCheckbox == null) {
			exportTagsCheckbox = new JCheckBox();
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

	@Override
	protected List<ComponentWithLabel> createComponents() {
		List<ComponentWithLabel> components = new ArrayList<>();

		components.add(new ComponentWithLabel(new JLabel(_("Export tags")), getTagExportField()));
		components.add(new ComponentWithLabel(new JLabel(""), new JPanel()));
		return components;
	}

	@Override
	protected int getNumberOfColumns() {
		return 2;
	}
}
