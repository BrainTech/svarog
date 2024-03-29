package org.signalml.domain.signal.export.eeglab;

import java.io.File;
import java.io.IOException;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.export.ISignalWriter;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.util.matfiles.CompressedMatlabFileWriter;
import org.signalml.util.matfiles.MatlabFileWriter;
import org.signalml.util.matfiles.array.lazy.LazyExportDoubleArray;

/**
 * Exports signal to MATLAB MAT-file.
 *
 * @author Piotr Szachewicz
 */
public class MatlabSignalWriter implements ISignalWriter {

	public void writeSignal(File output, MultichannelSampleSource sampleSource, SignalExportDescriptor descriptor, SignalWriterMonitor monitor) throws IOException {

		LazySampleProvider lazySampleProvider = new LazySampleProvider(sampleSource);
		lazySampleProvider.setSignalWriterMonitor(monitor);
		LazyExportDoubleArray lazyExportDoubleArray = new LazyExportDoubleArray("data", lazySampleProvider);

		MatlabFileWriter writer = new CompressedMatlabFileWriter(output);
		writer.addElement(lazyExportDoubleArray);
		writer.write();

		if (monitor != null) {
			monitor.setProcessedSampleCount(sampleSource.getSampleCount(0));
		}
	}

}
