/* ArtifactTypesPanel.java created 2007-11-02
 *
 */
package org.signalml.plugin.newartifact.ui;

import static org.signalml.plugin.newartifact.NewArtifactPlugin._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.plugin.newartifact.data.NewArtifactParameters;
import org.signalml.plugin.newartifact.data.NewArtifactType;

/** ArtifactTypesPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 			(dialog design based on work by Hubert Klekowicz)
 */
public class NewArtifactTypesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int SCROLLBAR_SCALE = 10;

	private NewArtifactType[] artifactTypes = NewArtifactType.values();

	private JTextField[] sensitivityTextFields;
	private JScrollBar[] sensitivityScrollBars;
	private JCheckBox[] artifactTypeCheckBoxes;

	public NewArtifactTypesPanel() {
		super();
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Artifact types & sensitivity")),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		JPanel typesPanel = new JPanel();
		typesPanel.setBorder(new EmptyBorder(5,0,0,0));

		GroupLayout layout = new GroupLayout(typesPanel);
		typesPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		ParallelGroup checkBoxParallelGroup = layout.createParallelGroup();
		ParallelGroup textFieldParallelGroup = layout.createParallelGroup();
		ParallelGroup scrollBarParallelGroup = layout.createParallelGroup(Alignment.TRAILING);

		JCheckBox[] checkBoxes = getArtifactTypeCheckBoxes();
		JScrollBar[] scrollBars = getSensitivityScrollBars();
		JTextField[] textFields = getSensitivityTextFields();

		for (int i=0; i<artifactTypes.length; i++) {

			vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(checkBoxes[i])
				.addComponent(textFields[i])
				.addComponent(scrollBars[i])
			);

			checkBoxParallelGroup.addComponent(checkBoxes[i]);
			textFieldParallelGroup.addComponent(textFields[i]);
			scrollBarParallelGroup.addComponent(scrollBars[i]);

			checkBoxes[i].addItemListener(new CheckBoxListener(i));
			textFields[i].addFocusListener(new TextFieldListener(i));
			scrollBars[i].addAdjustmentListener(new ScrollBarListener(i));

		}

		hGroup.addGroup(checkBoxParallelGroup);
		hGroup.addGroup(textFieldParallelGroup);
		hGroup.addGroup(scrollBarParallelGroup);

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);

		add(typesPanel, BorderLayout.CENTER);

	}

	public void fillPanelFromParameters(NewArtifactParameters parameters) {

		int[] chosenArtifactTypes = parameters.getChosenArtifactTypes();
		float[] sensitivities = parameters.getSensitivities();

		JCheckBox[] checkBoxes = getArtifactTypeCheckBoxes();
		JScrollBar[] scrollBars = getSensitivityScrollBars();
		JTextField[] textFields = getSensitivityTextFields();

		for (int i=0; i<artifactTypes.length; i++) {

			textFields[i].setText(Float.toString(sensitivities[i]));
			scrollBars[i].setValue((int) Math.round(sensitivities[i]*SCROLLBAR_SCALE));
			checkBoxes[i].setSelected(chosenArtifactTypes[i] != 0);

		}

	}

	// TODO no validation ?
	// not needed? The form enforces correct values?

	public void fillParametersFromPanel(NewArtifactParameters parameters) {

		int[] chosenArtifactTypes = parameters.getChosenArtifactTypes();
		float[] sensitivities = parameters.getSensitivities();

		JCheckBox[] checkBoxes = getArtifactTypeCheckBoxes();
		JTextField[] textFields = getSensitivityTextFields();

		for (int i=0; i<artifactTypes.length; i++) {

			chosenArtifactTypes[i] = (checkBoxes[i].isSelected() ? 1 : 0);
			sensitivities[i] = Float.parseFloat(textFields[i].getText());

		}

	}

	public void setLockOnType(NewArtifactType type, boolean locked) {

		JCheckBox checkBox = getArtifactTypeCheckBoxes()[ type.ordinal()];
		if (locked) {
			checkBox.setSelected(false);
			checkBox.setEnabled(false);
		} else {
			checkBox.setEnabled(true);
		}

	}

	public JTextField[] getSensitivityTextFields() {
		if (sensitivityTextFields == null) {
			sensitivityTextFields = new JTextField[artifactTypes.length];
			Dimension size = new Dimension(60,25);
			for (int i=0; i<sensitivityTextFields.length; i++) {
				sensitivityTextFields[i] = new JTextField();
				sensitivityTextFields[i].setPreferredSize(size);
				sensitivityTextFields[i].setHorizontalAlignment(SwingConstants.CENTER);
				sensitivityTextFields[i].setToolTipText("0 - 100");
				sensitivityTextFields[i].setEnabled(false);
			}
		}
		return sensitivityTextFields;
	}

	public JScrollBar[] getSensitivityScrollBars() {
		if (sensitivityScrollBars == null) {
			sensitivityScrollBars = new JScrollBar[artifactTypes.length];
			Dimension size = null;
			for (int i=0; i<sensitivityScrollBars.length; i++) {
				sensitivityScrollBars[i] = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0*SCROLLBAR_SCALE, 100*SCROLLBAR_SCALE);
				if (i == 0) {
					size = sensitivityScrollBars[i].getPreferredSize();
					size.width = 200;
				}
				sensitivityScrollBars[i].setPreferredSize(size);
				sensitivityScrollBars[i].setBlockIncrement(SCROLLBAR_SCALE);
				sensitivityScrollBars[i].setUnitIncrement(1);
				sensitivityScrollBars[i].setEnabled(false);
			}
		}
		return sensitivityScrollBars;
	}

	public JCheckBox[] getArtifactTypeCheckBoxes() {
		if (artifactTypeCheckBoxes == null) {
			artifactTypeCheckBoxes = new JCheckBox[artifactTypes.length];
			for (int i=0; i<artifactTypeCheckBoxes.length; i++) {
				artifactTypeCheckBoxes[i] = new JCheckBox(NewArtifactTypeCaptionHelper.GetCaption(artifactTypes[i]));
			}
		}
		return artifactTypeCheckBoxes;
	}

	private class CheckBoxListener implements ItemListener {

		private int index;

		private CheckBoxListener(int index) {
			this.index = index;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {

			boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
			getSensitivityScrollBars()[index].setEnabled(selected);
			getSensitivityTextFields()[index].setEnabled(selected);

		}

	}

	private class TextFieldListener extends FocusAdapter {

		private int index;

		private TextFieldListener(int index) {
			this.index = index;
		}

		@Override
		public void focusLost(FocusEvent e) {

			JTextField textField = getSensitivityTextFields()[index];
			String text = textField.getText();
			float value = -1;
			try {
				value = Float.parseFloat(text);
			} catch (NumberFormatException ex) {
				// do nothing, will cause value to remain at -1, which is detected later on
			}
			if (value > 100) {
				value = -1;
			}
			if (value < 0) {
				textField.setBackground(Color.RED.brighter());
				return;
			}
			textField.setBackground(Color.WHITE);
			getSensitivityScrollBars()[index].setValue((int) Math.round(value*SCROLLBAR_SCALE));

		}

	}

	private class ScrollBarListener implements AdjustmentListener {

		private int index;

		private ScrollBarListener(int index) {
			this.index = index;
		}

		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {

			float value = ((float) e.getValue()) / SCROLLBAR_SCALE ;

			JTextField textField = getSensitivityTextFields()[index];
			textField.setText(Float.toString(value));
			textField.setBackground(Color.WHITE);

		}

	}

}