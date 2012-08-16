/* SignalChecksumProgressMonitor.java created 2007-12-09
 *
 */

package org.signalml.app.document.signal;

import org.signalml.app.worker.SignalChecksumWorker;
import org.signalml.domain.signal.SignalChecksum;

/**
 * Interface for a monitor of the {@link SignalChecksumWorker worker}
 * which calculates the {@link SignalChecksum checksum} of the signal.
 * Allows to:
 * <ul>
 * <li>get the information if the job was cancelled,</li>
 * <li>get and set the number of processed bytes.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalChecksumProgressMonitor {

	/**
	 * Answers if the calculation of the {@link SignalChecksum checksum} was
	 * canceled
	 * @return {@code true} if the calculation of the checksum was
	 * canceled, {@code false} otherwise
	 */
	boolean isCancelled();

	/**
	 * Returns the number of the bytes of the signal that were already
	 * processed in the calculation.
	 * @return the number of the bytes of the signal that were already
	 * processed in the calculation
	 */
	long getBytesProcessed();

	/**
	 * Sets the number of the bytes of the signal that were already
	 * processed in the calculation.
	 * @param bytesProceeded the number of the bytes of the signal that were
	 * already processed in the calculation
	 */
	void setBytesProcessed(long bytesProceeded);

}
