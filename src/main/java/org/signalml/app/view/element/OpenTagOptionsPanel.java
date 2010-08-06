/* OpenTagOptionsPanel.java created 2007-10-10
 *
 */

package org.signalml.app.view.element;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.springframework.context.support.MessageSourceAccessor;

/** OpenTagOptionsPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenTagOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JComboBox signalDocumentComboBox;

	private MessageSourceAccessor messageSource;

	/**
	 * This is the default constructor
	 */
	public OpenTagOptionsPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 *
	 */
	private void initialize() {

		CompoundBorder cb = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("openTag.options.title")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(cb);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel signalDocumentLabel = new JLabel(messageSource.getMessage("openTag.options.signalDocument"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(signalDocumentLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getSignalDocumentComboBox())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
		        layout.createParallelGroup(Alignment.BASELINE)
		        .addComponent(signalDocumentLabel)
		        .addComponent(getSignalDocumentComboBox())
		);

		layout.setVerticalGroup(vGroup);

	}

	public JComboBox getSignalDocumentComboBox() {
		if (signalDocumentComboBox == null) {
			// model must be filled in by parent
			signalDocumentComboBox = new JComboBox(new Object[0]);
		}
		return signalDocumentComboBox;
	}


}
