/* OpenBookDescriptor.java created 2008-02-23
 * 
 */

package org.signalml.app.model;

import org.signalml.app.document.BookDocument;

/** OpenBookDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenBookDescriptor {

	private BookDocument existingDocument;

	public BookDocument getExistingDocument() {
		return existingDocument;
	}

	public void setExistingDocument(BookDocument existingDocument) {
		this.existingDocument = existingDocument;
	}
		
}
