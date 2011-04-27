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

/**
 * Panel which allows to select the parameters of the block selection:
 * <ul>
 * <li>the {@link #getStartPageSpinner() number} of the page in which the
 * first block of the selection is located,</li>
 * <li>the {@link #getStartBlockSpinner() number} (within the page) of the first
 * block in the selection,</li>
 * <li>the {@link #getLengthSpinner() number} of consecutive blocks that should
 * be included in the selection.</li></ul>
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BlockSignalSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the source of messages (labels)
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * the spinner with the number of the page in which the first block of the
	 * selection is located
	 */
	private JSpinner startPageSpinner;
	/**
	 * the spinner with the number (within the page) of the first block of the
	 * selection
	 */
	private JSpinner startBlockSpinner;
	/**
	 * the spinner with the number of consecutive blocks that should be included
	 * in the selection
	 */
	private JSpinner lengthSpinner;

	/**
	 * Constructor. Sets the source of messages and initializes this panel.
	 * @param messageSource the source of messages
	 */
	public BlockSignalSelectionPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * Initializes this panel with GroupLayout and two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for labels and one
	 * for spinners. This group positions the elements in two columns.</li>
	 * <li>vertical group which has 3 sub-groups - one for every row:
	 * <ul>
	 * <li>label and {@link #getStartPageSpinner() spinner} which contains the
	 * number of the page in which the first block of the selection is
	 * located,</li>
	 * <li>label and {@link #getStartBlockSpinner() spinner} which contains the
	 * number (within the page of the first block of the selection),</li>
	 * <li>label and {@link #getLengthSpinner() spinner} which contains the
	 * number of consecutive blocks that should be included in the selection.
	 * </li></ul>
	 * This group positions elements in rows.</li>
	 * </ul>
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

	/**
	 * Returns the spinner with the number of the page in which the first block
	 * of the selection is located.
	 * If the spinner doesn't exist it is created.
	 * <p>NOTE: the parameters of the spinner must be filled outside this
	 * panel.
	 * @return the spinner with the number of the page in which the first block
	 * of the selection is located
	 */
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

	/**
	 * Returns the spinner with the number (within the page) of the first block
	 * of the selection.
	 * If the spinner doesn't exist it is created.
	 * <p>NOTE: the parameters of the spinner must be filled outside this
	 * panel.
	 * @return the spinner with the number of the first block of the selection
	 */
	public JSpinner getStartBlockSpinner() {
		if (startBlockSpinner == null) {
			/* model is set by the dialog */
			startBlockSpinner = new RangeToolTipSpinner();
			startBlockSpinner.setPreferredSize(new Dimension(150,25));
			startBlockSpinner.setFont(startBlockSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return startBlockSpinner;
	}

	/**
	 * Returns the spinner with the number of consecutive blocks that should
	 * be included in the selection.
	 * If the spinner doesn't exist it is created.
	 * <p>NOTE: the parameters of the spinner must be filled outside this
	 * panel.
	 * @return the spinner with the number of consecutive blocks that should
	 * be included in the selection
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

}
