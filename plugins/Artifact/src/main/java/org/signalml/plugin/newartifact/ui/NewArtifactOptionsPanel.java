/* ArtifactOptionsPanel.java created 2007-11-02
 *
 */
package org.signalml.plugin.newartifact.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import static org.signalml.plugin.newartifact.NewArtifactPlugin._;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.plugin.newartifact.data.NewArtifactPowerGridFrequency;

/** ArtifactOptionsPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 			(dialog design based on work by Hubert Klekowicz)
 */
public class NewArtifactOptionsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private ResolvableComboBox powerComboBox;
	private JCheckBox exclusionCheckBox;
	private JButton exclusionButton;

	public NewArtifactOptionsPanel() {
		super();
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Options")),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel powerLabel = new JLabel(_("Power frequency"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(powerLabel)
			.addComponent(getExclusionCheckBox())
		);

		hGroup.addGroup(
			layout.createParallelGroup(Alignment.TRAILING)
			.addComponent(getPowerComboBox())
			.addComponent(getExclusionButton())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(powerLabel)
			.addComponent(getPowerComboBox())
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(getExclusionCheckBox())
			.addComponent(getExclusionButton())
		);

		layout.setVerticalGroup(vGroup);

	}

	public ResolvableComboBox getPowerComboBox() {
		if (powerComboBox == null) {
			powerComboBox = new ResolvableComboBox();
			DefaultComboBoxModel model = new DefaultComboBoxModel(NewArtifactPowerGridFrequency.values());
			powerComboBox.setModel(model);
		}
		return powerComboBox;
	}

	public JCheckBox getExclusionCheckBox() {
		if (exclusionCheckBox == null) {
			exclusionCheckBox = new JCheckBox(_("Exclude some derivations"));
			exclusionCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
					getExclusionButton().setEnabled(selected);

				}

			});
		}
		return exclusionCheckBox;
	}

	public JButton getExclusionButton() {
		if (exclusionButton == null) {
			exclusionButton = new JButton();
		}
		return exclusionButton;
	}

}