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

import org.signalml.app.action.RegisterCodecAction;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.dialog.RegisterCodecDialog;
import org.signalml.codec.SignalMLCodec;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Panel which allows to select the {@link SignalMLCodec codec} needed to open
 * a {@link SignalDocument}.
 * Contains:
 * <ul>
 * <li>the {@link #getSignalMLDriverComboBox() combo-box} which allows to
 * select the codec,</li>
 * <li>the {@link #getRegisterCodecButton() button} which activates the
 * {@link RegisterCodecAction registration} of a new codec.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the combo-box which allows to select the {@link SignalMLCodec codec}
	 */
	private JComboBox signalMLDriverComboBox;
	/**
	 * the button that activates the {@link RegisterCodecDialog registration}
	 * of the {@link SignalMLCodec codec}
	 */
	private JButton registerCodecButton;

	/**
	 * the {@link MessageSourceAccessor source} of messages (labels)
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * Constructor. Sets the {@link MessageSourceAccessor message source} and
	 * initializes this panel.
	 * @param messageSource the source of messages (labels)
	 */
	public SignalMLOptionsPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * Initializes this panel with a group layout.
	 * This panel contains two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for a label to the
	 * {@link #signalMLDriverComboBox} and one with the combo-box and the
	 * {@link #getRegisterCodecButton() button}.
	 * This group positions the elements in two columns.</li>
	 * <li>vertical group which has 2 sub-groups - one for every row:
	 * <ul>
	 * <li>the label and the combo-box which allows to select the
	 * {@link SignalMLCodec codec},</li>
	 * <li>the button that activates the {@link RegisterCodecDialog registration}
	 * of the codec.</li>
	 * </ul>
	 * This group positions elements in rows.</li>
	 * </ul>
	 */
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

	/**
	 * Returns the combo-box which allows to select the {@link SignalMLCodec codec}
	 * If it doesn't exist it is created.<p>
	 * NOTE: model must be filled in by parent
	 * @return the combo-box which allows to select the codec.
	 */
	public JComboBox getSignalMLDriverComboBox() {
		if (signalMLDriverComboBox == null) {
			// model must be filled in by parent
			signalMLDriverComboBox = new JComboBox(new Object[0]);
		}
		return signalMLDriverComboBox;
	}

	/**
	 * Returns the button that activates the {@link RegisterCodecDialog registration}
	 * of the {@link SignalMLCodec codec}.
	 * It if doesn't exist it is created.<br>
	 * NOTE: action must be filled in by parent.
	 * @return the button that activates the registration of the codec
	 */
	public JButton getRegisterCodecButton() {
		if (registerCodecButton == null) {
			// action must be filled in by parent
			registerCodecButton = new JButton((Action) null);
		}
		return registerCodecButton;
	}

}
