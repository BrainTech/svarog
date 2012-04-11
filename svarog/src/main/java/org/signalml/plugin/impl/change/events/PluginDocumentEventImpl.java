/**
 * 
 */
package org.signalml.plugin.impl.change.events;

import org.signalml.plugin.export.change.events.PluginDocumentEvent;
import org.signalml.plugin.export.signal.Document;

/**
 * Implementation of {@link PluginDocumentEvent}.
 * Contains a {@link Document}.
 * @author Marcin Szumski
 */
public class PluginDocumentEventImpl implements PluginDocumentEvent {

	/**
	 * the {@link Document} associated with this event 
	 */
	protected Document document;
	
	/**
	 * Constructor. Sets the {@link Document}.
	 * @param document the document to set
	 */
	public PluginDocumentEventImpl(Document document){
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
