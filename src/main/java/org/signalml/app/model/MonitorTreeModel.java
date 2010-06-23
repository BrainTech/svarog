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

/** MonitorTreeModel
 *
 */
public class MonitorTreeModel extends AbstractTreeModel implements DocumentManagerListener, PropertyChangeListener {

	protected static final Logger logger = Logger.getLogger(MonitorTreeModel.class);

	private static final String ROOT_NODE = "monitorTree.root";

	private DocumentManager documentManager;

	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if( parent == ROOT_NODE ) {
			return documentManager.getDocumentAt(ManagedDocumentType.MONITOR, index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if( parent == ROOT_NODE ) {
			return documentManager.getDocumentCount(ManagedDocumentType.MONITOR);
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if( parent == ROOT_NODE && ( child instanceof Document ) ) {
			return documentManager.getIndexOfDocument(ManagedDocumentType.MONITOR, ((Document) child));
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return ROOT_NODE;
	}

	@Override
	public boolean isLeaf(Object node) {
		if( node == ROOT_NODE ) {
			return false;
		}
		return true;
	}

	@Override
	public void documentAdded(DocumentManagerEvent e) {
		
		if( !( e.getDocument() instanceof MonitorSignalDocument ) ) {
			return;
		}
		MonitorSignalDocument monitorDocument = (MonitorSignalDocument) e.getDocument();
		monitorDocument.addPropertyChangeListener(this);
		
		fireTreeNodesInserted(this, new Object[] { ROOT_NODE }, new int[] { e.getInTypeIndex() }, new Object[] { monitorDocument } );
		
	}

	@Override
	public void documentRemoved(DocumentManagerEvent e) {
		if( !( e.getDocument() instanceof MonitorSignalDocument ) ) {
			return;
		}
		MonitorSignalDocument monitorDocument = (MonitorSignalDocument) e.getDocument();
		monitorDocument.removePropertyChangeListener(this);
		
		fireTreeNodesRemoved(this, new Object[] { ROOT_NODE }, new int[] { e.getInTypeIndex() }, new Object[] { monitorDocument } );
	}

	@Override
	public void documentPathChanged(DocumentManagerEvent e) {
		// path changes are not allowed		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		if( source instanceof MonitorSignalDocument ) {
			MonitorSignalDocument monitorDocument = (MonitorSignalDocument) source;
			// react to all properties for now
			fireTreeStructureChanged(this, new Object[] { ROOT_NODE, monitorDocument } );
		}		
	}

}
