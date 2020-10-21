package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * IncrementalBookWriter implementation for SQLite book files.
 * Instances of this class are created by SQLiteBookBuilder
 * when writing SQLite book files incrementally.
 *
 * @author ptr@mimuw.edu.pl
 */
public class SQLiteBookWriterIncremental implements IncrementalBookWriter {

	private final int channelCount;
	private final double samplingFrequency;
	private final Connection conn;

	private int totalSegmentsWritten = 0;

	public SQLiteBookWriterIncremental(File file, int channelCount, double samplingFrequency) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException ex) {
			throw new SQLException("could not access SQLite database driver");
		}

		if (file.exists()) {
			file.delete();
		}
		this.channelCount = channelCount;
		this.samplingFrequency = samplingFrequency;

		conn = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
		conn.setAutoCommit(false);

		createTable("metadata", new String[] {
			"param TEXT PRIMARY KEY",
			"value TEXT NOT NULL"
		});
		createTable("atoms", new String[] {
			"segment_id INTEGER NOT NULL",
			"channel_id INTEGER NOT NULL",
			"iteration UNSIGNED INTEGER NOT NULL",
			"amplitude REAL NOT NULL",
			"energy REAL NOT NULL",
			"envelope TEXT NOT NULL",
			"f_Hz REAL",
			"phase REAL",
			"scale_s REAL",
			"t0_s REAL",
			"t0_abs_s REAL",
			"PRIMARY KEY (segment_id, channel_id, iteration)"
		});
		createTable("segments", new String[] {
			"segment_id INTEGER PRIMARY KEY",
			"sample_count UNSIGNED INTEGER NOT NULL",
			"segment_length_s REAL NOT NULL",
			"segment_offset_s REAL NOT NULL"
		});
		createTable("samples", new String[] {
			"segment_id INTEGER NOT NULL",
			"channel_id INTEGER NOT NULL",
			"samples_float32 BLOB NOT NULL",
			"PRIMARY KEY (segment_id, channel_id)"
		});

		writeMetadata("channel_count", Integer.toString(channelCount));
		writeMetadata("sampling_frequency_Hz", Double.toString(samplingFrequency));
	}

	@Override
	public void writeSegment(StandardBookSegment[] segments) throws IOException {

		final double segmentOffset = segments[0].getSegmentTime();

		try {
			for (int c=0; c<channelCount; ++c) {
				StandardBookSegment bookChannel = segments[c];
				if (c == 0) {
					writeSegmentInfo(bookChannel.getSegmentLength(), segmentOffset);
				}
				final int atomCount = bookChannel.getAtomCount();
				for (int a=0; a<atomCount; ++a) {
					StandardBookAtom bookAtom = bookChannel.getAtomAt(a);
					writeSingleAtom(c, a, bookAtom, segmentOffset);
				}

				if (bookChannel.hasSignal()) {
					// TO jeszcze nie działa
					writeSegmentSamples(c, bookChannel.getSignalSamples());
				}
			}

			totalSegmentsWritten++;
		} catch (SQLException ex) {
			throw new IOException("writing SQLite book file failed", ex);
		}
	}

	@Override
	public void writeSegment(StandardBookSegmentWriter segment) throws IOException {
		throw new UnsupportedOperationException("this variant of writeSegment is not supported with SQLite writer");
	}

	@Override
	public void close() throws IOException {
		try {
			writeMetadata("segment_count", Integer.toString(totalSegmentsWritten));
			conn.commit();
			conn.close();
		} catch (SQLException ex) {
			throw new IOException("could not properly finalize SQLite book file", ex);
		}
	}

	private void createTable(String tableName, String[] tableSpecs) throws SQLException {
		try (Statement stmt = conn.createStatement()) {
			stmt.execute("CREATE TABLE " + tableName + " (" + String.join(", ", tableSpecs) + ")");
		}
	}

	private void writeMetadata(String param, String value) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO metadata VALUES (?, ?)")) {
			stmt.setString(1, param);
			stmt.setString(2, value);
			stmt.execute();
		}
	}

	private void writeSegmentInfo(int sampleCount, double segmentOffset) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO segments VALUES (?, ?, ?, ?)")) {
			stmt.setInt(1, totalSegmentsWritten);
			stmt.setInt(2, sampleCount);
			stmt.setDouble(3, sampleCount / samplingFrequency);
			stmt.setDouble(4, segmentOffset);
			stmt.execute();
		}
	}

	private void writeSegmentSamples(int channel, float[] samples) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO samples VALUES (?, ?, ?)")) {
			final int byteCount = 4 * samples.length;
			ByteBuffer buffer = ByteBuffer.allocate(byteCount);
			for (float sample : samples) {
				buffer.putFloat(sample);
			}

			stmt.setInt(1, totalSegmentsWritten);
			stmt.setInt(2, channel);
			stmt.setBytes(3, buffer.array());
			stmt.execute();
		}
	}

	private void writeSingleAtom(int channel, int iteration, StandardBookAtom bookAtom, double segmentOffset) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO atoms VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
			double position = bookAtom.getPosition() / samplingFrequency;

			stmt.setInt(1, totalSegmentsWritten);
			stmt.setInt(2, channel);
			stmt.setInt(3, iteration);
			stmt.setDouble(4, bookAtom.getAmplitude());
			stmt.setDouble(5, bookAtom.getModulus() * bookAtom.getModulus() / samplingFrequency);
			stmt.setString(6, composeEnvelope(bookAtom));
			if (bookAtom.getType() != StandardBookAtom.DIRACDELTA_IDENTITY) {
				stmt.setDouble(7, bookAtom.getHzFrequency());
				stmt.setDouble(8, bookAtom.getPhase());
				if (bookAtom.getType() != StandardBookAtom.SINCOSWAVE_IDENTITY) {
					stmt.setDouble(9, bookAtom.getScale() / samplingFrequency);
				}
			}
			stmt.setDouble(10, position);
			stmt.setDouble(11, segmentOffset + position);
			stmt.execute();
		}
	}

	private static String composeEnvelope(StandardBookAtom bookAtom) {
		switch (bookAtom.getType()) {
			case StandardBookAtom.DIRACDELTA_IDENTITY:
				return "delta";
			case StandardBookAtom.SINCOSWAVE_IDENTITY:
				return "none";
			default:
				return "gauss";
		}
	}
}
