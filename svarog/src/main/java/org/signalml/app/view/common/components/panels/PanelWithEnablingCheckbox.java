package org.signalml.app.view.common.components.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public abstract class PanelWithEnablingCheckbox<T extends AbstractPanel> extends AbstractPanel implements ActionListener {

	private JCheckBox enableFilteringCheckbox;
	protected T panel;

	public PanelWithEnablingCheckbox(String title) {
		super(title);

		createInterface();
	}

	protected void createInterface() {
		setLayout(new BorderLayout());
		add(getEnableFilteringCheckbox(), BorderLayout.NORTH);
		add(getPanel(), BorderLayout.CENTER);
	}

	public JCheckBox getEnableFilteringCheckbox() {
		if (enableFilteringCheckbox == null) {
			enableFilteringCheckbox = new JCheckBox(getEnableCheckboxText());

			enableFilteringCheckbox.addActionListener(this);
			actionPerformed(null);
		}
		return enableFilteringCheckbox;
	}

	protected abstract String getEnableCheckboxText();
	protected abstract T getPanel();

	@Override
	public void actionPerformed(ActionEvent event) {
		getPanel().setEnabledAll(isCheckboxSelected());
	}

	protected boolean isCheckboxSelected() {
		return getEnableFilteringCheckbox().isSelected();
	}

}
