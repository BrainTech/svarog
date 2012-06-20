/* Document.java created 2007-09-10
 *
 */
package org.signalml.plugin.export.signal;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

import org.signalml.app.document.FileBackedDocument;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.DocumentView;

/**
 * Interface for a document.
 * Allows to:
 * <ul>
 * <li>return the name of this document,</li>
 * <li>open and close a document,</li>
 * <li>add, remove and get dependent documents,</li>
 * <li>get and set associated view,</li>
 * <li>add and remove property listeners.</li>
 * </ul>
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface Document {

	String DEPENDENT_DOCUMENTS_PROPERTY = "dependentDocuments";
	String DOCUMENT_VIEW_PROPERTY = "documentView";

	/**
	 * Returns the name of this document.
	 * @return the name of this document
	 */
	String getName();

	/**
	 * If this document has a {@link FileBackedDocument#getBackingFile()
	 * backing file} reads this document from this file.
	 * If there is no such file TODO
	 * @throws SignalMLException if backing file doesn't exist and
	 * additional situations dependent on an implementation
	 * @throws IOException if I/O error occurs while reading data from file
	 */
	void openDocument() throws SignalMLException, IOException;
	/**
	 * Closes the document and depending data.
	 * @throws SignalMLException depends on an implementation.
	 */
	void closeDocument() throws SignalMLException;
	/**
	 * @return true if this document is closed, false otherwise
	 */
	boolean isClosed();

	/**
	 * @return true if this document has some dependent documents
	 * (for example {@link ExportedSignalDocument signal document} has dependent
	 * {@link ExportedTagDocument tag documents}),
	 * false otherwise.
	 */
	boolean hasDependentDocuments();
	/**
	 * Returns the list of dependent documents.
	 * @return the list of dependent documents
	 */
	List<Document> getDependentDocuments();
	/**
	 * Adds a dependent document.
	 * @param document a dependent document.
	 */
	void addDependentDocument(Document document);
	/**
	 * Removes a dependent document.
	 * If the provided document is not dependent from this document
	 * no action is taken
	 * @param document a dependent document.
	 */
	void removeDependentDocument(Document document);

	/**
	 * Returns the {@link DocumentView view} associated with this document.
	 * @return the view associated with this document
	 */
	DocumentView getDocumentView();
	/**
	 * Sets the {@link DocumentView view} to be associated with this document.
	 * @param documentView the {@link DocumentView view} to be associated
	 * with this document
	 */
	void setDocumentView(DocumentView documentView);

	/**
	 * Adds a listener for property changes.
	 * Listener should contain the name of property for changes on which
	 * it will be listening on.
	 * Default properties: DEPENDENT_DOCUMENTS_PROPERTY, DOCUMENT_VIEW_PROPERTY.
	 * There can be more properties specified by implementation/sub-interface.
	 * @param listener the listener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Removes a given listener.
	 * If <code>listener</code> was added more than once, it will be notified one
	 * less time after being removed.
	 * If {@code listener} is {@code null} or it was not added no action is taken.
	 * @param listener the listener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Returns an array containing all {@link PropertyChangeListener property change listeners}
	 * added to this document.
	 *
	 * @return an array of {@link PropertyChangeListener property change listeners} added to this
	 * document.
	 */
	public PropertyChangeListener[] getPropertyChangeListeners();

	/**
	 * Informs the document if its becoming active.
	 * @param active true if the document is becoming an active document
	 * (that means it will be currently displayed) or not.
	 */
	public void setActive(boolean active);

}
