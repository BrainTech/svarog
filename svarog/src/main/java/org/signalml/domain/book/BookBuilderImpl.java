package org.signalml.domain.book;

import java.io.File;
import java.io.IOException;


import pl.edu.fuw.MP.MPBookStore;
import pl.edu.fuw.MP.Core.BookLibraryV5Writer;

public class BookBuilderImpl implements BookBuilder {

	public StandardBook readBook(String file) throws IOException, BookFormatException {
		MPBookStore book = new MPBookStore();

		if (!book.Open(file)) {
			return null;
		}
		return book;
	}

	public StandardBook readBook(File file) throws IOException, BookFormatException {
		return this.readBook(file.getAbsolutePath());
	}

	public IncrementalBookWriter writeBookIncremental(StandardBookWriter book, String file) throws IOException {
		BookLibraryV5Writer b = (BookLibraryV5Writer) book;
		b.Open(file);
		return b;
	}

	public IncrementalBookWriter writeBookIncremental(StandardBook book, File file) throws IOException {
		BookLibraryV5Writer b = (BookLibraryV5Writer) book;
		b.Open(file.getAbsolutePath());
		return b;
	}

	public StandardBookWriter createBook() {
		BookLibraryV5Writer book = new BookLibraryV5Writer();
		return book;
	}

	public void writeBookComplete(StandardBook book, File file) throws IOException {
		// FIXME don't know what to do with this
		throw new UnsupportedOperationException("Not implemented: BookBuilderImpl.writeBookComplete");
	}



}
