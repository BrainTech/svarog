package org.signalml.domain.signal.ascii;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.signalml.domain.signal.raw.RawSignalDescriptor;

/**
 * Provides signal descriptor of ASCII files.
 * Main goal of this class is to read (or generate) channel names.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class AsciiSignalDescriptorReader {

	public static final String SEPARATOR_REGEX = "[ \t,]+";
	public static final char FIRST_LINE_START = '#';

	/**
	 * Read a {@link RawSignalDescriptor} of an ASCII signal
	 * from the given file.
	 *
	 * @param file the file containing the ASCII signal
	 * @return the created description
	 * @throws IOException if error occurs while reading the file
	 */
	public RawSignalDescriptor readDocument(File file) throws IOException {
		String[] channels = extractChannelNames(file);

		RawSignalDescriptor rawSignalDescriptor = new RawSignalDescriptor();
		rawSignalDescriptor.setSourceSignalType(RawSignalDescriptor.SourceSignalType.ASCII);
		rawSignalDescriptor.setChannelCount(channels.length);
		rawSignalDescriptor.setChannelLabels(channels);
		rawSignalDescriptor.setExportFileName(file.getName());
		rawSignalDescriptor.setSamplingFrequency(0.0f);
		rawSignalDescriptor.setCalibrationGain(1.0f);
		rawSignalDescriptor.setCalibrationOffset(0.0f);
		return rawSignalDescriptor;
	}

	private static String[] extractChannelNames(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String firstLine = reader.readLine();
			if (firstLine == null) {
				return new String[0];
			}
			if (firstLine.startsWith(String.valueOf(FIRST_LINE_START))) {
				// we have channel names
				return firstLine.substring(1).split(SEPARATOR_REGEX);
			} else {
				// values for first sample
				int channelCount = firstLine.split(SEPARATOR_REGEX).length;
				return generateChannelNames(channelCount);
			}
		}
	}

	private static String[] generateChannelNames(int channelCount) {
		String[] channels = new String[channelCount];
		for (int i=0; i<channelCount; ++i) {
			channels[i] = "L" + (i+1);
		}
		return channels;
	}
}
