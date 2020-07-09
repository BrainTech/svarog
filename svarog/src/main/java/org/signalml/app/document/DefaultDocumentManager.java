/* DefaultDocumentManager.java created 2007-09-10
 *
 */
package org.signalml.app.document;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import org.apache.log4j.Logger;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.signal.Document;

/**
 * Implementation of {@link DocumentManager}.
 * Each {@link Document document} is stored in three collections:
 * <ul>
 * <li>the vector with all documents,</li>
 * <li>the vector containing documents of a specified {@link ManagedDocumentType
 * type},</li>
 * <li>the map associating files with the documents backed with these files.
 * </li>
 * </ul>
 * Contains {@link DocumentManagerListener listeners} and informs them about
 * changes.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultDocumentManager implements DocumentManager {

	protected static final Logger logger = Logger.getLogger(DefaultDocumentManager.class);

	/**
	 * the vector containing all {@link Document documents} in this manager
	 */
	private Vector<Document> documents = new Vector<>(100,100);

	/**
	 * the map associating files with the {@link Document documents} backed
	 * with them
	 */
	private Map<File,Document> documentsByFile = new HashMap<>(100);

	/**
	 * the map associating {@link ManagedDocumentType types} of {@link Document
	 * documents} with vectors of {@link Document documents} of these types
	 */
	private Map<ManagedDocumentType,Vector<Document>> documentVectorsByType = new HashMap<>(10);

	/**
	 * the list of {@link DocumentManagerListener listeners}
	 */
	private EventListenerList listenerList = new EventListenerList();

	@Override
	public int getDocumentCount() {
		synchronized (this) {
			return documents.size();
		}
	}

	@Override
	public Document getDocumentAt(int index) {
		synchronized (this) {
			return documents.elementAt(index);
		}
	}

	@Override
	public int getIndexOfDocument(Document document) {
		synchronized (this) {
			return documents.indexOf(document);
		}
	}

	@Override
	public Iterator<Document> iterator() {
		synchronized (this) {
			return documents.iterator();
		}
	}

	@Override
	public Document getDocumentByFile(File file) {
		synchronized (this) {
			return documentsByFile.get(file);
		}
	}

	/**
	 * Adds a {@link Document document} to this manager.
	 * In order to do it:
	 * <ul>
	 * <li>if the document {@link FileBackedDocument has a backing file} it is
	 * added to the map associating files with the documents backed with them
	 * </li>
	 * <li>adds the document to the vector of all documents,</li>
	 * <li>adds the document to the vector of documents of a specified
	 * {@link ManagedDocumentType type}.</li>
	 * </ul>
	 */
	@Override
	public void addDocument(Document document) {

		synchronized (this) {
			if (documents.contains(document)) {
				logger.info("Document already in the manager");
				return;
			}

			if (document instanceof FileBackedDocument) {
				FileBackedDocument fbd = (FileBackedDocument) document;
				File file = fbd.getBackingFile();
				if (file != null) {
					File absFile = file.getAbsoluteFile();
					if (documentsByFile.containsKey(absFile)) {
						throw new SanityCheckException("Sanity check failed, the same path already open");
					}

					documentsByFile.put(absFile, document);
				}
			}

			documents.add(document);

			ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
			int inTypeIndex = -1;
			if (type != null) {
				Vector<Document> vector = documentVectorsByType.get(type);
				if (vector == null) {
					vector = new Vector<>(100);
					documentVectorsByType.put(type, vector);
				}
				vector.add(document);
				inTypeIndex = vector.indexOf(document);
			}

			fireDocumentAdded(document, documents.indexOf(document), inTypeIndex);

		}

	}

	@Override
	public void removeDocumentAt(int index) {

		synchronized (this) {
			removeDocumentInternal(documents.elementAt(index));
		}

	}

	@Override
	public void removeDocument(Document document) {

		synchronized (this) {
			if (!documents.contains(document)) {
				return;
			}

			removeDocumentInternal(document);
		}

	}

	/**
	 * Removes a {@link Document document} from this manager:
	 * <ul>
	 * <li>if the document {@link FileBackedDocument has a backing file}
	 * it is removed form the map associating files with the documents backed
	 * with them,</li>
	 * <li>removes the document from the vector of documents of a specified
	 * {@link ManagedDocumentType type},</li>
	 * <li>removes the document from a vector of all documents in this manager.
	 * </li>
	 * </ul>
	 * @param document the document to be removed
	 */
	private void removeDocumentInternal(Document document) {

		if (document instanceof FileBackedDocument) {
			FileBackedDocument fbd = (FileBackedDocument) document;
			File file = fbd.getBackingFile();
			if (file != null) {
				documentsByFile.remove(file.getAbsoluteFile());
			}
		}

		int index = documents.indexOf(document);

		ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
		int inTypeIndex = -1;
		if (type != null) {
			Vector<Document> vector = documentVectorsByType.get(type);
			if (vector != null) {
				inTypeIndex = vector.indexOf(document);
				vector.remove(document);
			}
		}

		documents.remove(document);


		fireDocumentRemoved(document, index, inTypeIndex);

	}


	@Override
	public void onDocumentPathChange(Document document, File oldFile, File newFile) {

		synchronized (this) {

			if (!documents.contains(document)) {
				return;
			}

			if (oldFile != null) {
				documentsByFile.remove(oldFile.getAbsoluteFile());
			}

			if (newFile != null) {
				documentsByFile.put(newFile.getAbsoluteFile(), document);
			}

			ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
			int inTypeIndex = -1;
			if (type != null) {
				Vector<Document> vector = documentVectorsByType.get(type);
				if (vector != null) {
					inTypeIndex = vector.indexOf(document);
				}
			}

			fireDocumentPathChanged(document, documents.indexOf(document), inTypeIndex);

		}

	}

	@Override
	public int getDocumentCount(ManagedDocumentType type) {

		synchronized (this) {
			Vector<Document> vector = documentVectorsByType.get(type);
			if (vector != null) {
				return vector.size();
			}
		}

		return 0;

	}

	@Override
	public Document getDocumentAt(ManagedDocumentType type, int index) {

		synchronized (this) {
			Vector<Document> vector = documentVectorsByType.get(type);
			if (vector != null) {
				return vector.elementAt(index);
			}
		}

		return null;

	}
	@Override
	public Iterator<Document> iterator(ManagedDocumentType type) {
		synchronized (this) {
			Vector<Document> vector = documentVectorsByType.getOrDefault(type,new Vector<>());
			return vector.iterator();
		}
	}
	
	@Override
	public int getIndexOfDocument(ManagedDocumentType type, Document document) {

		synchronized (this) {
			Vector<Document> vector = documentVectorsByType.get(type);
			if (vector != null) {
				return vector.indexOf(document);
			}
		}

		return -1;

	}

	/**
	 * Informs all {@link DocumentManagerListener listeners} that the
	 * {@link Document document} was added.
	 * @param document the added document
	 * @param index the index of the document in the
	 * collection of all documents in this manager
	 * @param inTypeIndex the index of the document in the
	 * collection of documents of a specified {@link ManagedDocumentType type}
	 */
	protected void fireDocumentAdded(Document document, int index, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		DocumentManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==DocumentManagerListener.class) {
				if (e == null) {
					e = new DocumentManagerEvent(this,document,index,inTypeIndex);
				}
				((DocumentManagerListener)listeners[i+1]).documentAdded(e);
			}
		}
	}

	/**
	 * Informs all {@link DocumentManagerListener listeners} that the
	 * {@link Document document} was removed.
	 * @param document the removed document
	 * @param index the index of the document in the
	 * collection of all documents in this manager
	 * @param inTypeIndex the index of the document in the
	 * collection of documents of a specified {@link ManagedDocumentType type}
	 */
	protected void fireDocumentRemoved(Document document, int index, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		DocumentManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==DocumentManagerListener.class) {
				if (e == null) {
					e = new DocumentManagerEvent(this,document,index,inTypeIndex);
				}
				((DocumentManagerListener)listeners[i+1]).documentRemoved(e);
			}
		}
	}

	/**
	 * Informs all {@link DocumentManagerListener listeners} that the path to
	 * the {@link Document document} has changed.
	 * @param document the document with the changed path
	 * @param index the index of the document in the
	 * collection of all documents in this manager
	 * @param inTypeIndex the index of the document in the
	 * collection of documents of a specified {@link ManagedDocumentType type}
	 */
	protected void fireDocumentPathChanged(Document document, int index, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		DocumentManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==DocumentManagerListener.class) {
				if (e == null) {
					e = new DocumentManagerEvent(this,document,index,inTypeIndex);
				}
				((DocumentManagerListener)listeners[i+1]).documentPathChanged(e);
			}
		}
	}

	@Override
	public void addDocumentManagerListener(DocumentManagerListener listener) {
		synchronized (this) {
			listenerList.add(DocumentManagerListener.class, listener);
		}
	}

	@Override
	public void removeDocumentManagerListener(DocumentManagerListener listener) {
		synchronized (this) {
			listenerList.remove(DocumentManagerListener.class, listener);
		}
	}

}
