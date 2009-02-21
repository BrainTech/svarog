/* Document.java created 2007-09-10
 * 
 */
package org.signalml.app.document;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

import org.signalml.app.view.DocumentView;
import org.signalml.exception.SignalMLException;

/** Document
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface Document {
	
	public static final String DEPENDENT_DOCUMENTS_PROPERTY = "dependentDocuments";	
	public static final String DOCUMENT_VIEW_PROPERTY = "documentView";	
	
	String getName();
	
	void openDocument() throws SignalMLException, IOException;
	void closeDocument() throws SignalMLException;
	boolean isClosed();
	
	boolean hasDependentDocuments();
	List<Document> getDependentDocuments();
	void addDependentDocument( Document document );
	void removeDependentDocument( Document document );
	
	DocumentView getDocumentView();
	void setDocumentView( DocumentView documentView );
	
	public void addPropertyChangeListener(PropertyChangeListener listener);
	public void removePropertyChangeListener(PropertyChangeListener listener);
	
}
