/* MonitorChannelSelectPanel.java
 *
 */
package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.JButton;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.OpenMonitorDescriptor;

/**
 * This class represents a panel for selecting channels which will be monitored.
 */
public class MonitorChannelSelectPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Logger to save history of execution at.
	 */
	protected static final Logger logger = Logger.getLogger(MonitorChannelSelectPanel.class);

	/**
	 * A list on which selections can be made.
	 */
	private CheckBoxList channelList;

	/**
	 * Button for selecting all channels on the list.
	 */
	private JButton selectAllButton;

	/**
	 * Button for deselecting all channels on the list.
	 */
	private JButton clearSelectionButton;

	/**
	 * This is the default constructor
	 */
	public MonitorChannelSelectPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		add(new JScrollPane(getChannelList()), BorderLayout.CENTER);

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Select channels to be monitored")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(getSelectAllButton());
		buttonPanel.add(getClearSelectionButton());
		add(buttonPanel, BorderLayout.SOUTH);

	}

	/**
	 * Returns the list of channels which were selected using this panel.
	 * @return the list of selected channels
	 */
	public JList getChannelList() {
		if (channelList == null) {
			channelList = new CheckBoxList();
		}
		return channelList;
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
					channelList.clearSelection();
					int end = channelList.getModel().getSize() - 1;
					if (end >= 0) {
						channelList.setSelectionInterval(0, end);
					}
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
					channelList.clearSelection();
				}
			});
		}
		return clearSelectionButton;
	}

	/**
	 * Fills the fields of this panel from the given model.
	 * @param openMonitorDescriptor the model from which this dialog will be
	 * filled.
	 */
	public void fillPanelFromModel(OpenMonitorDescriptor openMonitorDescriptor) {

		String[] channelLabels = openMonitorDescriptor.getChannelLabels();

		if (channelLabels == null) {
			Integer channelCount = openMonitorDescriptor.getChannelCount();
			if (channelCount == null)
				channelCount = 0;
			channelLabels = new String[channelCount];
			for (int i = 0; i < channelCount; i++)
				channelLabels[i] = Integer.toBinaryString(i);
		}

		getChannelList().setListData(channelLabels);

	}

	/**
	 * Fills the model with the data from this panel (user input).
	 * @param openMonitorDescriptor the model to be filled.
	 */
	public void fillModelFromPanel(OpenMonitorDescriptor openMonitorDescriptor) {
		try {
			openMonitorDescriptor.setSelectedChannelList(getChannelList().getSelectedValues());
		} catch (Exception ex) {
			java.util.logging.Logger.getLogger(MonitorChannelSelectPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
