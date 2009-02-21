package org.signalml.domain.book;

public interface StandardBookAtomWriter {
	 void set(StandardBookAtom atom);
	
     void setType(int type);
	
	/** Modulus.
	 * 
	 * @return
	 */
	 void setModulus(float modulus);
	
	/** Amplitude.
	 * 
	 * @return
	 */
	 void setAmplitude(float amplitude);
	
	/** Position in seconds relative to the beginning of decomposed signal.
	 * 
	 * @return
	 */
	void setPosition(float position);
	
	/** Scale in seconds.
	 * 
	 * @return
	 */
	void setScale(float scale);
	
	/** Frequency in Hz.
	 * 
	 * 
	 * @return
	 */
	void setFrequency(float freq);
	
	/** Phase.
	 * 
	 * @return
	 */
	void setPhase(float phase);
			
}
