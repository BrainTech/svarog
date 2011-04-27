package org.signalml.domain.book;

import org.signalml.domain.book.StandardBookWriter;

import pl.edu.fuw.MP.Core.BookLibraryV5;
import pl.edu.fuw.MP.Core.BookLibraryV5Writer;
import pl.edu.fuw.MP.Core.SegmentHeaderV5;

public class StandardBookSegmentWriterImpl extends SegmentHeaderV5 {

	public StandardBookSegmentWriterImpl(BookLibraryV5Writer lib) {
		super(new BookLibraryV5(lib));
	}

	public StandardBookSegmentWriterImpl(StandardBookWriter book) {
		BookLibraryV5 lib=new BookLibraryV5();
		lib.setFields(((BookLibraryV5Writer)book).getFields());
		this.parent=lib;
	}

}
