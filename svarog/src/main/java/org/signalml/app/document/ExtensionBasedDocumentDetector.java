/* ExtensionBasedDocumentDetector.java created 2007-09-18
 *
 */

package org.signalml.app.document;

import java.io.File;
import java.io.IOException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.util.Util;

/**
 * Implementation of {@link DocumentDetector}, which detects
 * {@link ManagedDocumentType types} of {@link Document documents} based
 * on their extensions:
 * <ul>
 * <li>{@code *.b} - {@link ManagedDocumentType#BOOK book} file,</li>
 * <li>{@code *.xml} or {@code *.tag} - {@link ManagedDocumentType#TAG tag}
 * file,</li>
 * <li>any other extension or no extension -
 * {@link ManagedDocumentType#SIGNAL signal} file.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExtensionBasedDocumentDetector implements DocumentDetector {

	/**
	 * Detects the {@link ManagedDocumentType type} of a {@link Document
	 * document} stored in the given file, based on the extension of the file:
	 * <ul>
	 * <li>{@code *.b} or {@code *.db} - {@link ManagedDocumentType#BOOK book} file,</li>
	 * <li>{@code *.xml} or {@code *.tag} - {@link ManagedDocumentType#TAG tag}
	 * file,</li>
	 * <li>any other extension of no extension -
	 * {@link ManagedDocumentType#SIGNAL signal} file.</li>
	 * </ul>
	 */
	@Override
	public ManagedDocumentType detectDocumentType(File file) throws IOException {
		String ext = Util.getFileExtension(file,false);
		if (ext == null) {
			// assume signal for extension-less files
			return ManagedDocumentType.SIGNAL;
		}
		if (ext.equalsIgnoreCase("B") || ext.equalsIgnoreCase("DB")) {
			return ManagedDocumentType.BOOK;
		}
		if (ext.equalsIgnoreCase("XML") || ext.equalsIgnoreCase("TAG")) {
			return ManagedDocumentType.TAG;
		}
		// assume signal for all other extensions
		return ManagedDocumentType.SIGNAL;
	}

}
