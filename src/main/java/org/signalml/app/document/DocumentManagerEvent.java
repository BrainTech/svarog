/* DocumentManagerEvent.java created 2007-09-21
 * 
 */

package org.signalml.app.document;

import java.util.EventObject;

/** DocumentManagerEvent
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DocumentManagerEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private Document document;
	private int index;
	private int inTypeIndex;
	
	public DocumentManagerEvent(DocumentManager source, Document document, int index, int inTypeIndex) {
		super(source);
		this.document = document;
		this.index = index;
		this.inTypeIndex = inTypeIndex;
	}
	
	public DocumentManager getDocumentManager() {
		return (DocumentManager) source;
	}
	
	public Document getDocument() {
		return document;
	}
	
	public int getIndex() {
		return index;
	}

	public int getInTypeIndex() {
		return inTypeIndex;
	}
		
}
