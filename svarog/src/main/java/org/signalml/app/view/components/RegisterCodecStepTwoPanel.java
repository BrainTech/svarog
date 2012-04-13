/* RegisterCodecStepTwoPanel.java created 2007-09-18
 *
 */
package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.dialogs.RegisterCodecDialog;
import org.signalml.codec.SignalMLCodec;

/**
 * Panel for the second step of {@link RegisterCodecDialog}.
 * Allows to {@link #getFormatNamePanel() select} the format name for the
 * opened {@link SignalMLCodec codec} and {@link #getWarningLabel() informs}
 * user if such name already exists.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RegisterCodecStepTwoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the panel with the desired {@link #getNameField() format name} of the
	 * {@link SignalMLCodec codec} and the {@link #getWarningLabel() warning
	 * label}
	 */
	private JPanel formatNamePanel = null;
	/**
	 * the text field with the desired format name
	 */
	private JTextField nameField = null;
	/**
	 * the label which warns that the {@link SignalMLCodec codec} of this name
	 * already exists
	 */
	private JLabel warningLabel = null;

	/**
	 * Constructor. Initializes the panel.
	 */
	public RegisterCodecStepTwoPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with the {@link BorderLayout} and
	 * adds to it the {@link #getFormatNamePanel() panel} with the
	 * desired {@link #getNameField() format name} of the {@link SignalMLCodec
	 * codec} and the {@link #getWarningLabel() warning label}.
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		add(getFormatNamePanel(), BorderLayout.CENTER);

	}

	/**
	 * Returns the panel with the desired {@link #getNameField() format name}
	 * of the {@link SignalMLCodec codec} and the {@link #getWarningLabel()
	 * warning label}.
	 * If the panel doesn't exist, it is created.
	 * The panel contains two elements (from top to bottom):
	 * <ul><li>the panel with the {@link #getNameField() field} with the
	 * desired format name,</li>
	 * <li>the {@link #getWarningLabel() warning label}.</li>
	 * </ul>
	 * @return the panel with the desired format name and the warning label
	 */
	private JPanel getFormatNamePanel() {
		if (formatNamePanel == null) {
			formatNamePanel = new JPanel();
			formatNamePanel.setBorder(BorderFactory.createTitledBorder(_("Choose the name of the format")));
			formatNamePanel.setLayout(new BorderLayout());
			TitledComponentPanel namePanel = new TitledComponentPanel(_("Format name"),getNameField());
			namePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			formatNamePanel.add(namePanel,BorderLayout.NORTH);
			formatNamePanel.add(getWarningLabel(),BorderLayout.CENTER);
		}

		return formatNamePanel;
	}

	/**
	 * Returns the text field with the desired format name of the
	 * {@link SignalMLCodec codec}.
	 * If the field doens't exist it is created.
	 * @return the text field with the desired format name of the codec
	 */
	public JTextField getNameField() {
		if (nameField == null) {
			nameField = new JTextField();
			nameField.setPreferredSize(new Dimension(200,25));
		}
		return nameField;
	}

	/**
	 * Returns the label which warns that the {@link SignalMLCodec codec} of
	 * the name selected in the {@link #getNameField() name field} already
	 * exists.
	 * If the label doesn't exist it is created and the icon and tex for it
	 * is set.
	 * @return the label which warns that the codec of the name selected in the
	 * name field already exists
	 */
	public JLabel getWarningLabel() {
		if (warningLabel == null) {
			warningLabel = new JLabel(_("Name exists. Current codec will be replaced!"));
			warningLabel.setIcon(IconUtils.getWarningIcon());
			warningLabel.setHorizontalAlignment(JLabel.CENTER);
			warningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return warningLabel;
	}

}
