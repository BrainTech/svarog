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
	 *
	 */
	int getBaseLength();

	/** Sampling frequency in Hz.
	 *
	 *
	 */
	float getSamplingFrequency();

	/** Iteration number.
	 *
	 *
	 */
	int getIteration();

	/** Modulus (energy > 0).
	 *
	 *
	 */
	float getModulus();

	/** Amplitude.
	 *
	 *
	 */
	float getAmplitude();

	/** Position in points relative to the beginning of decomposed signal.
	 *
	 *
	 */
	int getPosition();

	/** Position in seconds relative to the beginning of decomposed signal.
	 *
	 *
	 */
	float getTimePosition();

	/** Scale in points.
	 *
	 *
	 */
	int getScale();

	/** Scale in seconds.
	 *
	 *
	 */
	float getTimeScale();

	/** Frequency in natural units 0 to signal length / 2.
	 *
	 *
	 *
	 */
	int getNaturalFrequency();
	
	/**
	 * The frequency which was read from the book file
	 * (not always equal to the natural frequency).
	 */
	float getFrequency();

	/** Frequency in Hz.
	 *
	 *
	 *
	 */
	float getHzFrequency();

	/** Phase (-pi to pi).
	 *
	 *
	 */
	float getPhase();

	/** The names of additional (version or format specific) properties of this atom.
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
