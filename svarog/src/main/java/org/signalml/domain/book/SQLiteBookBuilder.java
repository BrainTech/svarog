package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * BookBuilder implementation for SQLite book files.
 *
 * @author ptr@mimuw.edu.pl
 */
public class SQLiteBookBuilder implements BookBuilder {

	public SQLiteBookBuilder() {
		// nothing here
	}

	@Override
	public StandardBook readBook(File file) throws IOException, BookFormatException {
		try {
			return new SQLiteBook(file);
		} catch (SQLException ex) {
			throw new IOException("could not read SQLite book", ex);
		}
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
			return new SQLiteBookBuilderIncremental(file, book.getChannelCount(), book.getSamplingFrequency());
		} catch (SQLException ex) {
			throw new IOException("could not write SQLite book", ex);
		}
	}

	@Override
	public IncrementalBookWriter writeBookIncremental(StandardBookWriter book, String file) throws IOException {
		throw new UnsupportedOperationException("this variant of writeBookIncremental is not supported with SQLite writer");
	}

	@Override
	public StandardBookWriter createBook() {
		throw new UnsupportedOperationException("createBook is not supported with SQLite writer");
	}
}
