/**
 * 
 */
package org.signalml.plugin.impl.change;

import org.signalml.plugin.export.change.SvarogDocumentViewEvent;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.DocumentView;

/**
 * Implementation of {@link SvarogDocumentViewEvent}.
 * Contains the {@link Document} and the old value of the
 * {@link DocumentView view} for it.  
 * @author Marcin Szumski
 */
public class DocumentViewEventImpl extends DocumentEventImpl implements SvarogDocumentViewEvent {

	/**
	 * the old value of the view for the document
	 */
	protected DocumentView view;
	
	/**
	 * Constructor. Sets the {@link Document} and the old value of the
	 * {@link DocumentView view} for it.
	 * @param document the document to set
	 * @param documentView the view to set
	 */
	public DocumentViewEventImpl(Document document, DocumentView documentView) {
		super(document);
		view = documentView;
	}	
	
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.change.DocumentViewEvent#getView()
	 */
	@Override
	public DocumentView getView() {
		return view;
	}

}
