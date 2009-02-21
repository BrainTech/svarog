/* StandardBookAtom.java created 2008-02-16
 * 
 */

package org.signalml.domain.book;

import java.util.Enumeration;

/** Standardized atom.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface StandardBookAtom {

    public static final int DIRACDELTA_IDENTITY=10;
	public static final int GAUSSFUNCTION_IDENTITY=11;
	public static final int SINCOSWAVE_IDENTITY=12;
    public static final int GABORWAVE_IDENTITY=13;
	
	/** Atom type.
	 * 
	 *  Can change to enum.
	 * 
	 * @return DIRACDELTA_IDENTITY - GABORWAVE_IDENTITY
	 */
	int getType();
	
	/** Atom base (signal length) in points.
	 * 
	 * @return
	 */
	int getBaseLength();
	
	/** Sampling frequency in Hz.
	 * 
	 * @return
	 */
	float getSamplingFrequency();
	
	/** Iteration number.
	 * 
	 * @return
	 */
	int getIteration();
	
	/** Modulus (energy > 0).
	 * 
	 * @return
	 */
	float getModulus();
	
	/** Amplitude.
	 * 
	 * @return
	 */
	float getAmplitude();
	
	/** Position in points relative to the beginning of decomposed signal.
	 * 
	 * @return
	 */
	int getPosition();
	
	/** Position in seconds relative to the beginning of decomposed signal.
	 * 
	 * @return
	 */
	float getTimePosition();
	
	/** Scale in points.
	 * 
	 * @return
	 */
	int getScale();

	/** Scale in seconds.
	 * 
	 * @return
	 */
	float getTimeScale();
	
	/** Frequency in natural units 0 to signal length / 2.
	 * 
	 * 
	 * @return
	 */
	int getFrequency();

	/** Frequency in Hz.
	 * 
	 * 
	 * @return
	 */
	float getHzFrequency();
	
	/** Phase (-pi to pi).
	 * 
	 * @return
	 */
	float getPhase();
			
	/** The names of additional (version or format specific) properties of this atom.
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
