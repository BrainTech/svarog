/**
 *
 */
package org.signalml.plugin.impl.change.events;

import org.signalml.plugin.export.change.events.PluginDocumentViewEvent;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.DocumentView;

/**
 * Implementation of {@link PluginDocumentViewEvent}.
 * Contains the {@link Document} and the old value of the
 * {@link DocumentView view} for it (the new value can be obtained from
 * document).
 * @author Marcin Szumski
 */
public class PluginDocumentViewEventImpl extends PluginDocumentEventImpl implements PluginDocumentViewEvent {

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
	public PluginDocumentViewEventImpl(Document document, DocumentView documentView) {
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
