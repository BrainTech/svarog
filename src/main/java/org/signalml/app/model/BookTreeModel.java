/* BookTreeModel.java created 2007-09-11
 * 
 */
package org.signalml.app.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.signalml.app.document.BookDocument;
import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.DocumentManagerEvent;
import org.signalml.app.document.DocumentManagerListener;
import org.signalml.app.document.ManagedDocumentType;

/** BookTreeModel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookTreeModel extends AbstractTreeModel implements DocumentManagerListener, PropertyChangeListener {

	// note - this doesn't implement book listeners - it is assumed that books
	// shown using this model do not changed
	
	private static final String ROOT_NODE = "bookTree.root";
	
	private DocumentManager documentManager;
	
	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	@Override
	public Object getRoot() {
		return ROOT_NODE;
	}
	
	@Override
	public int getChildCount(Object parent) {
		if( parent == ROOT_NODE ) {
			return documentManager.getDocumentCount(ManagedDocumentType.BOOK);
		}
		else if( parent instanceof BookDocument ) {
			return ((BookDocument) parent).getBook().getChannelCount();
		}
		else if( parent instanceof BookChannelTreeNode ) {
			return ((BookChannelTreeNode) parent).getBook().getSegmentCount();
		}
		else if( parent instanceof BookSegmentTreeNode ) {
			return ((BookSegmentTreeNode) parent).getAtomCount();
		}
		return 0;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if( parent == ROOT_NODE ) {
			return documentManager.getDocumentAt(ManagedDocumentType.BOOK, index);
		}
		else if( parent instanceof BookDocument ) {
			return new BookChannelTreeNode(((BookDocument) parent).getBook(), index);
		}
		else if( parent instanceof BookChannelTreeNode ) {
			BookChannelTreeNode channel = (BookChannelTreeNode) parent;
			return new BookSegmentTreeNode(channel.getBook(), channel.getChannelIndex(), index);
		}
		else if( parent instanceof BookSegmentTreeNode ) {
			return new BookAtomTreeNode( ((BookSegmentTreeNode) parent).getSegment().getAtomAt(index), index );
		}
		return null;
	}

	
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if( parent == ROOT_NODE && ( child instanceof Document ) ) {
			return documentManager.getIndexOfDocument(ManagedDocumentType.BOOK, ((Document) child));
		}
		else if( parent instanceof BookDocument && child instanceof BookChannelTreeNode ) {
			return ((BookChannelTreeNode) child).getChannelIndex();
		}
		else if( parent instanceof BookChannelTreeNode && child instanceof BookSegmentTreeNode ) {
			return ((BookSegmentTreeNode) child).getSegmentIndex();
		}
		else if( parent instanceof BookSegmentTreeNode && child instanceof BookAtomTreeNode ) {
			return ((BookAtomTreeNode) child).getIndex();
		}
		return -1;
	}

	@Override
	public boolean isLeaf(Object node) {
		if( node == ROOT_NODE ) {
			return false;
		}
		else if( node instanceof BookDocument ) {
			return false;
		}
		else if( node instanceof BookChannelTreeNode ) {
			return false;
		}
		else if( node instanceof BookSegmentTreeNode ) {
			return false;
		}
		return true;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// ignored
	}

	@Override
	public void documentAdded(DocumentManagerEvent e) {
		
		Document document = e.getDocument();
		if( document instanceof BookDocument ) {
			BookDocument bookDocument = (BookDocument) document;
			bookDocument.addPropertyChangeListener(this);
		
			fireTreeNodesInserted(this, new Object[] { ROOT_NODE }, new int[] { e.getInTypeIndex() }, new Object[] { bookDocument } );
		}
		
	}

	@Override
	public void documentRemoved(DocumentManagerEvent e) {

		Document document = e.getDocument();
		if( document instanceof BookDocument ) {
			BookDocument bookDocument = (BookDocument) document;
			bookDocument.removePropertyChangeListener(this);
		
			fireTreeNodesRemoved(this, new Object[] { ROOT_NODE }, new int[] { e.getInTypeIndex() }, new Object[] { bookDocument } );
		}
		
	}

	@Override
	public void documentPathChanged(DocumentManagerEvent e) {
		
		Document document = e.getDocument();
		if( document instanceof BookDocument ) {
			BookDocument bookDocument = (BookDocument) document;

			fireTreeNodesChanged(this, new Object[] { ROOT_NODE }, new int[] { e.getInTypeIndex() }, new Object[] { bookDocument } );
		}
			
	}

}
