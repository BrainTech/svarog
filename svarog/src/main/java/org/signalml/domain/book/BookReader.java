package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;

/**
 * Interface for reading book files.
 */
public interface BookReader {

	/**
	 * Should read the given file and return it. The read may be partial (i.e. only the headers)
	 * if the returned StandardBook supports incremental reading via it's methods (in this case
	 * StandardBook.close() should release the file). This method may return an instance
	 * implementing MutableBook if possible, but is not required to do so.
	 *
	 * @param file
	 * @return book object
	 *
	 * @throws IOException when disk access fails
	 * @throws BookFormatException when file format is invalid
	 */
	StandardBook readBook(File file) throws IOException, BookFormatException;
}
