/* StandardBookSegment.java created 2008-02-16
 *
 */

package org.signalml.domain.book;

import java.util.Enumeration;

/** StandardBookSegment. Corresponds to a single Wigner map.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface StandardBookSegment {

	/** Sampling frequnecy in Hz.
	 *
	 *
	 */
	float getSamplingFrequency();

	/** Index of this segment in decomposition.
	 *
	 *  Note that this is the index of the segment in original decomposition. The index of this
	 *  segment in a book may be different.
	 *
	 *
	 */
	int getSegmentNumber();

	/** Channel index.
	 *
	 *  Note that this is the index of the channel in original decomposition. The index of this
	 *  channel in a book may be different.
	 *
	 *
	 */
	int getChannelNumber();

	/** Segment start time in seconds.
	 *
	 *
	 */
	float getSegmentTime();

	/** Segment length in points.
	 *
	 *
	 */
	int getSegmentLength();

	/** Segment length in seconds.
	 *
	 *
	 */
	float getSegmentTimeLength();

	/** Whether the segment has a signal.
	 *
	 *
	 */
	boolean hasSignal();

	/** Signal samples.
	 *
	 * @return null if no signal with segment
	 */
	float[] getSignalSamples();

	/** Signal energy.
	 *
	 *
	 */
	float getSignalEnergy();

	/** Number of atoms in decomposition
	 *
	 *
	 */
	int getAtomCount();

	/** Index'th atom from decomposition.
	 *
	 * @param index
	 *
	 */
	StandardBookAtom getAtomAt(int index);

	/** The index of this atom in the decomposition.
	 *
	 * @param atom
	 *
	 */
	int indexOfAtom(StandardBookAtom atom);

	/** Decomposition energy.
	 *
	 *
	 */
	float getDecompositionEnergy();

	/** The names of additional (version or format specific) properties of this segment.
	 *
	 *
	 */
	Enumeration<String> getPropertyNames();

	/** Obtain named additional property
	 *
	 * @param name
	 * @return null
	 * @throws IllegalArgumentException on unsupported property name
	 */
	Object getProperty(String name) throws IllegalArgumentException;





}
