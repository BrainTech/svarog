/* PageSignalSelectionPanel.java created 2007-10-04
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

/** PageSignalSelectionPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PageSignalSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JSpinner startPageSpinner;
	private JSpinner lengthSpinner;

	/**
	 * This is the default constructor
	 */
	public PageSignalSelectionPanel(MessageSourceAccessor messageSource) {
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
		JLabel lengthLabel = new JLabel(messageSource.getMessage("signalSelection.lengthPages"));

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

	public JSpinner getStartPageSpinner() {
		if (startPageSpinner == null) {
			/* model is set by the dialog */
			startPageSpinner = new RangeToolTipSpinner();
			startPageSpinner.setPreferredSize(new Dimension(150,25));
			startPageSpinner.setFont(startPageSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return startPageSpinner;
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
