/* RawSignalWriter.java created 2008-01-28
 *
 */

package org.signalml.domain.signal.raw;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.exception.SanityCheckException;

/** RawSignalWriter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalWriter {

	private static final int BUFFER_SIZE = 8192;

	public void writeSignal(File signalFile, MultichannelSampleSource sampleSource, SignalExportDescriptor descriptor, int firstSample, int sampleCount, SignalWriterMonitor monitor) throws IOException {

		OutputStream os = null;

		try {
			os = new BufferedOutputStream(new FileOutputStream(signalFile));
			writeSignal(os, sampleSource, descriptor, firstSample, sampleCount, monitor);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ex) {
					// ignore
				}
			}
		}

	}

	public void writeSignal(OutputStream os, MultichannelSampleSource sampleSource, SignalExportDescriptor descriptor, int firstSample, int sampleCount, SignalWriterMonitor monitor) throws IOException {

		int bufferSize = Math.min(BUFFER_SIZE, sampleCount);

		RawSignalSampleType sampleType = descriptor.getSampleType();
		int sampleByteSize = sampleType.getByteWidth();

		int channelCount = sampleSource.getChannelCount();
		double[][] data = new double[channelCount][bufferSize];

		double[] dMultiBuffer = null;
		float[] fMultiBuffer = null;
		int[] iMultiBuffer = null;
		short[] sMultiBuffer = null;

		DoubleBuffer dBuffer = null;
		FloatBuffer fBuffer = null;
		IntBuffer iBuffer = null;
		ShortBuffer sBuffer = null;

		int multiSampleCount = channelCount*bufferSize;

		int cnt = 0;
		int length = channelCount * sampleCount;

		byte[] byteBuffer = new byte[multiSampleCount*sampleByteSize];

		ByteBuffer bBuffer = ByteBuffer.wrap(byteBuffer).order(descriptor.getByteOrder().getByteOrder());

		switch (sampleType) {

		case DOUBLE :
			dMultiBuffer = new double[multiSampleCount];
			dBuffer = bBuffer.asDoubleBuffer();
			break;

		case FLOAT :
			fMultiBuffer = new float[multiSampleCount];
			fBuffer = bBuffer.asFloatBuffer();
			break;

		case INT :
			iMultiBuffer = new int[multiSampleCount];
			iBuffer = bBuffer.asIntBuffer();
			break;

		case SHORT :
			sMultiBuffer = new short[multiSampleCount];
			sBuffer = bBuffer.asShortBuffer();
			break;

		default :
			throw new SanityCheckException("Unsupported type [" + sampleType + "]");

		}

		int bOffset;
		int channel = 0;
		int sample = 0;
		int currentSample = firstSample;
		int toGetCnt = 0;
		int samplesRemaining = sampleCount;
		int i;

		boolean normalize = descriptor.isNormalize();
		double normalizationFactor;
		if (normalize) {
			normalizationFactor = descriptor.getNormalizationFactor();
		} else {
			normalizationFactor = 1.0;
		}

		do {

			if (monitor != null && monitor.isRequestingAbort()) {
				return;
			}

			toGetCnt = Math.min(bufferSize, samplesRemaining);

			// get samples for all channels
			for (i=0; i<channelCount; i++) {
				sampleSource.getSamples(i, data[i], currentSample, toGetCnt, 0);
			}

			samplesRemaining -= toGetCnt;
			currentSample += toGetCnt;
			sample = 0;

			bOffset = 0;

			switch (sampleType) {

			case DOUBLE:
				// multiplex samples
				for (; cnt<length && bOffset<multiSampleCount; cnt++) {
					dMultiBuffer[bOffset] = (data[channel][sample] * normalizationFactor);
					bOffset++;
					channel = (channel+1) % channelCount;
					if (channel == 0) {
						sample++;
					}
				}

				// enforce byte order
				dBuffer.clear();
				dBuffer.put(dMultiBuffer, 0, bOffset);

				break;

			case FLOAT:
				// multiplex samples
				for (; cnt<length && bOffset<multiSampleCount; cnt++) {
					fMultiBuffer[bOffset] = (float)(data[channel][sample] * normalizationFactor);
					bOffset++;
					channel = (channel+1) % channelCount;
					if (channel == 0) {
						sample++;
					}
				}

				// enforce byte order
				fBuffer.clear();
				fBuffer.put(fMultiBuffer, 0, bOffset);

				break;

			case INT:
				// multiplex samples
				for (; cnt<length && bOffset<multiSampleCount; cnt++) {
					iMultiBuffer[bOffset] = (int) Math.round((data[channel][sample] * normalizationFactor));
					bOffset++;
					channel = (channel+1) % channelCount;
					if (channel == 0) {
						sample++;
					}
				}

				// enforce byte order
				iBuffer.clear();
				iBuffer.put(iMultiBuffer, 0, bOffset);

				break;

			case SHORT:
				// multiplex samples
				for (; cnt<length && bOffset<multiSampleCount; cnt++) {
					sMultiBuffer[bOffset] = (short) Math.round((data[channel][sample] * normalizationFactor));
					bOffset++;
					channel = (channel+1) % channelCount;
					if (channel == 0) {
						sample++;
					}
				}

				// enforce byte order
				sBuffer.clear();
				sBuffer.put(sMultiBuffer, 0, bOffset);

				break;

			default:
				break;
			}

			// write samples
			os.write(byteBuffer, 0, bOffset*sampleByteSize);

			if (monitor != null) {
				monitor.setProcessedSampleCount(currentSample - firstSample);
			}

		} while (cnt < length);

	}

	public void writeSignal(File signalFile, MultichannelSampleSource sampleSource, SignalExportDescriptor descriptor, SignalWriterMonitor monitor) throws IOException {

		int sampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);

		writeSignal(signalFile, sampleSource, descriptor, 0, sampleCount, monitor);

	}

	public void writeSignal(OutputStream os, MultichannelSampleSource sampleSource, SignalExportDescriptor descriptor, SignalWriterMonitor monitor) throws IOException {

		int sampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);

		writeSignal(os, sampleSource, descriptor, 0, sampleCount, monitor);

	}

	public void writeSignal(File signalFile, MultichannelSegmentedSampleSource sampleSource, SignalExportDescriptor descriptor, int segment, SignalWriterMonitor monitor) throws IOException {

		int sampleCount = sampleSource.getSegmentLength();

		writeSignal(signalFile, sampleSource, descriptor, segment * sampleSource.getSegmentLength(), sampleCount, monitor);

	}

	public void writeSignal(OutputStream os, MultichannelSegmentedSampleSource sampleSource, SignalExportDescriptor descriptor, int segment, SignalWriterMonitor monitor) throws IOException {

		int sampleCount = sampleSource.getSegmentLength();

		writeSignal(os, sampleSource, descriptor, segment * sampleSource.getSegmentLength(), sampleCount, monitor);

	}

}
