/* WholeTimeSpacePanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.element;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.springframework.context.support.MessageSourceAccessor;

/** WholeTimeSpacePanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class WholeTimeSpacePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(WholeTimeSpacePanel.class);

	private MessageSourceAccessor messageSource;

	private JTextField signalLengthTextField;
	private JTextField pageSizeTextField;
	private JTextField pageCountTextField;

	private JCheckBox completePagesCheckBox;

	public WholeTimeSpacePanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setBorder(new CompoundBorder(
		                  new TitledBorder(messageSource.getMessage("signalSpace.wholeTimeSpace.frameTitle")),
		                  new EmptyBorder(3,3,3,3)
		          ));

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel signalLengthLabel = new JLabel(messageSource.getMessage("signalSpace.wholeTimeSpace.signalLength"));
		JLabel pageSizeLabel = new JLabel(messageSource.getMessage("signalSpace.wholeTimeSpace.pageSize"));
		JLabel pageCountLabel = new JLabel(messageSource.getMessage("signalSpace.wholeTimeSpace.pageCount"));
		JLabel completePagesLabel = new JLabel(messageSource.getMessage("signalSpace.wholeTimeSpace.completePages"));

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

	public JTextField getSignalLengthTextField() {
		if (signalLengthTextField == null) {
			signalLengthTextField = new JTextField();
			signalLengthTextField.setEditable(false);
			signalLengthTextField.setPreferredSize(new Dimension(150,25));
			signalLengthTextField.setHorizontalAlignment(JTextField.RIGHT);
		}
		return signalLengthTextField;
	}

	public JTextField getPageSizeTextField() {
		if (pageSizeTextField == null) {
			pageSizeTextField = new JTextField();
			pageSizeTextField.setEditable(false);
			pageSizeTextField.setPreferredSize(new Dimension(150,25));
			pageSizeTextField.setHorizontalAlignment(JTextField.RIGHT);
		}
		return pageSizeTextField;
	}

	public JTextField getPageCountTextField() {
		if (pageCountTextField == null) {
			pageCountTextField = new JTextField();
			pageCountTextField.setEditable(false);
			pageCountTextField.setPreferredSize(new Dimension(150,25));
			pageCountTextField.setHorizontalAlignment(JTextField.RIGHT);
		}
		return pageCountTextField;
	}

	public JCheckBox getCompletePagesCheckBox() {
		if (completePagesCheckBox == null) {
			completePagesCheckBox = new JCheckBox();
		}
		return completePagesCheckBox;
	}

	public void fillPanelFromModel(SignalSpace space) {

		JCheckBox completeCheckBox = getCompletePagesCheckBox();
		if (completeCheckBox.isEnabled()) {
			completeCheckBox.setSelected(space.isWholeSignalCompletePagesOnly());
		}

	}

	public void fillModelFromPanel(SignalSpace space) {

		JCheckBox completeCheckBox = getCompletePagesCheckBox();
		if (completeCheckBox.isEnabled()) {
			space.setWholeSignalCompletePagesOnly(completeCheckBox.isSelected());
		} else {
			space.setWholeSignalCompletePagesOnly(true);
		}

	}

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
