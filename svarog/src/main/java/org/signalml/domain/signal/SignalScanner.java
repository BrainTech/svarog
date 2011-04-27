/* SignalScanner.java created 2008-01-30
 *
 */

package org.signalml.domain.signal;

import org.signalml.app.view.signal.SignalScanResult;

/**
 * This class allows to scan the signal for the samples of extremal values.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalScanner {

	private static final int BUFFER_SIZE = 8192;

        /**
         * Looks for the minimal and maximal value (of the sample) in the signal.
         * Informs the <code>monitor</code> about the number of already
         * processed samples and aborts operation if monitor says to do so.
         * @param sampleSource the {@link MultichannelSampleSource source}
         * of samples
         * @param monitor the {@link SignalWriterMonitor monitor} for this
         * operation
         * @return the created {@link SignalScanResult result} containing
         * the minimal and maximal value in the signal
         */
	public SignalScanResult scanSignal(MultichannelSampleSource sampleSource, SignalWriterMonitor monitor) {

		int channelCount = sampleSource.getChannelCount();
		int[] sampleCounts = new int[channelCount];
		int[] processedCounts = new int[channelCount];

		double[] data = new double[BUFFER_SIZE];

		double minSampleValue = Double.MAX_VALUE;
		double maxSampleValue = Double.MIN_VALUE;
		int i;
		int e;

		boolean moreSamples = false;

		for (i=0; i<channelCount; i++) {
			sampleCounts[i] = sampleSource.getSampleCount(i);
			if (sampleCounts[i] > 0) {
				moreSamples = true;
			}
		}

		int toGet;
		int totalProcessed = 0;

		while (moreSamples) {

			moreSamples = false;

			for (i=0; i<channelCount; i++) {

				if (sampleCounts[i] > processedCounts[i]) {

					if (monitor != null && monitor.isRequestingAbort()) {
						return null;
					}

					toGet = sampleCounts[i] - processedCounts[i];
					if (toGet > BUFFER_SIZE) {
						toGet = BUFFER_SIZE;
						moreSamples = true;
					}

					sampleSource.getSamples(i, data, processedCounts[i], toGet, 0);

					for (e=0; e<toGet; e++) {

						if (data[e] < minSampleValue) {
							minSampleValue = data[e];
						}
						if (data[e] > maxSampleValue) {
							maxSampleValue = data[e];
						}

					}

					processedCounts[i] += toGet;
					if (processedCounts[i] > totalProcessed) {
						totalProcessed = processedCounts[i];
						if (monitor != null) {
							monitor.setProcessedSampleCount(totalProcessed);
						}
					}

				}

			}

		}

		SignalScanResult result = new SignalScanResult();
		result.setMinSignalValue(minSampleValue);
		result.setMaxSignalValue(maxSampleValue);

		return result;

	}

}
