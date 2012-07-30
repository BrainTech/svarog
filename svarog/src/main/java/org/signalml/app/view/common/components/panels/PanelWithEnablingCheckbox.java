package org.signalml.app.view.common.components.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public abstract class PanelWithEnablingCheckbox<T extends AbstractPanel> extends AbstractPanel implements ActionListener {

	private JCheckBox checkbox;
	protected T panel;

	public PanelWithEnablingCheckbox(String title) {
		super(title);

		createInterface();
	}

	protected void createInterface() {
		setLayout(new BorderLayout());
		add(getCheckbox(), BorderLayout.NORTH);
		add(getPanel(), BorderLayout.CENTER);
	}

	private JCheckBox getCheckbox() {
		if (checkbox == null) {
			checkbox = new JCheckBox(getEnableCheckboxText());

			checkbox.addActionListener(this);
			actionPerformed(null);
		}
		return checkbox;
	}

	protected abstract String getEnableCheckboxText();
	protected abstract T getPanel();

	@Override
	public void actionPerformed(ActionEvent event) {
		getPanel().setEnabledAll(isCheckboxSelected());
	}

	protected boolean isCheckboxSelected() {
		return getCheckbox().isSelected();
	}

	public void setCheckboxSelected(boolean selected) {
		getCheckbox().setSelected(selected);
		actionPerformed(null);
	}

}
