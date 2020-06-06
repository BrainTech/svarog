/* WholeTimeSpacePanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.signal.signalselection;

import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;

/**
 * Panel which indicates the whole signal will be used.
 * In this panel the parameters of the signal are displayed:
 * <ul>
 * <li>the {@link #getSignalLengthTextField() length} of the signal,</li>
 * <li>the {@link #getPageSizeTextField() size} of the page of the signal,</li>
 * <li>the {@link #getPageCountTextField() number} of whole pages in the
 * signal.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class WholeTimeSpacePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(WholeTimeSpacePanel.class);

	/**
	 * the text field in which the length of the signal (in seconds) is
	 * displayed
	 */
	private JTextField signalLengthTextField;
	/**
	 * the text field in which the size (in seconds) of the page of the signal
	 * is displayed
	 */
	private JTextField pageSizeTextField;
	/**
	 * the text field in which the number of whole pages in the signal is
	 * displayed
	 */
	private JTextField pageCountTextField;

	/**
	 * the check-box which tells that only whole pages should be used
	 */
	private JCheckBox completePagesCheckBox;

	/**
	 * Constructor. Initializes the panel.
	 */
	public WholeTimeSpacePanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with the group layout and two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for labels and one
	 * for text fields. This group positions the elements in two columns.</li>
	 * <li>vertical group which has 4 sub-groups - one for every row:
	 * <ul>
	 * <li>label and {@link #getSignalLengthTextField() text field} in which
	 * the length of the signal (in seconds) is displayed,</li>
	 * <li>label and {@link #getPageSizeTextField() text field} in which
	 * the size (in seconds) of the page of the signal is displayed,</li>
	 * <li>label and {@link #getPageCountTextField() text field} in which
	 * the number of whole pages in the signal is displayed,</li>
	 * <li>label and {@link #getSignalLengthTextField() check-box} which tells
	 * that only whole pages should be used.</li>
	 * </ul>
	 * This group positions elements in rows.</li>
	 * </ul>
	 */
	private void initialize() {

		setBorder(new CompoundBorder(
					  new TitledBorder(_("Signal parameters")),
					  new EmptyBorder(3,3,3,3)
				  ));

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel signalLengthLabel = new JLabel(_("Signal length [s]"));
		JLabel pageSizeLabel = new JLabel(_("Page size [s]"));
		JLabel pageCountLabel = new JLabel(_("Number of whole pages"));
		JLabel completePagesLabel = new JLabel(_("Complete pages only"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(signalLengthLabel)
			.addComponent(pageSizeLabel)
			.addComponent(pageCountLabel)
			.addComponent(completePagesLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup(Alignment.TRAILING)
			.addComponent(getSignalLengthTextField())
			.addComponent(getPageSizeTextField())
			.addComponent(getPageCountTextField())
			.addComponent(getCompletePagesCheckBox())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(signalLengthLabel)
			.addComponent(getSignalLengthTextField())
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(pageSizeLabel)
			.addComponent(getPageSizeTextField())
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(pageCountLabel)
			.addComponent(getPageCountTextField())
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(completePagesLabel)
			.addComponent(getCompletePagesCheckBox())
		);

		layout.setVerticalGroup(vGroup);

	}

	/**
	 * Returns the text field in which the length of the signal (in seconds) is
	 * displayed.
	 * If the text field doesn't exist it is created.
	 * @return the text field in which the length of the signal is displayed
	 */
	public JTextField getSignalLengthTextField() {
		if (signalLengthTextField == null) {
			signalLengthTextField = new JTextField();
			signalLengthTextField.setEditable(false);
			signalLengthTextField.setPreferredSize(new Dimension(150,25));
			signalLengthTextField.setHorizontalAlignment(JTextField.RIGHT);
		}
		return signalLengthTextField;
	}

	/**
	 * Returns the text field in which the size (in seconds) of the page of the
	 * signal is displayed.
	 * If the text field doesn't exist it is created.
	 * @return the text field in which the size of the page of the signal
	 * is displayed
	 */
	public JTextField getPageSizeTextField() {
		if (pageSizeTextField == null) {
			pageSizeTextField = new JTextField();
			pageSizeTextField.setEditable(false);
			pageSizeTextField.setPreferredSize(new Dimension(150,25));
			pageSizeTextField.setHorizontalAlignment(JTextField.RIGHT);
		}
		return pageSizeTextField;
	}

	/**
	 * Returns the text field in which the number of whole pages in the signal
	 * is displayed.
	 * If the text field doesn't exist it is created.
	 * @return the text field in which the number of whole pages in the signal
	 * is displayed
	 */
	public JTextField getPageCountTextField() {
		if (pageCountTextField == null) {
			pageCountTextField = new JTextField();
			pageCountTextField.setEditable(false);
			pageCountTextField.setPreferredSize(new Dimension(150,25));
			pageCountTextField.setHorizontalAlignment(JTextField.RIGHT);
		}
		return pageCountTextField;
	}

	/**
	 * Returns the check-box which tells that only whole pages should be used.
	 * If the check-box doesn't exist it is created
	 * @return the check-box which tells that only whole pages should be used
	 */
	public JCheckBox getCompletePagesCheckBox() {
		if (completePagesCheckBox == null) {
			completePagesCheckBox = new JCheckBox();
		}
		return completePagesCheckBox;
	}

	/**
	 * Sets the {@link SignalSpace#isWholeSignalCompletePagesOnly()
	 * information} if only whole pages of signal should be used
	 * in the {@link #getCompletePagesCheckBox() check-box}.
	 * @param space the signal space
	 */
	public void fillPanelFromModel(SignalSpace space) {

		JCheckBox completeCheckBox = getCompletePagesCheckBox();
		if (completeCheckBox.isEnabled()) {
			completeCheckBox.setSelected(space.isWholeSignalCompletePagesOnly());
		}

	}

	/**
	 * Sets the {@link SignalSpace#setWholeSignalCompletePagesOnly(boolean)
	 * information} if only whole pages of signal should be used in the
	 * {@link SignalSpace model}
	 * basing on the {@link #getCompletePagesCheckBox() check-box}.
	 * @param space the signal space
	 */
	public void fillModelFromPanel(SignalSpace space) {

		JCheckBox completeCheckBox = getCompletePagesCheckBox();
		if (completeCheckBox.isEnabled()) {
			space.setWholeSignalCompletePagesOnly(completeCheckBox.isSelected());
		} else {
			space.setWholeSignalCompletePagesOnly(true);
		}

	}

	/**
	 * Sets the {@link SignalSpaceConstraints parameters} of the signal:
	 * <ul>
	 * <li>the length of the signal in seconds,</li>
	 * <li>the size of the page in seconds,</li>
	 * <li>the number of whole pages in the signal.</li></ul>
	 * @param constraints the parameters of the signal
	 */
	public void setConstraints(SignalSpaceConstraints constraints) {

		getSignalLengthTextField().setText(Float.toString(constraints.getTimeSignalLength()));
		getPageSizeTextField().setText(Float.toString(constraints.getPageSize()));
		getPageCountTextField().setText(Integer.toString(constraints.getMaxWholePage()+1));

		JCheckBox completeCheckBox = getCompletePagesCheckBox();
		if (constraints.isRequireCompletePages()) {
			completeCheckBox.setSelected(true);
			completeCheckBox.setEnabled(false);
		} else {
			completeCheckBox.setEnabled(true);
		}

	}

}
