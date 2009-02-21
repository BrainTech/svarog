/* AbstractDocument.java created 2007-09-20
 * 
 */

package org.signalml.app.document;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;

import org.signalml.app.view.DocumentView;
import org.signalml.exception.SignalMLException;

/** AbstractDocument
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractDocument implements Document {

	protected List<Document> dependants = new LinkedList<Document>();
	protected DocumentView documentView;
	
	protected PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);
		
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
	public void addDependentDocument( Document document ) {
		if( !dependants.contains(document) ) {
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
	public void removeDependentDocument( Document document ) {
		int index = dependants.indexOf(document);
		if( index >= 0 ) {
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
		if( this.documentView != documentView ) {
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
	
}
