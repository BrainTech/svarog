/* DocumentManagerListener.java created 2007-09-21
 *
 */

package org.signalml.app.document;

import java.util.EventListener;
import org.signalml.plugin.export.signal.Document;

/**
 * Interface for a listener on changes that occur in a {@link DocumentManager}.
 * These changes include:
 * <ul>
 * <li>addition of a {@link Document document},</li>
 * <li>removal of a document,</li>
 * <li>the change of the file with which the document is
 * {@link FileBackedDocument backed}.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface DocumentManagerListener extends EventListener {

	/**
	 * Invoked when a document is added to the {@link DocumentManager manager}.
	 * @param e the {@link DocumentManagerEvent event} with the parameters of
	 * the change
	 */
	void documentAdded(DocumentManagerEvent e);

	/**
	 * Invoked when a document is removed from the {@link DocumentManager manager}.
	 * @param e the {@link DocumentManagerEvent event} with the parameters of
	 * the change
	 */
	void documentRemoved(DocumentManagerEvent e);

	/**
	 * Invoked when a path to a {@link Document document} is changed.
	 * @param e the {@link DocumentManagerEvent event} with the parameters of
	 * the change
	 */
	void documentPathChanged(DocumentManagerEvent e);

}
