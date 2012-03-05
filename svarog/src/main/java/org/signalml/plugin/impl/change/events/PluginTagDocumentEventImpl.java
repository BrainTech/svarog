/**
 * 
 */
package org.signalml.plugin.impl.change.events;

import org.signalml.plugin.export.change.events.PluginTagDocumentEvent;
import org.signalml.plugin.export.signal.ExportedTagDocument;

/**
 * Implementation of {@link PluginTagDocumentEvent}.
 * Contains new and old value of the active {@link ExportedTagDocument
 * tag document}.
 * @author Marcin Szumski
 */
public class PluginTagDocumentEventImpl implements PluginTagDocumentEvent {

	/**
	 * the new value of the active {@link ExportedTagDocument tag document}
	 */
	protected ExportedTagDocument document;
	/**
	 * the old value of the active {@link ExportedTagDocument tag document}
	 */
	protected ExportedTagDocument oldDocument;
	
	/**
	 * Constructor. Sets new and old value the active
	 * {@link ExportedTagDocument tag document}.
	 * @param document the new value of the active tag document
	 * @param oldDocument the old value of the active tag document
	 */
	public PluginTagDocumentEventImpl(ExportedTagDocument document, ExportedTagDocument oldDocument){
		this.document = document;
		this.oldDocument = oldDocument;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.TagDocumentEvent#getTagDocument()
	 */
	@Override
	public ExportedTagDocument getTagDocument() {
		return document;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.TagDocumentEvent#getOldTagDocument()
	 */
	@Override
	public ExportedTagDocument getOldTagDocument() {
		return oldDocument;
	}
}
