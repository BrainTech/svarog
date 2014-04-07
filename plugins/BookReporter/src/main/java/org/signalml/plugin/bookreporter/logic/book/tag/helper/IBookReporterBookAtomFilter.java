package org.signalml.plugin.bookreporter.logic.book.tag.helper;

import java.util.Collection;
import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;

/**
 * @author piotr@develancer.pl
 * (based on INewStagerBookAtomFilter)
 */
public interface IBookReporterBookAtomFilter {
	public Collection<BookReporterAtom> filter(BookReporterAtom atoms[]);
}
