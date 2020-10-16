package org.signalml.domain.signal.export.edf;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.export.ISignalWriter;
import org.signalml.domain.signal.export.edf.writer.EDFException;
import org.signalml.domain.signal.export.edf.writer.EDFwriter;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * Exports signal to EDF+ format.
 */
public class EDFSignalWriter implements ISignalWriter {

	private static final int MIN_VALUE_24_BIT_INT = -(1<<23);
	private static final int MAX_VALUE_24_BIT_INT = (1<<23)-1;

	/**
	 * EDFLIB_FILETYPE_EDFPLUS or EDFLIB_FILETYPE_BDFPLUS
	 */
	private final int filetype;

	/**
	 * Create a new exporter for EDF+ format.
	 */
	public EDFSignalWriter() {
		this(EDFwriter.EDFLIB_FILETYPE_EDFPLUS);
	}

	/**
	 * Create a new exporter for either EDF+ or BDF+ format.
	 *
	 * @param filetype EDFLIB_FILETYPE_EDFPLUS or EDFLIB_FILETYPE_BDFPLUS
	 */
	protected EDFSignalWriter(int filetype) {
		this.filetype = filetype;
	}

	/**
	 * Writes the signal to a file in EDF+ format.
	 *
	 * @param output output file path
	 * @throws IOException if the file cannot be writen
	 */
	@Override
	public void writeSignal(File output, MultichannelSampleSource sampleSource, SignalExportDescriptor descriptor, SignalWriterMonitor monitor) throws IOException {

		try {
			final int channelCount = sampleSource.getChannelCount();
			final int samplingFrequency = Math.round(sampleSource.getSamplingFrequency());
			EDFwriter writer = new EDFwriter(output.getCanonicalPath(), filetype, channelCount);

			double[] target = new double[samplingFrequency]; // buffer for 1 second
			int maxSampleCount = 0;

			double maxValue = Double.MIN_VALUE;
			double minValue = Double.MAX_VALUE;
			for (int c=0; c<channelCount; ++c) {
				final int sampleCount = sampleSource.getSampleCount(c);
				for (int sampleOffset=0; sampleOffset<sampleCount; sampleOffset+=samplingFrequency) {
					int count = Math.min(samplingFrequency, sampleCount-sampleOffset);
					sampleSource.getSamples(c, target, sampleOffset, count, 0);
					for (int i=0; i<count; ++i) {
						maxValue = Math.max(maxValue, target[i]);
						minValue = Math.min(minValue, target[i]);
					}
					if (monitor.isRequestingAbort()) {
						return;
					}
				}
				maxSampleCount = Math.max(maxSampleCount, sampleCount);
			}
			if (minValue == maxValue) {
				minValue -= 0.5;
				maxValue += 0.5;
			}

			for (int c=0; c<channelCount; ++c) {
				writer.setSignalLabel(c, sampleSource.getLabel(c));
				writer.setSampleFrequency(c, samplingFrequency);
				if (filetype == EDFwriter.EDFLIB_FILETYPE_BDFPLUS) {
					writer.setDigitalMinimum(c, MIN_VALUE_24_BIT_INT);
					writer.setDigitalMaximum(c, MAX_VALUE_24_BIT_INT);
				} else {
					writer.setDigitalMinimum(c, Short.MIN_VALUE);
					writer.setDigitalMaximum(c, Short.MAX_VALUE);
				}
				writer.setPhysicalMinimum(c, minValue);
				writer.setPhysicalMaximum(c, maxValue);
				writer.setPhysicalDimension(c, "uV");
			}

			for (int sampleOffset=0; sampleOffset<maxSampleCount; sampleOffset+=samplingFrequency) {
				monitor.setProcessedSampleCount(sampleOffset);
				for (int c=0; c<channelCount; ++c) {
					int count = Math.max(0, Math.min(samplingFrequency, sampleSource.getSampleCount(c) - sampleOffset));
					if (count > 0) {
						sampleSource.getSamples(c, target, sampleOffset, count, 0);
					}
					if (count < samplingFrequency) {
						Arrays.fill(target, count, samplingFrequency, 0);
					}
					writer.writePhysicalSamples(target);
				}
				if (monitor.isRequestingAbort()) {
					return;
				}
			}

			writer.close();

		} catch (EDFException ex) {
			throw new IOException("export to EDF or BDF failed", ex);
		}
	}
}
