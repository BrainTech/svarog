/* ApplicationWorkspace.java created 2007-12-15
 *
 */

package org.signalml.app.config;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.util.XMLUtils;
import org.signalml.exception.SanityCheckException;
import org.signalml.exception.SignalMLException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/** ApplicationWorkspace
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("workspace")
public class ApplicationWorkspace extends AbstractXMLConfiguration {

	protected static final Logger logger = Logger.getLogger(ApplicationWorkspace.class);

	private LinkedList<WorkspaceDocument> documents = new LinkedList<WorkspaceDocument>();
	private WorkspaceDocument activeDocument;

	public ApplicationWorkspace() {
	}

	@Override
	public String getStandardFilename() {
		return "workspace.xml";
	}

	public void configureFrom(DocumentFlowIntegrator integrator) {

		DocumentManager documentManager = integrator.getDocumentManager();
		ActionFocusManager actionFocusManager = integrator.getActionFocusManager();

		int count = documentManager.getDocumentCount();
		WorkspaceDocument workspaceDocument;
		Document activeDocument = actionFocusManager.getActiveDocument();
		Document document;
		for (int i=0; i<count; i++) {
			document = documentManager.getDocumentAt(i);
			try {
				workspaceDocument = addDocument(document);
				if (document == activeDocument) {
					this.activeDocument = workspaceDocument;
				}
			} catch (Throwable t) {
				logger.error("Failed to add document: "+document, t);
			}
		}

	}

	public void configureIntegrator(DocumentFlowIntegrator integrator) {

		int cnt = documents.size();
		Document document;
		Document activeDocument = null;
		WorkspaceDocument workspaceDocument;

		for (int i=0; i<cnt; i++) {

			document = null;

			workspaceDocument = documents.get(i);

			try {
				document = restoreDocument(workspaceDocument, integrator);
			} catch (IOException ex) {
				logger.error("Exeption while restoring workspace", ex);
			} catch (SignalMLException ex) {
				logger.error("Exeption while restoring workspace", ex);
			}

			if (this.activeDocument == workspaceDocument) {
				activeDocument = document;
			}


		}

		if (activeDocument != null) {

			integrator.getActionFocusManager().setActiveDocument(activeDocument);

		}

	}

	public WorkspaceDocument addDocument(Document document) {

		ManagedDocumentType type;

		if (document instanceof FileBackedDocument) {

			type = ManagedDocumentType.getForClass(document.getClass());
			if (type == ManagedDocumentType.SIGNAL) {

				WorkspaceSignal signal = new WorkspaceSignal((SignalDocument) document);
				documents.add(signal);

				return signal;

			}
			else if (type == ManagedDocumentType.TAG) {
				// tags are added inside their signals
				return null;
			}
			else if (type == ManagedDocumentType.BOOK) {

				WorkspaceBook book = new WorkspaceBook((BookDocument) document);
				documents.add(book);

				return book;

			} else {
				throw new SanityCheckException("Bad document type [" + type + "]");
			}

		} else {

			// other types not saved into workspace - currently no such documents
			return null;

		}

	}

	public int getDocumentCount() {
		return documents.size();
	}

	public Document restoreDocument(WorkspaceDocument workspaceDocument, DocumentFlowIntegrator integrator) throws IOException, SignalMLException {

		if (workspaceDocument instanceof WorkspaceSignal) {

			Document document = integrator.openMRUDEntry(workspaceDocument.getMrudEntry());
			if (document == null || !(document instanceof SignalDocument)) {
				logger.warn("WARNING: not a signal");
				return null;
			}
			((WorkspaceSignal) workspaceDocument).configureSignal((SignalDocument) document, integrator);

			return document;

		}
		else if (workspaceDocument instanceof WorkspaceBook) {

			Document document = integrator.openMRUDEntry(workspaceDocument.getMrudEntry());
			if (document == null || !(document instanceof BookDocument)) {
				logger.warn("WARNING: not a book");
				return null;
			}
			((WorkspaceBook) workspaceDocument).configureBook((BookDocument) document);

			return document;

		}
		else if (workspaceDocument instanceof WorkspaceTag) {
			return null;
			// do nothing - tags restored inside signals
		} else {
			throw new SanityCheckException("Bad workspace document class [" + workspaceDocument.getClass() + "]");
		}

	}

	@Override
	public XStream getStreamer() {
		if (streamer == null) {
			streamer = createWorkspaceStreamer();
		}
		return streamer;
	}

	private XStream createWorkspaceStreamer() {
		XStream streamer = XMLUtils.getDefaultStreamer();
		XMLUtils.configureStreamerForMontage(streamer);
		XMLUtils.configureStreamerForBookFilter(streamer);
		Annotations.configureAliases(
		        streamer,
		        ApplicationWorkspace.class,
		        WorkspaceDocument.class,
		        WorkspaceSignal.class,
		        WorkspaceBook.class,
		        WorkspaceTag.class,
		        WorkspaceSignalPlot.class,
		        WorkspaceBookPlot.class
		);
		streamer.setMode(XStream.ID_REFERENCES);

		return streamer;
	}

}
