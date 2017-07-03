package org.signalml.app.video;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Timestamps data for video frames.
 * Can be read from text files in "Timecode file format" v2/4.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class TimecodeData {

	private final double[] timestamps;

	/**
	 * Create a new instance with predefined data.
	 *
	 * @param timestamps  reference will be owned by the new object
	 */
	public TimecodeData(double[] timestamps) {
		this.timestamps = timestamps;
	}

	/**
	 * Get timestamp for given timestamp.
	 *
	 * @param index  0 &le; index &lt; size()
	 * @return  UTC timestamp in seconds
	 */
	public double get(int index) {
		return timestamps[index];
	}

	/**
	 * Get number of timestamps.
	 *
	 * @return  number of stored timestamps
	 */
	public int size() {
		return timestamps.length;
	}

	/**
	 * Read timestamp data from given file.
	 * Empty lines or lines beginning with "#" are ignored.
	 * Reading is interrupted on first invalid entry.
	 *
	 * @param file  existing file
	 * @return  created TimecodeData instance
	 * @throws IOException  if reading from file fails
	 */
	public static TimecodeData readFromFile(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			ArrayList<Double> buffer = new ArrayList<>();
			for (String line; (line = readLineSkippingComments(reader)) != null; ) {
				try {
					buffer.add(Double.parseDouble(line));
				} catch (NumberFormatException ex) {
					break;  // truncated entry at the end of file
				}
			}
			double[] timestamps = new double[buffer.size()];
			for (int i=0; i<timestamps.length; ++i) {
				timestamps[i] = buffer.get(i);
			}
			return new TimecodeData(timestamps);
		}
	}

	private static String readLineSkippingComments(BufferedReader reader) throws IOException {
		String line;
		do {
			line = reader.readLine();
			if (line == null) {
				break;
			}
			line = line.trim();
		} while (line.isEmpty() || line.startsWith("#"));
		return line;
	}

}
