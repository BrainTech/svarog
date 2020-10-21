package org.signalml.app.method.mp5;

import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.method.mp5.AtomsInDictionary;
import org.signalml.method.mp5.MP5AtomType;
import org.signalml.method.mp5.MP5Parameters;

/**
 *
 *
 * @author Piotr Szachewicz
 */
public class MP5AtomsInDictionaryPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JCheckBox[] atomTypeCheckBoxes;

	public MP5AtomsInDictionaryPanel() {
		super();
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Atoms to be included in the dictionary")),
			new EmptyBorder(3,3,3,3)
		);

		setBorder(border);

		int numberOfAtomTypes = MP5AtomType.values().length;
		int numberOfRows = (int) Math.ceil(((double) numberOfAtomTypes) / 2);

		setLayout(new GridLayout(numberOfRows, 2, 10, 10));

		atomTypeCheckBoxes = new JCheckBox[numberOfAtomTypes];
		for (int i = 0; i < numberOfAtomTypes; i++) {
			MP5AtomType type = MP5AtomType.values()[i];
			atomTypeCheckBoxes[i] = new JCheckBox(type.getName());
			atomTypeCheckBoxes[i].setSelected(type == MP5AtomType.GAUSS || type == MP5AtomType.GABOR);
			atomTypeCheckBoxes[i].setEnabled(false);
			add(atomTypeCheckBoxes[i]);
		}

	}

	public void fillPanelFromParameters(MP5Parameters parameters) {
		// the current (0.5.0) version of empi does not allow for selecting dictionary atoms
//		AtomsInDictionary dictionary = parameters.getAtomsInDictionary();
//		for (int i = 0; i < atomTypeCheckBoxes.length; i++) {
//			MP5AtomType atomType = MP5AtomType.values()[i];
//			atomTypeCheckBoxes[i].setSelected(dictionary.isAtomIncluded(atomType));
//		}
	}

	public void fillParametersFromPanel(MP5Parameters parameters) {
		AtomsInDictionary dictionary = parameters.getAtomsInDictionary();
		for (int i = 0; i < atomTypeCheckBoxes.length; i++) {
			MP5AtomType atomType = MP5AtomType.values()[i];
			Boolean isSelected = atomTypeCheckBoxes[i].isSelected();
			dictionary.setAtomIncluded(atomType, isSelected);
		}
	}

	public boolean isAtLeastOneAtomTypeSelected() {
		for (JCheckBox atomTypeCheckBox : atomTypeCheckBoxes) {
			if (atomTypeCheckBox.isSelected()) {
				return true;
			}
		}
		return false;
	}

	public void validatePanel(ValidationErrors errors) {
		if (!isAtLeastOneAtomTypeSelected()) {
			errors.addError(_("At least one atom type should be selected!"));
		}
	}

}
