/* MutableDocument.java created 2007-09-20
 *
 */

package org.signalml.app.document;

import java.io.IOException;

import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;

/**
 * Interface for a document that can be saved.
 * Allows to:
 * <ul>
 * <li>check if it is saved,</li>
 * <li>save it,</li>
 * <li>(re-)initialize it.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MutableDocument extends Document {

	/**
	 * Returns true if this document is saved or false otherwise.
	 * @return true if this document is saved or false otherwise
	 */
	boolean isSaved();
	
	/**
	 * Sets if this document is saved.
	 * @param saved true if this document is saved or false otherwise
	 */
	void setSaved(boolean saved);

	/**
	 * (Re)Initializes this document as a new document.
	 * @throws SignalMLException never thrown in implementations
	 */
	void newDocument() throws SignalMLException;
	
	/**
	 * Saves this document.
	 * Document may be saved to file or saving may be performed only virtually.
	 * @throws SignalMLException if the backing file is required but there is non
	 * @throws IOException if I/O error occurs while writing document to file
	 */
	void saveDocument() throws SignalMLException, IOException;

}
