/* 1999 08 20 */

package pl.edu.fuw.MP.Core;

public class Book {
	/**
	 *
	 */
	public MPParameters param;
	public NewAtom atoms[];

	public Book() {
		param=new MPParameters();
	}

	public NewAtom []getAtoms() {
		return atoms;
	}

	public void importAtoms(NewBookLibrary lib) {
		int size=lib.getNumOfAtoms();

		atoms=new NewAtom[size];
		for (int i=0 ; i<size ; i++) {
			NewAtom stmp=lib.getAtom(i);
			atoms[i]=new NewAtom();
			atoms[i].scale=stmp.scale;
			atoms[i].frequency=stmp.frequency;
			atoms[i].position=stmp.position;
			atoms[i].modulus=stmp.modulus;
			atoms[i].amplitude=stmp.amplitude;
			atoms[i].phase=stmp.phase;
			atoms[i].index=i;
		}

		param.MaxNumberOfIteration=lib.getMaxNumberOfIteration();
		param.EnergyEps=		   lib.getEnergyPercent();
		param.DictionarySize=	  lib.getDictionarySize();
		param.SamplingFrequency=   lib.getSamplingFreq();
	}
}



