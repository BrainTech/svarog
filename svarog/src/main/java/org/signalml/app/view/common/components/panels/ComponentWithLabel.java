package org.signalml.app.view.common.components.panels;

import java.awt.Component;
import javax.swing.JLabel;

public class ComponentWithLabel {

	private JLabel label;
	private Component component;

	public ComponentWithLabel(JLabel label, Component component) {
		this.label = label;
		this.component = component;
	}

	public JLabel getLabel() {
		return label;
	}

	public void setLabel(JLabel label) {
		this.label = label;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

}