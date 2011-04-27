/* BookBuilder.java created 2008-02-16
 *
 */

package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;

/** BookBuilder
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface BookBuilder {


	/** Should create a new mutable book with given channel count.
	 ** XXXYYY added to test
	 *
	 *
	 */
	StandardBookWriter createBook();

	/** Should read the given file and return it. The read may be partial (i.e. only the headers)
	 *  if the returned StandardBook supports incremental reading via it's methods (in this case
	 *  StandardBook.close() should release the file). This method may return an instance
	 *  implementing MutableBook if possible, but is not required to do so.
	 *
	 * @param file
	 *
	 * @throws IOException when disk access fails
	 * @throws BookFormatException when file format is invalid
	 */
	StandardBook readBook(File file) throws IOException, BookFormatException;

	/** Should write the given book to disk (all header information and all segments
	 *  as specified in the book).
	 *
	 *  (this function is pretty redundant vs. writeBookIncremetal, it might be removed from here
	 *  and placed in an util class).
	 *
	 * @param book
	 * @param file
	 * @throws IOException
	 */
	void writeBookComplete(StandardBook book, File file) throws IOException;

	/** Should write header information from the book, but no segments. Should return
	 *  an IncrementalBookWriter object which can be used to add segments to the file).
	 *
	 * @param book
	 * @param file
	 *
	 * @throws IOException
	 */
	IncrementalBookWriter writeBookIncremental(StandardBook book, File file) throws IOException;

	IncrementalBookWriter writeBookIncremental(StandardBookWriter book, String file) throws IOException;

}
