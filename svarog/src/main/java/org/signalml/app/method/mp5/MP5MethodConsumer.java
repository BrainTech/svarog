/* MP5MethodConsumer.java created 2007-10-28
 *
 */

package org.signalml.app.method.mp5;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodResultConsumer;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.book.BookBuilder;
import org.signalml.domain.book.BookFormatException;
import org.signalml.domain.book.DefaultBookBuilder;
import org.signalml.domain.book.SQLiteBookBuilder;
import org.signalml.domain.book.StandardBook;
import org.signalml.method.Method;
import org.signalml.method.mp5.MP5Data;
import org.signalml.method.mp5.MP5Result;
import org.signalml.plugin.export.SignalMLException;

/** MP5MethodConsumer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5MethodConsumer implements InitializingMethodResultConsumer {

	protected static final Logger logger = Logger.getLogger(MP5MethodConsumer.class);

	private MP5ResultDialog resultDialog;

	private DocumentFlowIntegrator documentFlowIntegrator;
	private Window dialogParent;

	@Override
	public void initialize(ApplicationMethodManager manager) {

		dialogParent = manager.getDialogParent();
		documentFlowIntegrator = manager.getDocumentFlowIntegrator();

		resultDialog = new MP5ResultDialog(dialogParent, true);
		resultDialog.setFileChooser(manager.getFileChooser());

	}

	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {

		String bookFilePath = null;

		MP5Result result = (MP5Result) methodResult;
		if (result != null) {
			bookFilePath = result.getBookFilePath();
		}

		if (bookFilePath == null) {
			// for deserialized tasks
			MP5Data data = (MP5Data) methodData;
			if (data != null) {
				bookFilePath = data.getBookFilePath();
			}
		}

		if (bookFilePath == null) {
			throw new SignalMLException(_("No result book"));
		}

		MP5ResultTargetDescriptor descriptor = new MP5ResultTargetDescriptor();
		descriptor.setOpenInWindow(true);
		descriptor.setSaveToFile(false);

		boolean dialogOk = resultDialog.showDialog(descriptor, true);
		if (!dialogOk) {
			return false;
		}

		if (descriptor.isSaveToFile()) {

			try {
				BookBuilder tempBuilder = DefaultBookBuilder.getInstance();
				StandardBook book = tempBuilder.readBook(new File(bookFilePath));
				BookBuilder sqliteBuilder = new SQLiteBookBuilder();
				sqliteBuilder.writeBookComplete(book, descriptor.getBookFile());
			} catch (IOException|BookFormatException ex) {
				logger.error("Failed to export file [" + bookFilePath + " to " + descriptor.getBookFile() + "]", ex);
				throw new SignalMLException(ex);
			}

		}

		if (descriptor.isOpenInWindow()) {
			OpenDocumentDescriptor odd = new OpenDocumentDescriptor();
			odd.setMakeActive(true);
			odd.setFile(new File(bookFilePath));
			odd.setMakeActive(true);
			odd.setType(ManagedDocumentType.BOOK);

			if (documentFlowIntegrator.maybeOpenDocument(odd) == null)
				return false;
		}

		return true;

	}

}
