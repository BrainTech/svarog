/* FilterNotValidPanel.java created 2011-02-22
 *
 */

package org.signalml.app.view.montage.filters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class represents a panel which shows filter not valid red messages.
 *
 * @author Piotr Szachewicz
 */
public class FilterNotValidPanel extends JPanel {

	/**
	 * An array of labels used to show error messages.
	 */
	private List<JLabel> labels = new ArrayList<>();

	/**
	 * Number of labels in this panel.
	 */
	private int numberOfLabels = 2;

	/**
	 * The index of the first empty label in labels.
	 */
	private int firstEmptyLabelIndex = 0;

	public FilterNotValidPanel() {
		createInterface();
	}

	/**
	 * Creates the panel and all components.
	 */
	private void createInterface() {

		this.setLayout(new GridLayout(numberOfLabels, 1));
		this.setPreferredSize(new Dimension(1050, 50));

		createLabels();

		for (JLabel label: labels)
			this.add(label);

	}

	/**
	 * Creates all labels and puts them in the labels list.
	 */
	protected void createLabels() {
		for (int i = 0; i < numberOfLabels; i++)
			labels.add(createLabel());
	}

	/**
	 * Create a single label.
	 * @return a new label
	 */
	private JLabel createLabel() {
		JLabel label = new JLabel();
		label.setForeground(Color.red);
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}

	/**
	 * Clears all error messages from the panel.
	 */
	public void clearMessages() {
		for (JLabel label: labels)
			label.setText("");
		firstEmptyLabelIndex = 0;
	}

	/**
	 * Adds a new error message to be shown in this panel. If no label
	 * is empty, the message will be not shown.
	 * @param text the message to be shown
	 */
	protected void addMessage(String text) {
		if (!areAnyLabelsEmpty())
			return;

		labels.get(firstEmptyLabelIndex).setText(text);
		firstEmptyLabelIndex++;
	}

	/**
	 * Sets the list of messages to be shown on this panel.
	 * @param messages messages to be shown
	 */
	public void setMessages(List<String> messages) {
		clearMessages();
		for (String message: messages)
			addMessage(message);
	}

	/**
	 * Returns true if there is any empty label in the labels list,
	 * false otherwise.
	 * @return true if there is any empty label in the labels list,
	 * false otherwise.
	 */
	protected boolean areAnyLabelsEmpty() {
		if (firstEmptyLabelIndex == numberOfLabels)
			return false;
		return true;
	}

}
