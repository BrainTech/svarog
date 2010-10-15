/* SignalTreeModel.java created 2007-09-11
 *
 */
package org.signalml.app.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.DocumentManagerEvent;
import org.signalml.app.document.DocumentManagerListener;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;

/** SignalTreeModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalTreeModel extends AbstractTreeModel implements DocumentManagerListener, PropertyChangeListener {

	protected static final Logger logger = Logger.getLogger(SignalTreeModel.class);

	private static final String ROOT_NODE = "signalTree.root";

	private DocumentManager documentManager;

	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent == ROOT_NODE) {
			return documentManager.getDocumentAt(ManagedDocumentType.SIGNAL, index);
		}
		else if (parent instanceof SignalDocument) {
			SignalDocument signalDocument = (SignalDocument) parent;
			float pageSize = signalDocument.getPageSize();
			SignalPageTreeNode node = new SignalPageTreeNode(
			        index+1,
			        pageSize,
			        index * pageSize,
			        (index+1) * pageSize
			);
			return node;
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent == ROOT_NODE) {
			return documentManager.getDocumentCount(ManagedDocumentType.SIGNAL);
		}
		else if (parent instanceof SignalDocument) {
			return ((SignalDocument) parent).getPageCount();
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == ROOT_NODE && (child instanceof Document)) {
			return documentManager.getIndexOfDocument(ManagedDocumentType.SIGNAL, ((Document) child));
		}
		else if ((parent instanceof SignalDocument) && (child instanceof SignalPageTreeNode)) {
			return ((SignalPageTreeNode) child).getPage() - 1;
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return ROOT_NODE;
	}

	@Override
	public boolean isLeaf(Object node) {
		if (node == ROOT_NODE) {
			return false;
		}
		else if (node instanceof SignalDocument) {
			return false;
		}
		return true;
	}

	@Override
	public void documentAdded(DocumentManagerEvent e) {

		if (!(e.getDocument() instanceof SignalDocument) || e.getDocument() instanceof MonitorSignalDocument) {
			return;
		}
		SignalDocument signalDocument = (SignalDocument) e.getDocument();
		signalDocument.addPropertyChangeListener(this);

		fireTreeNodesInserted(this, new Object[] { ROOT_NODE }, new int[] { e.getInTypeIndex() }, new Object[] { signalDocument });

	}

	@Override
	public void documentRemoved(DocumentManagerEvent e) {
		if (!(e.getDocument() instanceof SignalDocument) || e.getDocument() instanceof MonitorSignalDocument) {
			return;
		}
		SignalDocument signalDocument = (SignalDocument) e.getDocument();
		signalDocument.removePropertyChangeListener(this);

		fireTreeNodesRemoved(this, new Object[] { ROOT_NODE }, new int[] { e.getInTypeIndex() }, new Object[] { signalDocument });
	}

	@Override
	public void documentPathChanged(DocumentManagerEvent e) {
		// ignored
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		if (source instanceof SignalDocument) {
			SignalDocument signalDocument = (SignalDocument) source;
			// react to all properties for now
			fireTreeStructureChanged(this, new Object[] { ROOT_NODE, signalDocument });
		}
	}

}
