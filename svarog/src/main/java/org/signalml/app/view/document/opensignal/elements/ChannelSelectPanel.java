package org.signalml.app.view.document.opensignal.elements;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;
import org.signalml.app.model.document.opensignal.elements.SignalSource;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * This class represents a panel for selecting channels which will be monitored.
 *
 * @author Piotr Szachewicz
 */
public class ChannelSelectPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	/**
	 * Logger to save history of execution at.
	 */
	protected static final Logger logger = Logger.getLogger(ChannelSelectPanel.class);
	/**
	 * A list on which selections can be made.
	 */
	private ChannelSelectTable channelSelectTable;
	/**
	 * Button for selecting all channels on the list.
	 */
	private JButton selectAllButton;
	/**
	 * Button for deselecting all channels on the list.
	 */
	private JButton clearSelectionButton;

	/**
	 * This is the default constructor.
	 */
	public ChannelSelectPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this panel.
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		add(new JScrollPane(getChannelSelectTable()), BorderLayout.CENTER);

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Available channels")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		add(createButtonsPanel(), BorderLayout.SOUTH);

	}

	protected JPanel createButtonsPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(getSelectAllButton());
		buttonPanel.add(getClearSelectionButton());

		return buttonPanel;
	}

	/**
	 * Returns the list of channels which were selected using this panel.
	 * @return the list of selected channels
	 */
	public ChannelSelectTable getChannelSelectTable() {
		if (channelSelectTable == null) {
			channelSelectTable = new ChannelSelectTable();
		}
		return channelSelectTable;
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
			selectAllButton.setEnabled(false);
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
			clearSelectionButton.setEnabled(false);
		}
		return clearSelectionButton;
	}

	/**
	 * Sets all channels to be selected or not.
	 * @param selected selected
	 */
	protected void setAllSelected(boolean selected) {
		getChannelSelectTable().setAllSelected(selected);
	}

	/**
	 * Sets enabled to this panel and all it's children.
	 * Clears all fields if enabled == false.
	 *
	 * @param enabled true or false
	 */
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

	/**
	 * Fills this panel from an {@link AmplifierConnectionDescriptor} object.
	 *
	 * @param descriptor
	 */
	public void fillPanelFromModel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		getChannelSelectTable().fillTableFromModel(openSignalDescriptor);

		if (openSignalDescriptor instanceof ExperimentDescriptor) {
			boolean enableSelection = ((ExperimentDescriptor) openSignalDescriptor).getStatus() == ExperimentStatus.NEW;
			getSelectAllButton().setEnabled(enableSelection);
			getClearSelectionButton().setEnabled(enableSelection);
		}
	}

	public void fillModelFromPanel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		String[] channelLabels = getChannelSelectTable().getChannelLabels();
		openSignalDescriptor.setChannelLabels(channelLabels);
	}

	public void preparePanelForSignalSource(SignalSource signalSource) {
	}

}
