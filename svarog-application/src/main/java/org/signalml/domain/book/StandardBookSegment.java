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
	 * @return
	 */
	float getSamplingFrequency();
	
	/** Index of this segment in decomposition.
	 * 
	 *  Note that this is the index of the segment in original decomposition. The index of this
	 *  segment in a book may be different. 
	 * 
	 * @return
	 */
	int getSegmentNumber();	
	
	/** Channel index.
	 * 
	 *  Note that this is the index of the channel in original decomposition. The index of this
	 *  channel in a book may be different. 
	 *
	 * @return
	 */
	int getChannelNumber();

	/** Segment start time in seconds.
	 * 
	 * @return
	 */
	float getSegmentTime();
	
	/** Segment length in points.
	 * 
	 * @return
	 */
	int getSegmentLength();

	/** Segment length in seconds.
	 * 
	 * @return
	 */
	float getSegmentTimeLength();
	
	/** Whether the segment has a signal.
	 * 
	 * @return
	 */
	boolean hasSignal();
		
	/** Signal samples.
	 * 
	 * @return null if no signal with segment
	 */
	float[] getSignalSamples();
	
	/** Signal energy.
	 * 
	 * @return
	 */
	float getSignalEnergy();
		
	/** Number of atoms in decomposition
	 * 
	 * @return
	 */
	int getAtomCount();
		
	/** Index'th atom from decomposition. 
	 * 
	 * @param index
	 * @return
	 */
	StandardBookAtom getAtomAt(int index);
		
	/** The index of this atom in the decomposition.
	 * 
	 * @param atom
	 * @return
	 */
	int indexOfAtom( StandardBookAtom atom );
	
	/** Decomposition energy.
	 * 
	 * @return
	 */
	float getDecompositionEnergy();
	
	/** The names of additional (version or format specific) properties of this segment.
	 * 
	 * @return
	 */
	Enumeration<String> getPropertyNames();
		
	/** Obtain named additional property
	 * 
	 * @param name
	 * @return null
	 * @throws IllegalArgumentException on unsupported property name
	 */
	Object getProperty( String name ) throws IllegalArgumentException;
	
	
	
	
	
}
