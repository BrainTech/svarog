/* PageSignalSelectionPanel.java created 2007-10-04
 *
 */
package org.signalml.app.view.signal.signalselection;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.spinners.RangeToolTipSpinner;


/**
 * Panel which allows to select the parameters of the page selection:
 * <ul>
 * <li>the {@link #getStartPageSpinner() number} of the first page in the
 * selection,</li>
 * <li>the {@link #getLengthSpinner() number} of consecutive pages that should
 * be included in the selection.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PageSignalSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the spinner with the number of the first page that should be included
	 * in the selection
	 */
	private JSpinner startPageSpinner;
	/**
	 * the spinner with the number of consecutive pages that should be included
	 * in the selection
	 */
	private JSpinner lengthSpinner;

	/**
	 * Constructor. Initializes the panel.
	 */
	public PageSignalSelectionPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with GroupLayout and two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for labels and one
	 * for spinners. This group positions the elements in two columns.</li>
	 * <li>vertical group which has 2 sub-groups - one for every row:
	 * <ul>
	 * <li>label and {@link #getStartPageSpinner() spinner} which contains the
	 * number of the first page that should be included in the selection,</li>
	 * <li>label and {@link #getLengthSpinner() spinner} which contains the
	 * number of consecutive pages that should be included in the selection.
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

		JLabel startPageLabel = new JLabel(_("Start page"));
		JLabel lengthLabel = new JLabel(_("Length (pages)"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(startPageLabel)
			.addComponent(lengthLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getStartPageSpinner())
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
			.addComponent(lengthLabel)
			.addComponent(getLengthSpinner())
		);

		layout.setVerticalGroup(vGroup);
	}

	/**
	 * Returns the spinner with the number of the first page that should be
	 * included in the selection.
	 * If the spinner doesn't exist it is created.
	 * <p>NOTE: the spinner must be filled outside this panel
	 * @return the spinner with the number of the first page that should be
	 * included in the selection
	 */
	public JSpinner getStartPageSpinner() {
		if (startPageSpinner == null) {
			/* model is set by the dialog */
			startPageSpinner = new RangeToolTipSpinner();
			startPageSpinner.setPreferredSize(new Dimension(150,25));
			startPageSpinner.setFont(startPageSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return startPageSpinner;
	}

	/**
	 * Returns the spinner with the number of consecutive pages that should be
	 * included in the selection.
	 * If the spinner doesn't exist it is created.
	 * <p>NOTE: the spinner must be filled outside this panel
	 * @return the spinner with the number of consecutive pages that should be
	 * included in the selection
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
