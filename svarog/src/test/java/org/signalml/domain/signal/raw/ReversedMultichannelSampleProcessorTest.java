package org.signalml.domain.signal.raw;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.signalml.BaseTestCase;
import static org.signalml.SignalMLAssert.assertArrayEquals;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.math.ArrayOperations;

public class ReversedMultichannelSampleProcessorTest extends BaseTestCase {

	private File file;
	private SignalExportDescriptor descriptor = new SignalExportDescriptor();

	@Test
	public void testGetSamples() throws IOException {

		file = new File("reversed_test.bin");
		double[][] samples = new double[][]{
				{1.0, 2.0, 3.0, 4.0},
				{5.0, 6.0, 7.0, 8.0}};
		DoubleArraySampleSource originalSampleSource = new DoubleArraySampleSource(samples, 2, 4);

		RawSignalWriter writer = new RawSignalWriter();
		writer.setMaximumBufferSize(2);
		writer.writeSignal(file, originalSampleSource, descriptor, null);

		RawSignalSampleSource rawSignalSampleSource = new RawSignalSampleSource(file, 2, 128.0F, descriptor.getSampleType(), descriptor.getByteOrder());
		ReversedMultichannelSampleProcessor reversedSampleSource = new ReversedMultichannelSampleProcessor(rawSignalSampleSource);

		double[] firstChannel = new double[4];
		reversedSampleSource.getSamples(0, firstChannel, 0, 4, 0);
		assertArrayEquals(samples[0], ArrayOperations.reverse(firstChannel), 1e-5);

		double[] secondChannel = new double[4];
		reversedSampleSource.getSamples(1, secondChannel, 0, 4, 0);
		assertArrayEquals(samples[1], ArrayOperations.reverse(secondChannel), 1e-5);

		rawSignalSampleSource.close();
		file.delete();
	}

	@Test
	public void testGetSamples2() throws IOException {

		double[][] samples = new double[][]{
				{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0},
				{11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0}};
		DoubleArraySampleSource originalSampleSource = new DoubleArraySampleSource(samples, 2, 7);
		ReversedMultichannelSampleProcessor reversed = new ReversedMultichannelSampleProcessor(originalSampleSource);

		double[] firstChannel = new double[7];
		reversed.getSamples(0, firstChannel, 0, 4, 0);
		reversed.getSamples(0, firstChannel, 4, 3, 4);
		assertArrayEquals(samples[0], ArrayOperations.reverse(firstChannel), 1e-5);

		double[] secondChannel = new double[7];
		reversed.getSamples(1, secondChannel, 0, 7, 0);
		assertArrayEquals(samples[1], ArrayOperations.reverse(secondChannel), 1e-5);
	}

}
