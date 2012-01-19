/* SignalSelectionTypePanel.java created 2007-10-04
 *
 */
package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.signalml.plugin.export.signal.SignalSelectionType;

/**
 * Panel which allows to select the {@link SignalSelectionType type} of
 * the selection:
 * <ul>
 * <li>the {@link #getPageRadio() page} selection,</li>
 * <li>the {@link #getBlockRadio() block} selection,</li>
 * <li>the {@link #getChannelRadio() channel} selection</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSelectionTypePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the radio button which tells that page selection should be used
	 * (the selection that contains whole pages)
	 */
	private JRadioButton pageRadio = null;
	/**
	 * the radio button which tells that block selection should be used
	 * (the selection that contains whole blocks)
	 */
	private JRadioButton blockRadio = null;
	/**
	 * the radio button which tells that channel (custom) selection should be
	 * used
	 */
	private JRadioButton channelRadio = null;

	/**
	 * the group of radio buttons containing
	 * {@link #pageRadio}, {@link #blockRadio} and {@link #channelRadio}
	 */
	private ButtonGroup radioGroup;

	/**
	 * Constructor. Initializes the panel.
	 */
	public SignalSelectionTypePanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with box layout and 3 radio buttons (from left to
	 * right):
	 * <ul>
	 * <li>the {@link #getPageRadio() button} which tells that page selection
	 * should be used,</li>
	 * <li>the {@link #getBlockRadio() button} which tells that block selection
	 * should be used,</li>
	 * <li>the {@link #getChannelRadio() button} which tells that channel
	 * selection should be used,</li></ul>
	 */
	private void initialize() {

		setBorder(BorderFactory.createTitledBorder(_("Selection type")));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		radioGroup = new ButtonGroup();

		add(getPageRadio());
		add(getBlockRadio());
		add(getChannelRadio());

		getPageRadio().setSelected(true);

	}

	/**
	 * Returns the radio button which tells that page selection should be used
	 * (the selection that contains whole pages).
	 * If the button doesn't exist it is created and added to the group.
	 * @return the radio button which tells that page selection should be used
	 */
	public JRadioButton getPageRadio() {
		if (pageRadio == null) {
			pageRadio = new JRadioButton();
			pageRadio.setText(_("Page selection"));
			pageRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(pageRadio);
		}
		return pageRadio;
	}

	/**
	 * Returns the radio button which tells that block selection should be used
	 * (the selection that contains whole blocks).
	 * If the button doesn't exist it is created and added to the group.
	 * @return the radio button which tells that block selection should be used
	 */
	public JRadioButton getBlockRadio() {
		if (blockRadio == null) {
			blockRadio = new JRadioButton();
			blockRadio.setText(_("Block selection"));
			blockRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(blockRadio);
		}
		return blockRadio;
	}

	/**
	 * Returns the radio button which tells that channel (custom) selection
	 * should be used.
	 * If the button doesn't exist it is created and added to the group.
	 * @return the radio button which tells that channel selection should be
	 * used
	 */
	public JRadioButton getChannelRadio() {
		if (channelRadio == null) {
			channelRadio = new JRadioButton();
			channelRadio.setText(_("Free (channel) selection"));
			channelRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(channelRadio);
		}
		return channelRadio;
	}

}
