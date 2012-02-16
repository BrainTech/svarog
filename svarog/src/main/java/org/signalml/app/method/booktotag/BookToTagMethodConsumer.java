/* BookToTagMethodConsumer.java created 2007-10-23
 *
 */

package org.signalml.app.method.booktotag;

import java.awt.Window;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodResultConsumer;
import org.signalml.app.view.components.dialogs.OptionPane;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.method.Method;
import org.signalml.method.booktotag.BookToTagResult;
import org.signalml.plugin.export.SignalMLException;

/** BookToTagMethodConsumer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookToTagMethodConsumer implements InitializingMethodResultConsumer {

	protected static final Logger logger = Logger.getLogger(BookToTagMethodConsumer.class);

	private Window dialogParent;
	private ViewerFileChooser fileChooser;

	@Override
	public void initialize(ApplicationMethodManager manager) {
		dialogParent = manager.getDialogParent();
		fileChooser = manager.getFileChooser();

	}

	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {

		BookToTagResult result = (BookToTagResult) methodResult;

		TagDocument tagDocument = new TagDocument(result.getTagSet());

		boolean hasFile = false;
		File saveFile;

		do {

			saveFile = fileChooser.chooseSaveTag(dialogParent);
			if (saveFile == null) {
				// file choice canceled
				break;
			}

			hasFile = true;

			// file exists warning
			if (saveFile.exists()) {
				int res = OptionPane.showFileAlreadyExists(dialogParent);
				if (res != OptionPane.OK_OPTION) {
					hasFile = false;
				}
			}

		} while (!hasFile);

		if (hasFile) {

			tagDocument.setBackingFile(saveFile);
			try {
				tagDocument.saveDocument();
			} catch (SignalMLException ex) {
				logger.error("Failed to save document", ex);
				Dialogs.showExceptionDialog(dialogParent, ex);
				return false;
			} catch (IOException ex) {
				logger.error("Failed to save document - i/o exception", ex);
				Dialogs.showExceptionDialog(dialogParent, ex);
				return false;
			}

		}

		return true;

	}
}
