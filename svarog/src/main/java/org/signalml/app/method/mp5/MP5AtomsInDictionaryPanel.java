package org.signalml.app.method.mp5;

import static org.signalml.app.util.i18n.SvarogI18n._;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.method.mp5.AtomsInDictionary;
import org.signalml.method.mp5.MP5AtomType;
import org.signalml.method.mp5.MP5Parameters;

import org.springframework.validation.Errors;

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
			add(atomTypeCheckBoxes[i]);
		}

	}

	public void fillPanelFromParameters(MP5Parameters parameters) {
		AtomsInDictionary dictionary = parameters.getAtomsInDictionary();
		for (int i = 0; i < atomTypeCheckBoxes.length; i++) {
			MP5AtomType atomType = MP5AtomType.values()[i];
			atomTypeCheckBoxes[i].setSelected(dictionary.isAtomIncluded(atomType));
		}
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
		for (int i = 0; i < atomTypeCheckBoxes.length; i++)
			if (atomTypeCheckBoxes[i].isSelected())
				return true;
		return false;
	}

	public void validatePanel(Errors errors) {
		if (!isAtLeastOneAtomTypeSelected()) {
			errors.rejectValue("atomsInDictionary", null, _("At least one atom type should be selected!"));
		}
	}

}
