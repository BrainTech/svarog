/* ChannelSignalSelectionPanel.java created 2007-10-04
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.border.EmptyBorder;


/**
 * Panel which allows to select the parameters of the channel (custom)
 * selection:
 * <ul>
 * <li>the {@link #getStartTimeSpinner() point in time} (seconds) where the
 * selection starts,</li>
 * <li>the {@link #getLengthSpinner() length} in seconds of the selection,</li>
 * <li>if this panel should allow to select the channel - the {@link
 * #getChannelComboBox() channel} for the selection.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSignalSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the spinner with the point in time (seconds) where the selection starts
	 */
	private JSpinner startTimeSpinner;
	/**
	 * the spinner with the length in seconds of the selection
	 */
	private JSpinner lengthSpinner;
	/**
	 * the combo-box which allows to select the channel for the selection
	 * (from the list of names)
	 */
	private JComboBox channelComboBox;

	/**
	 * boolean which tells if {@link #channelComboBox} should be included in
	 * this panel
	 */
	private boolean withChannelSelection;

	/**
	 * Constructor. Sets the source of messages and initializes this panel.
	 * @param messageSource the source of messages
	 * @param withChannelSelection {@code true} if this panel should allow to
	 * select a channel, {@code false} otherwise
	 */
	public  ChannelSignalSelectionPanel( boolean withChannelSelection) {
		super();
		this.withChannelSelection = withChannelSelection;
		initialize();
	}

	/**
	 * Initializes this panel with GroupLayout and two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for labels and one
	 * for spinners. This group positions the elements in two columns.</li>
	 * <li>vertical group which has 3 sub-groups - one for every row:
	 * <ul>
	 * <li>label and {@link #getStartTimeSpinner() spinner} which contains the
	 * the point in time (seconds) where the selection starts,</li>
	 * <li>label and {@link #getLengthSpinner() spinner} which contains the
	 * length in seconds of the selection,
	 * <li>if {@link #withChannelSelection} is set - label and {@link
	 * #getChannelComboBox() combo-box} which allows to select the channel
	 * for the selection (from the list of names)</li></ul>
	 * This group positions elements in rows.</li>
	 * </ul>
	 */
	private void initialize() {

		setBorder(new EmptyBorder(3,3,3,3));

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel channelLabel = new JLabel(_("Channel"));
		JLabel startTimeLabel = new JLabel(_("Start time"));
		JLabel lengthLabel = new JLabel(_("Length (seconds)"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		ParallelGroup group = layout.createParallelGroup();
		if (withChannelSelection) {
			group.addComponent(channelLabel);
		}
		group.addComponent(startTimeLabel);
		group.addComponent(lengthLabel);

		hGroup.addGroup(group);

		group = layout.createParallelGroup();
		if (withChannelSelection) {
			group.addComponent(getChannelComboBox());
		}
		group.addComponent(getStartTimeSpinner());
		group.addComponent(getLengthSpinner());

		hGroup.addGroup(group);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		if (withChannelSelection) {
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(channelLabel)
					.addComponent(getChannelComboBox())
				);
		}

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(startTimeLabel)
				.addComponent(getStartTimeSpinner())
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(lengthLabel)
				.addComponent(getLengthSpinner())
			);

		layout.setVerticalGroup(vGroup);

	}

	/**
	 * Returns the spinner with the point in time (seconds) where the
	 * selection starts.
	 * If the spinner doesn't exist it is created.
	 * <p>NOTE: the parameters of the spinner must be filled outside this
	 * panel.
	 * @return the spinner with the point in time where the selection starts
	 */
	public JSpinner getStartTimeSpinner() {
		if (startTimeSpinner == null) {
			/* model is set by the dialog */
			startTimeSpinner = new RangeToolTipSpinner();
			startTimeSpinner.setPreferredSize(new Dimension(150,25));
			startTimeSpinner.setFont(startTimeSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return startTimeSpinner;
	}

	/**
	 * Returns the spinner with the length in seconds of the selection.
	 * If the spinner doesn't exist it is created.
	 * <p>NOTE: the parameters of the spinner must be filled outside this
	 * panel.
	 * @return the spinner with the length of the selection
	 */
	public JSpinner getLengthSpinner() {
		if (lengthSpinner == null) {
			/* model is set by the dialog */
			lengthSpinner = new RangeToolTipSpinner();
			lengthSpinner.setPreferredSize(new Dimension(150,25));
			lengthSpinner.setFont(lengthSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return lengthSpinner;
	}

	/**
	 * Returns the combo-box which contains the names (labels) of channels and
	 * allows to select the channel for the selection.
	 * If the combo-box doesn't exist it is created.
	 * <p>NOTE: the combo-box must be filled outside this panel.
	 * @return the combo-box which contains the names (labels) of channels and
	 * allows to select the channel for the selection
	 */
	public JComboBox getChannelComboBox() {
		if (channelComboBox == null) {
			/* model is set by the dialog */
			channelComboBox = new JComboBox();
			channelComboBox.setPreferredSize(new Dimension(150,25));
		}
		return channelComboBox;
	}

}
