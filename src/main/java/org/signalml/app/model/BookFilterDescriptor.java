/* BookFilterDescriptor.java created 2008-03-04
 *
 */

package org.signalml.app.model;

import org.signalml.app.document.BookDocument;

import org.signalml.domain.book.filter.AtomFilterChain;

/** BookFilterDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookFilterDescriptor {

	private AtomFilterChain chain;
	private BookDocument document;

	public BookFilterDescriptor(AtomFilterChain chain, BookDocument document) {
		this.chain = chain;
		this.document = document;
	}

	public AtomFilterChain getChain() {
		return chain;
	}

	public void setChain(AtomFilterChain chain) {
		this.chain = chain;
	}

	public BookDocument getDocument() {
		return document;
	}

	public void setDocument(BookDocument document) {
		this.document = document;
	}

}
