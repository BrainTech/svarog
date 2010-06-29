/* ChannelSignalSelectionPanel.java created 2007-10-04
 *
 */
package org.signalml.app.view.element;

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

import org.springframework.context.support.MessageSourceAccessor;

/** ChannelSignalSelectionPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSignalSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JSpinner startTimeSpinner;
	private JSpinner lengthSpinner;
	private JComboBox channelComboBox;

	private boolean withChannelSelection;

	/**
	 * This is the default constructor
	 * @param noChannelSelection
	 */
	public ChannelSignalSelectionPanel(MessageSourceAccessor messageSource, boolean withChannelSelection) {
		super();
		this.messageSource = messageSource;
		this.withChannelSelection = withChannelSelection;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {

		setBorder(new EmptyBorder(3,3,3,3));

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel channelLabel = new JLabel(messageSource.getMessage("signalSelection.channel"));
		JLabel startTimeLabel = new JLabel(messageSource.getMessage("signalSelection.startTime"));
		JLabel lengthLabel = new JLabel(messageSource.getMessage("signalSelection.lengthSeconds"));

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

	public JSpinner getStartTimeSpinner() {
		if (startTimeSpinner == null) {
			/* model is set by the dialog */
			startTimeSpinner = new RangeToolTipSpinner();
			startTimeSpinner.setPreferredSize(new Dimension(150,25));
			startTimeSpinner.setFont(startTimeSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return startTimeSpinner;
	}

	public JSpinner getLengthSpinner() {
		if (lengthSpinner == null) {
			/* model is set by the dialog */
			lengthSpinner = new RangeToolTipSpinner();
			lengthSpinner.setPreferredSize(new Dimension(150,25));
			lengthSpinner.setFont(lengthSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return lengthSpinner;
	}

	public JComboBox getChannelComboBox() {
		if (channelComboBox == null) {
			/* model is set by the dialog */
			channelComboBox = new JComboBox();
			channelComboBox.setPreferredSize(new Dimension(150,25));
		}
		return channelComboBox;
	}

}
