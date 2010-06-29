/* SignalMLOptionsPanel.java created 2008-01-28
 *
 */

package org.signalml.app.view.element;

import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.springframework.context.support.MessageSourceAccessor;

/** SignalMLOptionsPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JComboBox signalMLDriverComboBox;
	private JButton registerCodecButton;

	private MessageSourceAccessor messageSource;

	public SignalMLOptionsPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		CompoundBorder cb = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("openSignal.options.signalml.title")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(cb);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel driverLabel = new JLabel(messageSource.getMessage("openSignal.options.signalml.driver"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(driverLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(getSignalMLDriverComboBox()).addComponent(getRegisterCodecButton()));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(driverLabel).addComponent(getSignalMLDriverComboBox()));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(getRegisterCodecButton()));

		layout.setVerticalGroup(vGroup);

	}

	public JComboBox getSignalMLDriverComboBox() {
		if (signalMLDriverComboBox == null) {
			// model must be filled in by parent
			signalMLDriverComboBox = new JComboBox(new Object[0]);
		}
		return signalMLDriverComboBox;
	}

	public JButton getRegisterCodecButton() {
		if (registerCodecButton == null) {
			// action must be filled in by parent
			registerCodecButton = new JButton((Action) null);
		}
		return registerCodecButton;
	}

}
