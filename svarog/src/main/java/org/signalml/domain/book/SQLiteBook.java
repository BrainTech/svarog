package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import org.apache.log4j.Logger;
import org.signalml.util.FileUtils;

/**
 * StandardBook implementation based on SQLITE3 format.
 *
 * @author mdovgialo & ptr
 */
public class SQLiteBook implements StandardBook {

	protected static final Logger LOGGER = Logger.getLogger(SQLiteBook.class);

	private final int channelCount;
	private final double samplingFrequency;
	private final int segmentCount;
	private final Connection conn;
	private final File file;

	public SQLiteBook(File file) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException ex) {
			throw new SQLException("could not access SQLite database driver");
		}

		conn = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());

		this.channelCount = Integer.parseInt(getMetadataValue("channel_count"));
		this.samplingFrequency = Double.parseDouble(getMetadataValue("sampling_frequency_Hz"));
		this.segmentCount = Integer.parseInt(getMetadataValue("segment_count"));

		this.file = file;
	}

	public void createCopy(File destinationFile) throws IOException {
		FileUtils.copyFile(file, destinationFile);
	}

	@Override
	public String getVersion() {
		return "SQLite3";
	}

	@Override
	public String getBookComment() {
		return "";
	}

	@Override
	public float getEnergyPercent() {
		return Float.NaN; // not supported
	}

	@Override
	public int getMaxIterationCount() {
		return 0; // not supported
	}

	@Override
	public int getDictionarySize() {
		return 0; // not supported
	}

	@Override
	public char getDictionaryType() {
		return 'F'; // OCTAVE_FIXED
	}

	@Override
	public float getSamplingFrequency() {
		return (float) samplingFrequency;
	}

	@Override
	public float getCalibration() {
		return 0.0f;
	}

	@Override
	public int getSignalChannelCount() {
		return channelCount;
	}

	@Override
	public String getTextInfo() {
		return ""; // not supported
	}

	@Override
	public String getWebSiteInfo() {
		return ""; // not supported
	}

	@Override
	public String getDate() {
		return ""; // not supported
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

	@Override
	public String getChannelLabel(int channelIndex) {
		return null; // not supported
	}

	@Override
	public int getSegmentCount() {
		return segmentCount;
	}

	@Override
	public StandardBookSegment[] getSegmentAt(int segmentIndex) {

		StandardBookSegment[] channels = new MutableBookSegment[channelCount];
		for (int channelIndex=0; channelIndex<channelCount; ++channelIndex) {
			channels[channelIndex] = getSegmentAt(segmentIndex, channelIndex);
		}
		return channels;
	}

	@Override
	public StandardBookSegment getSegmentAt(int segmentIndex, int channelIndex) {
		try {
			int sampleCount;
			double segmentOffset;
			try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM segments WHERE segment_id=?")) {
				stmt.setInt(1, segmentIndex);
				try (ResultSet rs = stmt.executeQuery()) {
					sampleCount = rs.getInt("sample_count");
					segmentOffset = rs.getDouble("segment_offset_s");
				}
			}

			DefaultMutableBookSegment segment = new DefaultMutableBookSegment(
				(float) samplingFrequency,
				channelIndex+1,
				segmentIndex+1,
				(float) segmentOffset,
				sampleCount
			);

			try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM atoms WHERE segment_id=? AND channel_id=? ORDER BY iteration")) {
				stmt.setInt(1, segmentIndex);
				stmt.setInt(2, channelIndex);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						segment.addAtom(parseSingleAtom(rs, samplingFrequency, sampleCount));
					}
				}
			}

			try (PreparedStatement stmt = conn.prepareStatement("SELECT samples_float32 FROM samples WHERE segment_id=? AND channel_id=?")) {
				stmt.setInt(1, segmentIndex);
				stmt.setInt(2, channelIndex);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						float[] samples = new float[sampleCount];
						ByteBuffer.wrap(rs.getBytes("samples_float32")).asFloatBuffer().get(samples);
						segment.setSignalSamples(samples);
					}
				}
			}

			return segment;

		} catch (SQLException ex) {
			LOGGER.error("could not read segment from SQLite book", ex);
			return null;
		}
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return Collections.emptyEnumeration();
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		throw new IllegalArgumentException("SQLite books have no available properties");
	}

	@Override
	public void close() {
		try {
			conn.close();
		} catch (SQLException ex) {
			LOGGER.warn("could not properly close SQLite book file", ex);
		}
	}

	private String getMetadataValue(String param) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT value FROM metadata WHERE param=?")) {
			stmt.setString(1, param);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.getString("value");
			}
		}
	}

	private static DefaultBookAtom parseSingleAtom(ResultSet rs, double samplingFrequency, int sampleCount) throws SQLException {
		return new DefaultBookAtom(
				(float) samplingFrequency,
				sampleCount,
				composeInternalType(rs.getString("envelope"), rs.getDouble("f_Hz")),
				rs.getInt("iteration"),
				(float) Math.sqrt(rs.getDouble("energy") * samplingFrequency),
				(float) (rs.getDouble("f_Hz") / samplingFrequency * sampleCount),
				(float) (rs.getDouble("t0_s") * samplingFrequency),
				(float) (rs.getDouble("scale_s") * samplingFrequency),
				(float) rs.getDouble("amplitude"),
				(float) rs.getDouble("phase")
		);
	}

	private static int composeInternalType(String envelope, double frequency) {
		switch (envelope) {
			case "delta":
				return StandardBookAtom.DIRACDELTA_IDENTITY;
			case "none":
				return StandardBookAtom.SINCOSWAVE_IDENTITY;
			case "gauss":
				if (frequency == 0.0) {
					return StandardBookAtom.GAUSSFUNCTION_IDENTITY;
				}
				return StandardBookAtom.GABORWAVE_IDENTITY;
			default:
				return 0; // unknown type
		}
	}

}
