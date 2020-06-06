/* DocumentManagerEvent.java created 2007-09-21
 *
 */

package org.signalml.app.document;

import java.util.EventObject;
import org.signalml.plugin.export.signal.Document;

/**
 * The event associated with a change in a {@link DocumentManager}
 * (addition, removal of a {@link Document document} or a change of the
 * path of a document).
 * Contains 3 fields:
 * <ul>
 * <li>the document connected with the change,</li>
 * <li>the index of the document in the collection of all documents in the
 * manager,</li>
 * <li>the index of the document in the collection of documents of a specified
 * {@link ManagedDocumentType type}.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DocumentManagerEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link Document document} connected with the change
	 */
	private Document document;

	/**
	 * the index of the {@link Document document} in the collection of all
	 * documents in the manager
	 */
	private int index;

	/**
	 * the index of the {@link Document document} in the collection of
	 * documents of a specified {@link ManagedDocumentType type}
	 */
	private int inTypeIndex;


	/**
	 * Constructor. Sets all parameters of this event.
	 * @param source the manager in which this event occurred
	 * @param document the {@link Document document} connected with the change
	 * @param index the index of the document in the collection of all documents
	 * in the manager
	 * @param inTypeIndex the index of the document in the collection of
	 * documents of a specified {@link ManagedDocumentType type}
	 */
	public DocumentManagerEvent(DocumentManager source, Document document, int index, int inTypeIndex) {
		super(source);
		this.document = document;
		this.index = index;
		this.inTypeIndex = inTypeIndex;
	}

	/**
	 * Returns the {@link DocumentManager manager} in which this event occurred.
	 * @return the manager in which this event occurred
	 */
	public DocumentManager getDocumentManager() {
		return (DocumentManager) source;
	}

	/**
	 * Returns the {@link Document document} connected with the change.
	 * @return the document connected with the change
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Returns the index of the {@link Document document} in the collection
	 * of all documents in the manager.
	 * @return the index of the document in the collection of all
	 * documents in the manager
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the index of the {@link Document document} in the collection of
	 * documents of a specified {@link ManagedDocumentType type}.
	 * @return the index of the document in the collection of
	 * documents of a specified type
	 */
	public int getInTypeIndex() {
		return inTypeIndex;
	}

}
