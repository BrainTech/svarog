/**
 * 
 */
package org.signalml.plugin.impl.change;

import org.signalml.plugin.export.change.SvarogDocumentEvent;
import org.signalml.plugin.export.signal.Document;

/**
 * Implementation of {@link SvarogDocumentEvent}.
 * Contains a {@link Document}.
 * @author Marcin Szumski
 */
public class DocumentEventImpl implements SvarogDocumentEvent {

	/**
	 * the {@link Document} associated with this event 
	 */
	protected Document document;
	
	/**
	 * Constructor. Sets the {@link Document}.
	 * @param document the document to set
	 */
	public DocumentEventImpl(Document document){
		this.document = document;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.DocumentEvent#getDocument()
	 */
	@Override
	public Document getDocument() {
		return document;
	}

}
