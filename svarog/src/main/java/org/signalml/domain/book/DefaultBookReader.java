package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;
import pl.edu.fuw.MP.MPBookStore;

/**
 * BookReader implementation for legacy book files.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultBookReader implements BookReader {

	private static DefaultBookReader sharedInstance = null;

	protected DefaultBookReader() {
	}

	public static DefaultBookReader getInstance() {
		if (sharedInstance == null) {
			synchronized (DefaultBookReader.class) {
				if (sharedInstance == null)
					sharedInstance = new DefaultBookReader();
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

}
