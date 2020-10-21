package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;

/**
 * Interface for writing book files.
 */
public interface BookWriter {

	/**
	 * Should write the given book to disk (all header information and all segments
	 * as specified in the book).
	 *
	 * (this function is pretty redundant vs. writeBookIncremetal, it might be removed from here
	 * and placed in an util class).
	 *
	 * @param book
	 * @param file
	 * @throws IOException
	 */
	void writeBookComplete(StandardBook book, File file) throws IOException;

	/**
	 * Should write header information from the book, but no segments. Should return
	 * an IncrementalBookWriter object which can be used to add segments to the file).
	 *
	 * @param book
	 * @param file
	 * @return object to write consecutive book segments
	 *
	 * @throws IOException
	 */
	IncrementalBookWriter writeBookIncremental(StandardBook book, File file) throws IOException;
}
