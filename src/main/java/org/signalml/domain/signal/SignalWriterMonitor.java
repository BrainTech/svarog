/* SignalWriterMonitor.java created 2007-11-09
 *
 */

package org.signalml.domain.signal;

/**
 * This is an interface for the monitor that monitors the processing of samples.
 * Can request abortion of that process.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalWriterMonitor {

        /**
         * Sets the number of already processed samples.
         * @param sampleCount the number of already processed samples
         */
	void setProcessedSampleCount(int sampleCount);

        /**
         * Sets that this monitor should request abortion of the monitored
         * process.
         */
	void abort();

        /**
         * Returns if this monitor requests the abortion of the monitored
         * process.
         * @return true if this monitor requests the abortion of the monitored
         * process, false otherwise
         */
	boolean isRequestingAbort();

}
