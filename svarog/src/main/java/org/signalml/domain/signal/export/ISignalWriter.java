package org.signalml.domain.signal.export;

import java.io.File;
import java.io.IOException;

import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * Abstract class for all signal writers (exporters).
 *
 * @author Piotr Szachewicz
 */
public interface ISignalWriter {

	/**
	 * Writes the whole signal from the specified
	 * {@link MultichannelSampleSource source} to the specified file
	 * based on the given {@link SignalExportDescriptor description}.
	 *
	 * @param outputFile the file to which the signal will written
	 * @param sampleSource the {@link MultichannelSampleSource source} of
	 * signal samples
	 * @param descriptor the {@link SignalExportDescriptor description}
	 * of the signal
	 * @param monitor the {@link SignalWriterMonitor monitor} which monitors
	 * the process of writing signal to file and can request its abortion
	 *
	 * @throws IOException if there is an error while writing bytes
	 * to file
	 */
	void writeSignal(
			File outputFile,
			MultichannelSampleSource sampleSource,
			SignalExportDescriptor descriptor,
			SignalWriterMonitor monitor) throws IOException;

}
