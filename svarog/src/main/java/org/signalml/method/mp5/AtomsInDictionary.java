package org.signalml.method.mp5;

import java.io.Serializable;
import java.util.EnumMap;

/**
 *
 * @author Piotr Szachewicz
 */
public class AtomsInDictionary implements Serializable {

	private EnumMap<MP5AtomType, Boolean> atoms = new EnumMap<MP5AtomType, Boolean>(MP5AtomType.class);

	public AtomsInDictionary() {
		for (MP5AtomType atom: MP5AtomType.values()) {
			atoms.put(atom, true); //all atoms types are selected by default
		}
	}

	public void setAtomIncluded(MP5AtomType atomType, Boolean included) {
		atoms.put(atomType, included);
	}

	public boolean isAtomIncluded(MP5AtomType atomType) {
		return atoms.get(atomType);
	}

}
