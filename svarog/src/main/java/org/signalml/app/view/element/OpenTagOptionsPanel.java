/* OpenTagOptionsPanel.java created 2007-10-10
 *
 */

package org.signalml.app.view.element;

import static org.signalml.app.SvarogI18n._;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;

/**
 * Panel which allows to {@link #getSignalDocumentComboBox() select} which the
 * {@link SignalDocument signal document} to which the opened
 * {@link TagDocument tag document} should be attached.
 * 
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenTagOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the combo-box which allows to select the signal document to which
	 * the opened tag document should be attached
	 */
	private JComboBox signalDocumentComboBox;

	/**
	 * Constructor. Initializes the panel.
	 */
	public OpenTagOptionsPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with {@link GroupLayout} and two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for label and one
	 * for combo box. This group positions the elements in two columns.</li>
	 * <li>vertical group with only one sub-group:
	 * <ul>
	 * <li>label and {@link #getSignalDocumentComboBox() combo-box} which
	 * allows to select the signal document to which the opened tag document
	 * will be attached.</li>
	 * </ul>
	 * This group positions elements in the same row.</li>
	 * </ul>
	 */
	private void initialize() {

		CompoundBorder cb = new CompoundBorder(
		        new TitledBorder(_("Choose options for tag")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(cb);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel signalDocumentLabel = new JLabel(_("Select signal document"));

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

	/**
	 * Returns the combo-box which allows to select the signal document to
	 * which the opened tag document should be attached.
	 * If the combo-box doesn't exist it is created.
	 * <p>NOTE: the combo-box must be filed in by parent
	 * @return the combo-box which allows to select the signal document to
	 * which the opened tag document should be attached
	 */
	public JComboBox getSignalDocumentComboBox() {
		if (signalDocumentComboBox == null) {
			// model must be filled in by parent
			signalDocumentComboBox = new JComboBox(new Object[0]);
		}
		return signalDocumentComboBox;
	}


}
