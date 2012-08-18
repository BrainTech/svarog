package org.signalml.app.view.common.components.panels;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.signalml.app.model.components.table.AbstractSelectionTableModel;

/**
 * This panel contains a table and select all/clear all buttons.
 *
 * @author Piotr Szachewicz
 * @param <T> the table model that is used to represent data in this panel.
 */
public abstract class AbstractSelectionPanel<T extends AbstractSelectionTableModel> extends AbstractPanel {

	/**
	 * The table model that is used to represent data in this panel.
	 */
	protected T tableModel;

	/**
	 * A list on which selections can be made.
	 */
	protected JTable table;
	/**
	 * Button for selecting all channels on the list.
	 */
	private JButton selectAllButton;
	/**
	 * Button for deselecting all channels on the list.
	 */
	private JButton clearSelectionButton;

	public AbstractSelectionPanel(String title) {
		super(title);
		initialize();
	}

	/**
	 * This method initializes this panel.
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		add(new JScrollPane(getTable()), BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);
	}

	protected JPanel createButtonsPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(getSelectAllButton());
		buttonPanel.add(getClearSelectionButton());

		return buttonPanel;
	}

	public abstract T getTableModel();

	/**
	 * Returns the list of channels which were selected using this panel.
	 * @return the list of selected channels
	 */
	public JTable getTable() {
		if (table == null)
			table = new JTable(getTableModel());
		return table;
	}

	/**
	 * Returns the button for selecting all channels.
	 * @return the button which is useful for selecting all channels from
	 * the list.
	 */
	public JButton getSelectAllButton() {
		if (selectAllButton == null) {
			selectAllButton = new JButton(new AbstractAction(_("Select all")) {

				@Override
				public void actionPerformed(ActionEvent e) {
					setAllSelected(true);
				}
			});
		}
		return selectAllButton;
	}

	/**
	 * Returns the button for deselecting all positions in the list.
	 * @return the button which can be used to clear all selections made
	 * on the list.
	 */
	public JButton getClearSelectionButton() {
		if (clearSelectionButton == null) {
			clearSelectionButton = new JButton(new AbstractAction(_("Clear all")) {

				@Override
				public void actionPerformed(ActionEvent e) {
					setAllSelected(false);
				}
			});
		}
		return clearSelectionButton;
	}

	/**
	 * Sets all channels to be selected or not.
	 * @param selected selected
	 */
	protected void setAllSelected(boolean selected) {
		getTableModel().setAllSelected(selected);
	}

	/**
	 * Sets enabled to this panel and all it's children.
	 * Clears all fields if enabled == false.
	 *
	 * @param enabled true or false
	 */
	@Override
	public void setEnabledAll(boolean enabled) {
		setEnabledToChildren(this, enabled);
	}

	/**
	 * Sets enabled to a component and all of it's children.
	 *
	 * @param component target component
	 * @param enabled true or false
	 * @param omit wheter to omit component
	 */
	private void setEnabledToChildren(Component component, boolean enabled) {

		component.setEnabled(enabled);
		if (component instanceof Container) {
			Component[] children = ((Container) component).getComponents();
			for (Component child : children) {
				setEnabledToChildren(child, enabled);
			}
		}
	}

}
