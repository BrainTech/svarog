/* DocumentManager.java created 2007-09-10
 *
 */
package org.signalml.app.document;

import java.io.File;
import java.util.Iterator;

import org.signalml.plugin.export.signal.Document;

/** DocumentManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface DocumentManager {

	boolean isAllSaved();

	int getDocumentCount();
	Document getDocumentAt(int index);
	int getIndexOfDocument(Document document);
	Iterator<Document> iterator();
	Document getDocumentByFile(File file);

	void addDocument(Document document);

	void removeDocument(Document document);
	void removeDocumentAt(int index);

	void onDocumentPathChange(Document document, File oldFile, File newFile);

	int getDocumentCount(ManagedDocumentType type);
	Document getDocumentAt(ManagedDocumentType type, int index);
	int getIndexOfDocument(ManagedDocumentType type, Document document);

	void addDocumentManagerListener(DocumentManagerListener listener);
	void removeDocumentManagerListener(DocumentManagerListener listener);
}
