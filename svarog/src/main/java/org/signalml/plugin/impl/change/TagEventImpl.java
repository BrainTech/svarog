/**
 * 
 */
package org.signalml.plugin.impl.change;

import org.signalml.plugin.export.change.SvarogTagEvent;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;

/**
 * Implementation of {@link SvarogTagEvent}.
 * Contains the {@link ExportedTag tag} and a
 * {@link ExportedTagDocument document} in which the tag is/was located.
 * @author Marcin Szumski
 */
public class TagEventImpl implements SvarogTagEvent {

	/**
	 * the document in which the {@link #tag tag} is/was located
	 */
	protected ExportedTagDocument document;
	/**
	 * the {@link ExportedTag tag} associated with this event
	 */
	protected ExportedTag tag;
	
	/**
	 * Constructor. Sets the {@link ExportedTag tag} and
	 * {@link ExportedTagDocument document} in which the tag is located.
	 * @param tag the tag to set
	 * @param document the document in which the tag is located
	 */
	public TagEventImpl(ExportedTag tag, ExportedTagDocument document){
		this.tag = tag;
		this.document = document;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.TagEvent#getDocument()
	 */
	@Override
	public ExportedTagDocument getDocument() {
		return document;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.TagEvent#getTag()
	 */
	@Override
	public ExportedTag getTag() {
		return tag;
	}

}
