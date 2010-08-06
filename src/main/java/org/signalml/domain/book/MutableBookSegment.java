/* MutableBookSegment.java created 2008-02-16
 *
 */

package org.signalml.domain.book;

/** MutableBookSegment
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MutableBookSegment extends StandardBookSegment {

	/** Create and return a new atom compatible with this segment.
	 *
	 *
	 * @param type
	 * @param modulus
	 * @param amplitude
	 * @param position
	 * @param scale
	 * @param frquency
	 * @param phase
	 *
	 */
	StandardBookAtom createAtom(int type, int iteration, float modulus, float amplitude, int position, int scale, int frequency, float phase);

	/** Add atom to segment (at the end).
	 *
	 * @param atom
	 */
	int addAtom(StandardBookAtom atom);

	/** Replace index'th atom.
	 *
	 * @param index
	 * @param atom
	 */
	void setAtomAt(int index, StandardBookAtom atom);

	/** Remove index'th atom from the segment and return it.
	 *
	 * @param index
	 *
	 */
	StandardBookAtom removeAtomAt(int index);

	/** Remove all atoms.
	 *
	 */
	void clear();

	/** Sets signal samples.
	 *
	 * @param samples null to remove samples from segment
	 */
	void setSignalSamples(float[] samples);

	/** Sets signal energy.
	 *
	 * @param signalEnergy
	 */
	void setSignalEnergy(float signalEnergy);

	/** Sets decomposition energy
	 *
	 * @param decompositionEnergy
	 */
	void setDecompositionEnergy(float decompositionEnergy);

}
