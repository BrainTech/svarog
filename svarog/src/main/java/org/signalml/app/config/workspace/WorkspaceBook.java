/* WorkspaceBook.java created 2008-02-23
 *
 */

package org.signalml.app.config.workspace;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.mrud.MRUDEntry;
import org.signalml.app.view.book.BookPlot;
import org.signalml.app.view.book.BookView;
import org.signalml.domain.book.filter.AtomFilterChain;
import org.signalml.plugin.export.SignalMLException;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** WorkspaceBook
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("workspacebook")
public class WorkspaceBook extends WorkspaceDocument {

	protected static final Logger logger = Logger.getLogger(WorkspaceBook.class);

	private AtomFilterChain filterChain;

	private int currentSegment;
	private int currentChannel;

	private WorkspaceBookPlot plot;

	protected WorkspaceBook() {
		super();
	}

	public WorkspaceBook(BookDocument document) {

		MRUDEntry mrud = new MRUDEntry(ManagedDocumentType.BOOK, document.getClass(), document.getBackingFile().getAbsolutePath());
		mrud.setLastTimeOpened(new Date());

		mrudEntry = mrud;

		BookView view = (BookView) document.getDocumentView();
		BookPlot bookPlot = view.getPlot();

		filterChain = document.getFilterChain();

		currentSegment = view.getCurrentSegment();
		currentChannel = view.getCurrentChannel();

		plot = new WorkspaceBookPlot(bookPlot);

	}

	public void configureBook(BookDocument document) throws IOException, SignalMLException {

		BookView view = (BookView) document.getDocumentView();
		BookPlot bookPlot = view.getPlot();

		if (filterChain != null) {
			document.setFilterChain(filterChain);
		}

		view.setCurrentSegment(currentSegment);
		view.setCurrentChannel(currentChannel);

		plot.configurePlot(bookPlot);

	}

}
