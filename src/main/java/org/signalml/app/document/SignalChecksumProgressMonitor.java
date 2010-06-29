/* SignalChecksumProgressMonitor.java created 2007-12-09
 *
 */

package org.signalml.app.document;

/** SignalChecksumProgressMonitor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalChecksumProgressMonitor {

	boolean isCancelled();

	long getBytesProcessed();

	void setBytesProcessed(long bytesProceeded);

}
