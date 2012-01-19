/* CheckBoxList.java created 2010-11-19
 *
 */
package org.signalml.app.view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

/**
 * This class represents a {@link JList} in which elements can be also selected
 * using checkboxes.
 *
 * @author Piotr Szachewicz
 */
public class CheckBoxList extends JList {

	/**
	 * Constructor. Creates and initializes new CheckBoxList.
	 */
	public CheckBoxList() {
		super();

		setCellRenderer(new CheckBoxListCellRenderer());
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setSelectionModel(new DefaultListSelectionModel() {

			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (super.isSelectedIndex(index0)) {
					super.removeSelectionInterval(index0, index1);
				} else {
					super.addSelectionInterval(index0, index1);
				}
			}
		});
	}
}

/**
 * This class represents a {@link ListCellRenderer} for {@link CheckBoxList}.
 * It is responsible for displaying and controlling the checkbox used in the
 * {@link CheckBoxList}.
 * @author Piotr Szachewicz
 */
class CheckBoxListCellRenderer extends JComponent implements ListCellRenderer {

	/**
	 * {@link DefaultListCellRenderer} reponsible for default operations and
	 * rendering of the element of the {@link CheckBoxList}.
	 */
	private DefaultListCellRenderer defaultComp;

	/**
	 * A checkbox for a single {@link CheckBoxList} element.
	 */
	private JCheckBox CheckBox;

	/**
	 * Constructor. Creates a new {@link CheckBoxListCellRenderer}.
	 */
	public CheckBoxListCellRenderer() {
		setLayout(new BorderLayout());
		defaultComp = new DefaultListCellRenderer();
		CheckBox = new JCheckBox();
		add(CheckBox, BorderLayout.WEST);
		add(defaultComp, BorderLayout.CENTER);
	}

	/**
	 * Returns a component that has been configured to display the specified
	 * value.
	 * @param list the JList we're painting.
	 * @param value the value returned by list.getModel().getElementAt(index).
	 * @param index the cells index.
	 * @param isSelected true if the specified cell was selected.
	 * @param cellHasFocus true if the specified cell has the focus.
	 * @return a component for displaying the specified value
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		defaultComp.getListCellRendererComponent(list, value, index,
			isSelected, cellHasFocus);
		CheckBox.setSelected(isSelected);
		CheckBox.setBackground(Color.white);

		return this;

	}

}
