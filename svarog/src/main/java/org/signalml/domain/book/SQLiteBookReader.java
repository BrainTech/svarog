package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * BookReader implementation for SQLite book files.
 *
 * @author ptr@mimuw.edu.pl
 */
public class SQLiteBookReader implements BookReader {

	public SQLiteBookReader() {
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
}
