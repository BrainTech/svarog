/* SignalWriterMonitor.java created 2007-11-09
 *
 */

package org.signalml.domain.signal;

/** SignalWriterMonitor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalWriterMonitor {

	void setProcessedSampleCount(int sampleCount);

	void abort();

	boolean isRequestingAbort();

}
