/* InputSignalPanel.java created 2007-11-02
 *
 */
package org.signalml.app.method;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


/** InputSignalPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class InputSignalPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField signalTextField;
	private JButton montageButton;

	public InputSignalPanel() {
		super();
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout(3,3));

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(_("Input data")),
		        new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		add(getSignalTextField(), BorderLayout.CENTER);
		add(getMontageButton(), BorderLayout.EAST);

	}

	public JTextField getSignalTextField() {
		if (signalTextField == null) {
			signalTextField = new JTextField();
			signalTextField.setPreferredSize(new Dimension(200,25));
			signalTextField.setEditable(false);
		}
		return signalTextField;
	}

	public JButton getMontageButton() {
		if (montageButton == null) {
			montageButton = new JButton();
		}
		return montageButton;
	}

}
