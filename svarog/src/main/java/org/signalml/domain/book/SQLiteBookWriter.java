package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * BookWriter implementation for SQLite book files.
 *
 * @author ptr@mimuw.edu.pl
 */
public class SQLiteBookWriter implements BookWriter {

	public SQLiteBookWriter() {
		// nothing here
	}

	@Override
	public void writeBookComplete(StandardBook book, File file) throws IOException {
		IncrementalBookWriter incremental = writeBookIncremental(book, file);
		try {
			int segmentCount = book.getSegmentCount();
			for (int i=0; i<segmentCount; i++) {
				incremental.writeSegment(book.getSegmentAt(i));
			}
		} finally {
			incremental.close();
		}
	}

	@Override
	public IncrementalBookWriter writeBookIncremental(StandardBook book, File file) throws IOException {
		try {
			return new SQLiteBookWriterIncremental(file, book.getChannelCount(), book.getSamplingFrequency());
		} catch (SQLException ex) {
			throw new IOException("could not write SQLite book", ex);
		}
	}
}
