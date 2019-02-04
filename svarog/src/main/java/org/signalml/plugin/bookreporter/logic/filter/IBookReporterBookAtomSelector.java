package org.signalml.plugin.bookreporter.logic.filter;

import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;

/**
 * @author piotr@develancer.pl
 * (based on INewStagerBookAtomSelector)
 */
public interface IBookReporterBookAtomSelector {

	public boolean matches(BookReporterAtom atom);

}
