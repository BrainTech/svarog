package org.signalml.app.view.signal.export;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.common.components.panels.ComponentWithLabel;

/**
 * This class represents a panel which may be used to select a file. It contains
 * text fields where a file name can be entered, a label for this field (which
 * can be set in the constructor) and a button which opens a dialog using which
 * a file path can be selected more conveniently.
 */
public class ASCIIExportOptionsPanel extends AbstractExportOptionsPanel {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger
			.getLogger(ASCIIExportOptionsPanel.class);

	private JTextField separatorField;

	/**
	 * This is the default constructor
	 */
	public ASCIIExportOptionsPanel() {
		super();
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

	@Override
	public void fillPanelFromModel(SignalExportDescriptor descriptor) {
		separatorField.setText(descriptor.getSeparator());
	}

	@Override
	public void fillModelFromPanel(SignalExportDescriptor descriptor) {
		descriptor.setSeparator(separatorField.getText());
		descriptor.setSaveXML(false);
	}

	@Override
	protected List<ComponentWithLabel> createComponents() {
		List<ComponentWithLabel> components = new ArrayList<ComponentWithLabel>();

		components.add(new ComponentWithLabel(new JLabel(_("Separator: ")), getSeparatorField()));
		components.add(new ComponentWithLabel(new JLabel(""), new JPanel()));
		return components;
	}

	@Override
	protected int getNumberOfColumns() {
		return 2;
	}
}
