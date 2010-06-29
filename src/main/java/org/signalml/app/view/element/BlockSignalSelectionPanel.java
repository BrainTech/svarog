/* BlockSignalSelectionPanel.java created 2007-10-04
 *
 */
package org.signalml.app.view.element;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;

import org.springframework.context.support.MessageSourceAccessor;

/** BlockSignalSelectionPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BlockSignalSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JSpinner startPageSpinner;
	private JSpinner startBlockSpinner;
	private JSpinner lengthSpinner;

	/**
	 * This is the default constructor
	 */
	public BlockSignalSelectionPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
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

		JLabel startPageLabel = new JLabel(messageSource.getMessage("signalSelection.startPage"));
		JLabel startBlockLabel = new JLabel(messageSource.getMessage("signalSelection.startBlock"));
		JLabel lengthLabel = new JLabel(messageSource.getMessage("signalSelection.lengthBlocks"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(startPageLabel)
		        .addComponent(startBlockLabel)
		        .addComponent(lengthLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getStartPageSpinner())
		        .addComponent(getStartBlockSpinner())
		        .addComponent(getLengthSpinner())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
		        layout.createParallelGroup(Alignment.BASELINE)
		        .addComponent(startPageLabel)
		        .addComponent(getStartPageSpinner())
		);

		vGroup.addGroup(
		        layout.createParallelGroup(Alignment.BASELINE)
		        .addComponent(startBlockLabel)
		        .addComponent(getStartBlockSpinner())
		);

		vGroup.addGroup(
		        layout.createParallelGroup(Alignment.BASELINE)
		        .addComponent(lengthLabel)
		        .addComponent(getLengthSpinner())
		);

		layout.setVerticalGroup(vGroup);

	}

	public JSpinner getStartPageSpinner() {
		if (startPageSpinner == null) {
			/* model is set by the dialog */
			startPageSpinner = new RangeToolTipSpinner();
			startPageSpinner.setEditor(new JSpinner.NumberEditor(startPageSpinner));
			startPageSpinner.setPreferredSize(new Dimension(150,25));
			startPageSpinner.setFont(startPageSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return startPageSpinner;
	}

	public JSpinner getStartBlockSpinner() {
		if (startBlockSpinner == null) {
			/* model is set by the dialog */
			startBlockSpinner = new RangeToolTipSpinner();
			startBlockSpinner.setPreferredSize(new Dimension(150,25));
			startBlockSpinner.setFont(startBlockSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return startBlockSpinner;
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

}
