/* DocumentManager.java created 2007-09-10
 *
 */
package org.signalml.app.document;

import java.io.File;
import java.util.Iterator;
import org.signalml.plugin.export.signal.Document;

/**
 * Interface for manager of {@link Document documents} in Svarog.
 * Allows to:
 * <ul>
 * <li>add and remove documents,</li>
 * <li>get the number of all documents and of documents of a specified type,</li>
 * <li>get the document of a specified index in the collection of all documents
 * in this manager and in the collection of documents of a specified
 * {@link ManagedDocumentType type},</li>
 * <li>get the index of the document in the collection of all documents
 * in this manager and in the collection of documents of a specified
 * {@link ManagedDocumentType type},</li>
 * <li>add and remove {@link DocumentManagerListener listeners}.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface DocumentManager {

	/**
	 * Returns the number of all {@link Document documents} in this manager.
	 * @return the number of all documents in this manager
	 */
	int getDocumentCount();

	/**
	 * Returns the {@link Document document} of a specified index in the
	 * collection of all documents in this manager.
	 * @param index the index of a document
	 * @return the document of a specified index
	 */
	Document getDocumentAt(int index);

	/**
	 * Returns the index of a given {@link Document document} in the
	 * collection of all documents in this manager.
	 * @param document the document which index is to be found
	 * @return the index of a given document in the
	 * collection of all documents in this manager.
	 */
	int getIndexOfDocument(Document document);

	/**
	 * Returns the iterator over the collection of all {@link Document
	 * documents} in this manager.
	 * @return the iterator over the collection of all documents in this
	 * manager
	 */
	Iterator<Document> iterator();

	/**
	 * Returns the {@link Document document} which has a given file
	 * as a backing file.
	 * @param file the backing file
	 * @return the {@link Document document} which has a given file
	 * as a backing file
	 */
	Document getDocumentByFile(File file);

	/**
	 * Adds a {@link Document document} to this manager.
	 * @param document the document to add
	 */
	void addDocument(Document document);

	/**
	 * Removes a {@link Document document} from this manager.
	 * If there is no such document in this manager no action is taken.
	 * @param document the document to remove
	 */
	void removeDocument(Document document);

	/**
	 * Removes a {@link Document document} from this manager.
	 * @param index the index of the document
	 * @throws ArrayIndexOutOfBoundsException if there is no document
	 * of such index
	 */
	void removeDocumentAt(int index);

	/**
	 * Called when the backing file for the {@link Document document} changes.
	 * <p>Changes the stored file for the document.
	 * If the document is not in this manager no action is taken.
	 * @param document the document for which the path has changed
	 * @param oldFile the old file for this document
	 * @param newFile the new file for this document
	 */
	void onDocumentPathChange(Document document, File oldFile, File newFile);

	/**
	 * Returns the number of {@link Document documents} of a specified
	 * {@link ManagedDocumentType type}.
	 * @param type the type of a document
	 * @return the number of documents of a specified type
	 */
	int getDocumentCount(ManagedDocumentType type);

	/**
	 * Returns the {@link Document document} of a specified index in the
	 * collection of documents of a specified {@link ManagedDocumentType type}.
	 * @param type the type of a document
	 * @param index the index of a document
	 * @return the document of a specified index
	 */
	Document getDocumentAt(ManagedDocumentType type, int index);
	/**
	 * Returns the iterator over the collection of all {@link Document
	 * documents} of a specified {@link ManagedDocumentType type}.in this manager.
	 * @return the iterator over the collection of all documents of a specified type 
	 * in this manager
	 */
	Iterator<Document> iterator(ManagedDocumentType type);
	/**
	 * Returns the index of a given {@link Document document} in the
	 * collection of documents of a specified {@link ManagedDocumentType type}.
	 * @param type the type of a document
	 * @param document the document which index is to be found
	 * @return the index of a given document in the
	 * collection of documents of a specified type.
	 */
	int getIndexOfDocument(ManagedDocumentType type, Document document);

	/**
	 * Adds a {@link DocumentManagerListener listener} for changes in this
	 * manager (addition, removal and change of the path of the
	 * {@link Document document}).
	 * @param listener the listener to be added
	 */
	void addDocumentManagerListener(DocumentManagerListener listener);

	/**
	 * Removes a {@link DocumentManagerListener listener} for changes in this
	 * manager (addition, removal and change of the path of the
	 * {@link Document document}).
	 * @param listener the listener to be removed
	 */
	void removeDocumentManagerListener(DocumentManagerListener listener);
}
