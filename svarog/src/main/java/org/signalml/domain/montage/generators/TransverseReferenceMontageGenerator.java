package org.signalml.domain.montage.generators;

import static org.signalml.app.util.i18n.SvarogI18n._;

public class TransverseReferenceMontageGenerator extends LongitudalReferenceMontageGenerator {

	String[][] PAIRS = {{"F7", "Fp1"},
	{"Fp1", "Fp2"},
	{"Fp2", "F8"},
	{"F7", "F3"},
	{"F3", "Fz"},
	{"Fz", "F4"},
	{"F4", "F8"},
	{"T3", "C3"},
	{"C3", "Cz"},
	{"Cz", "C4"},
	{"C4", "T4"},
	{"T5", "P3"},
	{"P3", "Pz"},
	{"Pz", "P4"},
	{"P4", "T6"},
	{"T5", "O1"},
	{"O1", "O2"},
	{"O2", "T6"}
	};

	public TransverseReferenceMontageGenerator() {
		super();
		this.setName(_("Transverse Banana"));
		this.setChannelPairs(PAIRS);
	}

}
