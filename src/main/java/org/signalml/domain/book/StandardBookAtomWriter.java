package org.signalml.domain.book;

public interface StandardBookAtomWriter {
	void set(StandardBookAtom atom);

	void setType(int type);

	/** Modulus.
	 *
	 *
	 */
	void setModulus(float modulus);

	/** Amplitude.
	 *
	 *
	 */
	void setAmplitude(float amplitude);

	/** Position in seconds relative to the beginning of decomposed signal.
	 *
	 *
	 */
	void setPosition(float position);

	/** Scale in seconds.
	 *
	 *
	 */
	void setScale(float scale);

	/** Frequency in Hz.
	 *
	 *
	 *
	 */
	void setFrequency(float freq);

	/** Phase.
	 *
	 *
	 */
	void setPhase(float phase);

}
