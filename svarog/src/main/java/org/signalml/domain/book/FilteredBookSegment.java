/* FilteredBookSegment.java created 2008-02-28
 *
 */

package org.signalml.domain.book;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import org.signalml.domain.book.filter.AtomFilterChain;

/** FilteredBookSegment
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class FilteredBookSegment implements StandardBookSegment {

	private StandardBookSegment source;

	private StandardBookAtom[] acceptedAtoms;
	private HashMap<StandardBookAtom,Integer> atomIndexMap;

	public FilteredBookSegment(StandardBookSegment source, AtomFilterChain filter) {
		this.source = source;

		LinkedList<StandardBookAtom> atoms = new LinkedList<StandardBookAtom>();

		atomIndexMap = new HashMap<StandardBookAtom, Integer>();

		int atomCount = source.getAtomCount();
		StandardBookAtom atom;
		for (int i=0; i<atomCount; i++) {
			atom = source.getAtomAt(i);
			if (filter == null || filter.matches(source, atom)) {
				atomIndexMap.put(atom, atoms.size());
				atoms.add(atom);
			}
		}

		acceptedAtoms = new StandardBookAtom[atoms.size()];
		atoms.toArray(acceptedAtoms);

	}

	@Override
	public StandardBookAtom getAtomAt(int index) {
		return acceptedAtoms[index];
	}

	@Override
	public int getAtomCount() {
		return acceptedAtoms.length;
	}

	@Override
	public int getChannelNumber() {
		return source.getChannelNumber();
	}

	@Override
	public float getDecompositionEnergy() {
		return source.getDecompositionEnergy();
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return source.getProperty(name);
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return source.getPropertyNames();
	}

	@Override
	public int getSegmentLength() {
		return source.getSegmentLength();
	}

	@Override
	public float getSamplingFrequency() {
		return source.getSamplingFrequency();
	}

	@Override
	public float getSegmentTimeLength() {
		return source.getSegmentTimeLength();
	}

	@Override
	public int getSegmentNumber() {
		return source.getSegmentNumber();
	}

	@Override
	public float getSegmentTime() {
		return source.getSegmentTime();
	}

	@Override
	public float getSignalEnergy() {
		return source.getSignalEnergy();
	}

	@Override
	public float[] getSignalSamples() {
		return source.getSignalSamples();
	}

	@Override
	public boolean hasSignal() {
		return source.hasSignal();
	}

	@Override
	public int indexOfAtom(StandardBookAtom atom) {
		return atomIndexMap.get(atom).intValue();
	}

}
