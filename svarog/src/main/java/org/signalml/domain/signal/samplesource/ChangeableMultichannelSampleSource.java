/* ChangeableMultichannelSampleSource.java created 2010-09-07
 *
 */

package org.signalml.domain.signal.samplesource;

import java.util.List;

/**
 * This interface represents a multichannel sample source which can be changed
 * by adding new samples to it. Additional functions like semaphore locking
 * and unlocking are available to reinforce multithreaded use.
 *
 * @author Piotr Szachewicz
 */
public interface ChangeableMultichannelSampleSource {

	/**
	 * Returns the number of new samples added counting from last call of
	 * this function.
	 *
	 * @return number of new samples added
	 */
	int getNewSamplesCount();

	/**
	 * Clears the counter of new samples used by {@link #getNewSamplesCount()}.
	 */
	void clearNewSamplesCount();

	void addSampleChunk(float[] newSamples);

	void addSamples(List<float[]> newSamples);

	void addSamples(float[] newSamples);

	/**
	 * Acquires the built in binary semaphore. It does not prevent other threads
	 * from using the {@link ChangeableMultichannelSampleSource}, unless their
	 * critical sections are surrounded by {@link #lock()} and {@link #unlock()}
	 * methods.
	 */
	void lock();

	/**
	 * Releases the built in binary semaphore.
	 */
	void unlock();

}
