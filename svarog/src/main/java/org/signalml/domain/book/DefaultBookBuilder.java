/* BookBuilderImpl.java created 2008-03-26
 *
 */

package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;

import pl.edu.fuw.MP.MPBookStore;
import pl.edu.fuw.MP.Core.BookLibraryV5Writer;

/** BookBuilderImpl
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultBookBuilder implements BookBuilder {

	private static DefaultBookBuilder sharedInstance = null;

	protected DefaultBookBuilder() {
	}

	public static DefaultBookBuilder getInstance() {
		if (sharedInstance == null) {
		    synchronized (DefaultBookBuilder.class) {
		        if (sharedInstance == null)
		            sharedInstance = new DefaultBookBuilder();
		    }
		}

		return sharedInstance;
	}

	@Override
	public StandardBook readBook(File file) throws IOException, BookFormatException {
		MPBookStore book = new MPBookStore();

		if (!book.Open(file.getAbsolutePath())) {
			return null;
		}
		return book;
	}

	@Override
	public void writeBookComplete(StandardBook book, File file) throws IOException {
		IncrementalBookWriter bookWriter = writeBookIncremental(book, file);
		int segmentCount = book.getSegmentCount();
		for (int i=0; i<segmentCount; i++) {
			bookWriter.writeSegment(book.getSegmentAt(i));
		}
		bookWriter.close();
	}

	@Override
	public IncrementalBookWriter writeBookIncremental(StandardBook book, File file) throws IOException {
		return new MPv5BookWriter(book, file);
	}

	@Override
	public IncrementalBookWriter writeBookIncremental(StandardBookWriter book, String file) throws IOException {
		BookLibraryV5Writer b=(BookLibraryV5Writer)book;
		b.Open(file);
		return b;
	}


	public StandardBookWriter createBook() {
		BookLibraryV5Writer book=new BookLibraryV5Writer();
		return book;
	}


}
