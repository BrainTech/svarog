/* ExportBookWorker.java created 2008-01-27
 *
 */

package org.signalml.app.worker.document;

import java.io.File;
import java.util.List;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.domain.book.IncrementalBookWriter;
import org.signalml.domain.book.SQLiteBook;
import org.signalml.domain.book.SQLiteBookBuilder;
import org.signalml.domain.book.StandardBook;
import org.signalml.domain.book.StandardBookSegment;

/** ExportBookWorker
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExportBookWorker extends SwingWorker<Void,Integer> {

	protected static final Logger logger = Logger.getLogger(ExportBookWorker.class);

	private File bookFile;
	private StandardBook book;

	private PleaseWaitDialog pleaseWaitDialog;

	public ExportBookWorker(StandardBook book, File bookFile, PleaseWaitDialog pleaseWaitDialog) {
		this.book = book;
		this.bookFile = bookFile;
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	@Override
	protected Void doInBackground() throws Exception {

		final int segmentCount = book.getSegmentCount();
		if (book instanceof SQLiteBook) {
			publish(0);
			((SQLiteBook) book).createCopy(bookFile);
			publish(segmentCount);
		} else {
			SQLiteBookBuilder bookBuilder = new SQLiteBookBuilder();

			IncrementalBookWriter incrementalBookWriter = bookBuilder.writeBookIncremental(book, bookFile);
			for (int i=0; i<segmentCount; i++) {
				StandardBookSegment[] segments = book.getSegmentAt(i);
				incrementalBookWriter.writeSegment(segments);
				publish(i+1);
			}
			incrementalBookWriter.close();
		}

		return null;
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		synchronized (pleaseWaitDialog) {
			return pleaseWaitDialog;
		}
	}

	@Override
	protected void done() {
		if (pleaseWaitDialog != null) {
			pleaseWaitDialog.releaseIfOwnedBy(this);
		}
	}

	@Override
	protected void process(List<Integer> chunks) {
		if (pleaseWaitDialog != null && !chunks.isEmpty()) {
			synchronized (pleaseWaitDialog) {
				pleaseWaitDialog.setProgress((int) chunks.get(0));
			}
		}
	}

}
