/* ChannelSpacePanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.action.util.ListSelectAllAction;
import org.signalml.app.action.util.ListSelectInvertAction;
import org.signalml.app.action.util.ListSelectNoneAction;
import org.signalml.domain.signal.space.ChannelSpace;
import org.signalml.domain.signal.space.ChannelSpaceType;
import org.signalml.domain.signal.space.SignalSourceLevel;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;

import org.springframework.validation.Errors;

/**
 * Panel which allows to select channels from the {@link #getChannelList()
 * list} containing their names (labels).
 * Contains two sub-panels:
 * <ul>
 * <li>the {@link #getChannelScrollPane() pane} with the list of channels,
 * </li><li>the panel with 3 buttons (from top to bottom):
 * <ul><li>to select {@link #getChannelSelectAllButton() all} channel,</li>
 * <li>to select {@link #getChannelSelectNoneButton() no} channels,</li>
 * <li>to {@link #getChannelSelectInvertButton() invert} the selection
 * of channels.</li></ul></li></ul>
 * Channels can be selected from the list of {@link #getSourceChannels()}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSpacePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ChannelSpacePanel.class);

	/**
	 * the list with the names of channels;
	 * multiple selection is allowed
	 */
	private JList channelList;
	/**
	 * the scroll pane with the {@link #channelList list} of names of channels
	 */
	private JScrollPane channelScrollPane;

	/**
	 * the button which selects all channels on the {@link #channelList list}
	 */
	private JButton channelSelectAllButton;
	/**
	 * the button which inverts the selection on the {@link #channelList list}
	 */
	private JButton channelSelectInvertButton;
	/**
	 * the button which makes there is no element selected on the
	 * {@link #channelList list}
	 */
	private JButton channelSelectNoneButton;

	/**
	 * the array of names (labels) of all montage channels available
	 */
	private String[] channels;

	/**
	 * Constructor. Initializes the panel.
	 */
	public ChannelSpacePanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with BorderLayout and two sub-panels:
	 * <ul>
	 * <li>the {@link #getChannelScrollPane() pane} with the list of channels,
	 * </li><li>the panel with 3 buttons (from top to bottom):
	 * <ul><li>to select {@link #getChannelSelectAllButton() all} channel,</li>
	 * <li>to select {@link #getChannelSelectNoneButton() no} channels,</li>
	 * <li>to {@link #getChannelSelectInvertButton() invert} the selection
	 * of channels.</li></ul></li></ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		setBorder(new CompoundBorder(
		                  new TitledBorder(_("Channel selection")),
		                  new EmptyBorder(3,3,3,3)
		          ));

		JPanel channelButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 3, 3));
		channelButtonPanel.add(getChannelSelectAllButton());
		channelButtonPanel.add(getChannelSelectNoneButton());
		channelButtonPanel.add(getChannelSelectInvertButton());

		add(getChannelScrollPane(), BorderLayout.CENTER);
		add(channelButtonPanel, BorderLayout.SOUTH);

	}

	/**
	 * Returns the list with the names (labels) of channels.
	 * If the list doesn't exist it is created with multiple selection
	 * allowed.
	 * @return the list with the names (labels) of channels
	 */
	public JList getChannelList() {
		if (channelList == null) {
			channelList = new JList();
			channelList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		return channelList;
	}

	/**
	 * Returns the pane with the {@link #getChannelList() list} of the names
	 * (labels) of channels.
	 * If the pane doesn't exist it is created.
	 * @return the pane with the list of the names of channels
	 */
	public JScrollPane getChannelScrollPane() {
		if (channelScrollPane == null) {
			channelScrollPane = new JScrollPane(getChannelList());
			channelScrollPane.setPreferredSize(new Dimension(300,200));
		}
		return channelScrollPane;
	}

	/**
	 * Returns the button which selects all channels on the
	 * {@link #getChannelList() list}.
	 * If the button doesn't exist it is created.
	 * @return the button which selects all channels on the list
	 */
	public JButton getChannelSelectAllButton() {
		if (channelSelectAllButton == null) {
			channelSelectAllButton = new JButton(new ListSelectAllAction( getChannelList()));
		}
		return channelSelectAllButton;
	}

	/**
	 * Returns the button which makes there is no element selected on the
	 * {@link #getChannelList() list}.
	 * If the button doesn't exist it is created.
	 * @return the button which makes there is no element selected on the list
	 */
	public JButton getChannelSelectNoneButton() {
		if (channelSelectNoneButton == null) {
			channelSelectNoneButton = new JButton(new ListSelectNoneAction( getChannelList()));
		}
		return channelSelectNoneButton;
	}

	/**
	 * Returns the button which inverts the selection on the
	 * {@link #getChannelList() list}.
	 * If the button doesn't exist it is created.
	 * @return the button which inverts the selection on the list
	 */
	public JButton getChannelSelectInvertButton() {
		if (channelSelectInvertButton == null) {
			channelSelectInvertButton = new JButton(new ListSelectInvertAction( getChannelList()));
		}
		return channelSelectInvertButton;
	}

	/**
	 * Using the {@link ChannelSpace} from given {@link SignalSpace model}
	 * sets which elements on the {@link #getChannelList() list} are
	 * selected.
	 * @param space the signal space
	 */
	public void fillPanelFromModel(SignalSpace space) {

		ChannelSpaceType channelSpaceType = space.getChannelSpaceType();
		JList list = getChannelList();
		if (channelSpaceType == ChannelSpaceType.WHOLE_SIGNAL) {
			list.setSelectionInterval(0, list.getModel().getSize()-1);
		} else {

			ChannelSpace channelSpace = space.getChannelSpace();
			list.clearSelection();

			if (channels != null && channelSpace != null) {

				for (int i=0; i<channels.length; i++) {
					if (channelSpace.isChannelSelected(i)) {
						list.addSelectionInterval(i, i);
					}
				}

			}

		}
		//previously this panel allowed to select the signal source level
		//now it is always set to assembled
		space.setSignalSourceLevel(SignalSourceLevel.ASSEMBLED);

	}

	/**
	 * Stores the information which channels on the {@link #getChannelList()
	 * list} are selected in the {@link SignalSpace model}.
	 * In order to do it:
	 * <ul>
	 * <li>if all channels are selected {@link SignalSpace#setChannelSpaceType(
	 * ChannelSpaceType) sets} the {@link ChannelSpaceType type} of
	 * {@link ChannelSpace} in the model,</li>
	 * <li>otherwise stores this information in channel space and sets in the
	 * model,</li></ul>
	 * @param space the signal space
	 */
	public void fillModelFromPanel(SignalSpace space) {

		boolean all = true;

		JList list = getChannelList();
		ChannelSpace channelSpace = space.getChannelSpace();
		if (channelSpace != null) {
			channelSpace.clear();
		} else {
			channelSpace = new ChannelSpace();
		}

		if (channels != null) {

			for (int i=0; i<channels.length; i++) {

				if (list.isSelectedIndex(i)) {
					channelSpace.addChannel(i);
				} else {
					all = false;
				}

			}

		}

		if (all) {
			space.setChannelSpaceType(ChannelSpaceType.WHOLE_SIGNAL);
			space.setChannelSpace(null);
		} else {
			space.setChannelSpaceType(ChannelSpaceType.SELECTED);
			space.setChannelSpace(channelSpace);
		}

	}

	/**
	 * Returns the array of names (labels) of montage channels.
	 * @return the array of names (labels) of montage channels
	 */
	public String[] getChannels() {
		return channels;
	}

	/**
	 * Sets the array of names (labels) of montage channels.
	 * @param channels the array of names (labels) of montage channels
	 */
	public void setChannels(String[] channels) {
		if (this.channels != channels) {
			this.channels = channels;

			DefaultListModel listModel = new DefaultListModel();
			for (int i=0; i<channels.length; i++) {
				listModel.addElement(channels[i]);
			}

			JList list = getChannelList();
			list.setModel(listModel);
			list.clearSelection();
		}
	}

	/**
	 * Sets the names of {@link #setSourceChannels(String[]) source} and
	 * {@link #setChannels(String[]) montage} channels using the given
	 * {@link SignalSpaceConstraints parameters} of the signal.
	 * @param constraints the parameters of the signal
	 */
	public void setConstraints(SignalSpaceConstraints constraints) {
		setChannels(constraints.getChannels());
	}

	/**
	 * Validates this panel.
	 * This panel is valid if there is at least one channel selected.
	 * @param errors the object in which errors are stored
	 */
	public void validatePanel(Errors errors) {

		if (getChannelList().isSelectionEmpty()) {
			errors.rejectValue("channelSpace", "error.signalSpace.noChannels");
		}

	}

}
