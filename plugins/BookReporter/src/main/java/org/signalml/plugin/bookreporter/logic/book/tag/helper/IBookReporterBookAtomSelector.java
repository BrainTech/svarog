package org.signalml.plugin.bookreporter.logic.book.tag.helper;

import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;

/**
 * @author piotr@develancer.pl
 * (based on INewStagerBookAtomSelector)
 */
public interface IBookReporterBookAtomSelector {

	public boolean matches(BookReporterAtom atom);

}
