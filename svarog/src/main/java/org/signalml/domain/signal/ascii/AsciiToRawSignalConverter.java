package org.signalml.domain.signal.ascii;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorWriter;
import org.signalml.domain.signal.raw.RawSignalSampleType;

/**
 * Converter from ASCII signal files to RAW + XML data sets.
 *
 * Input (ASCII) files consist of lines separated by the new line sequence.
 * Each line corresponds to one time sample, and may consist of one or more values,
 * separated by a chosen character (default: comma), representing simultaneous
 * values on different channels. First line, preceding all data, may optionally
 * consist of channel names, separated by the same separator character.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class AsciiToRawSignalConverter {

	// byte order has to be consistent with DataOutputStream's writeDouble
	private static final RawSignalByteOrder BYTE_ORDER = RawSignalByteOrder.BIG_ENDIAN;

	private float samplingFrequency = 128.0f;
	private String separator = ",";

	/**
	 * Specify a sampling frequency.
	 *
	 * @param samplingFrequency  sampling frequency in Hz
	 */
	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	/**
	 * Specify a separator for the input file.
	 *
	 * @param separator  character separating values in line
	 */
	public void setSeparator(char separator) {
		// separator will be used in String.split which accepts regex
		this.separator = Pattern.quote(Character.toString(separator));
	}

	/**
	 * Read given ASCII file and write signal and metadata to given output files.
	 * If output files exist, their contents will be replaced.
	 *
	 * @param txtFile input ASCII file
	 * @param rawFile output binary signal file
	 * @param xmlFile output XML metadata file
	 * @throws IOException
	 */
	public void convertAsciiSignalToXml(File txtFile, File rawFile, File xmlFile) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
			try (DataOutputStream output = new DataOutputStream(new FileOutputStream(rawFile))) {
				int sampleCount = 0;

				String[] channels = reader.readLine().split(separator);
				if (!areTheseChannelNames(channels)) {
					processSamples(channels, output);
					++sampleCount;
					channels = generateChannelNames(channels.length);
				}
				for (String line; (line = reader.readLine()) != null && !line.isEmpty(); ++sampleCount) {
					String[] values = line.split(separator);
					processSamples(values, output);
				}

				RawSignalDescriptor rawSignalDescriptor = new RawSignalDescriptor();
				rawSignalDescriptor.setByteOrder(BYTE_ORDER);
				rawSignalDescriptor.setChannelCount(channels.length);
				rawSignalDescriptor.setChannelLabels(channels);
				rawSignalDescriptor.setExportFileName(txtFile.getName());
				rawSignalDescriptor.setSampleCount(sampleCount);
				rawSignalDescriptor.setSampleType(RawSignalSampleType.DOUBLE);
				rawSignalDescriptor.setSamplingFrequency(samplingFrequency);

				RawSignalDescriptorWriter descriptorWriter = new RawSignalDescriptorWriter();
				descriptorWriter.writeDocument(rawSignalDescriptor, xmlFile);
			}
		}
	}

	private static boolean areTheseChannelNames(String[] channels) {
		for (String channel : channels) {
			if (Character.isAlphabetic(channel.charAt(0))) {
				return true;
			}
		}
		return false;
	}

	private static String[] generateChannelNames(int channelCount) {
		String[] channels = new String[channelCount];
		for (int i=0; i<channelCount; ++i) {
			channels[i] = "L" + (i+1);
		}
		return channels;
	}

	private static void processSamples(String[] values, DataOutputStream output) throws IOException, NumberFormatException {
		for (String string : values) {
			double value = Double.parseDouble(string);
			output.writeDouble(value);
		}
	}
}
