/* AbstractDocument.java created 2007-09-20
 *
 */

package org.signalml.plugin.export.signal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;

import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.DocumentView;

/**
 * Abstract implementation of a {@link Document} interface.
 * Contains necessary structures:
 * <ul>
 * <li>dependent documents - can be get, removed and added</li>
 * <li>a {@link DocumentView view} for this document - can be get and set</li>
 * <li>a support for property changes - allows to add and remove listeners</li>
 * </ul>
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractDocument implements Document {

	/**
	 * list of documents dependent from this one
	 */
	protected List<Document> dependants = new LinkedList<Document>();
	/**
	 * the view for this document
	 */
	protected DocumentView documentView;

	/**
	 * the support for changes in this document, used to inform listeners
	 * about changes
	 */
	protected PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

	/**
	 * true if this document is closed, false otherwise
	 */
	protected boolean closed = false;

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void closeDocument() throws SignalMLException {
		closed = true;
	}

	@Override
	public boolean hasDependentDocuments() {
		return !dependants.isEmpty();
	}

	@Override
	public List<Document> getDependentDocuments() {
		return dependants;
	}

	@Override
	public void addDependentDocument(Document document) {
		if (!dependants.contains(document)) {
			dependants.add(document);
			pcSupport.fireIndexedPropertyChange(
			        DEPENDENT_DOCUMENTS_PROPERTY,
			        dependants.indexOf(document),
			        null,
			        document
			);
		}
	}

	@Override
	public void removeDependentDocument(Document document) {
		int index = dependants.indexOf(document);
		if (index >= 0) {
			dependants.remove(document);
			pcSupport.fireIndexedPropertyChange(
			        DEPENDENT_DOCUMENTS_PROPERTY,
			        index,
			        document,
			        null
			);
		}
	}

	@Override
	public DocumentView getDocumentView() {
		return documentView;
	}

	@Override
	public void setDocumentView(DocumentView documentView) {
		if (this.documentView != documentView) {
			DocumentView oldDocumentView = this.documentView;
			this.documentView = documentView;
			pcSupport.firePropertyChange(DOCUMENT_VIEW_PROPERTY, oldDocumentView, documentView);
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Returns an array containing all {@link PropertyChangeListener
	 * PropertyChangeListeners} added to this document.
	 * @return an array containing all {@link PropertyChangeListener
	 * PropertyChangeListeners}.
	 */
	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return pcSupport.getPropertyChangeListeners();
	}
}
