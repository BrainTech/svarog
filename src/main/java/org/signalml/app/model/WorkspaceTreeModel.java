/* WorkspaceTreeModel.java created 2007-09-11
 *
 */
package org.signalml.app.model;

import javax.swing.tree.TreePath;

import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.DocumentManagerEvent;
import org.signalml.app.document.DocumentManagerListener;
import org.signalml.app.document.MRUDEntry;
import org.signalml.app.document.MRUDRegistry;
import org.signalml.app.document.MRUDRegistryEvent;
import org.signalml.app.document.MRUDRegistryListener;
import org.signalml.app.document.ManagedDocumentType;

/** WorkspaceTreeModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class WorkspaceTreeModel extends AbstractTreeModel implements DocumentManagerListener, MRUDRegistryListener {

	/* extends DefaultTreeModel */

	private static final String ROOT_NODE = "workspaceTree.root";
	private static final String OPEN_DOCUMENTS_NODE = "workspaceTree.openDocuments";
	private static final String OPEN_SIGNALS_NODE = "workspaceTree.openSignals";
	private static final String OPEN_MONITORS_NODE = "workspaceTree.openMonitors";
	private static final String OPEN_BOOKS_NODE = "workspaceTree.openBooks";
	private static final String OPEN_TAGS_NODE = "workspaceTree.openTags";
	private static final String RECENT_DOCUMENTS_NODE = "workspaceTree.recentDocuments";
	private static final String RECENT_SIGNALS_NODE = "workspaceTree.recentSignals";
	private static final String RECENT_MONITORS_NODE = "workspaceTree.recentMonitors";
	private static final String RECENT_BOOKS_NODE = "workspaceTree.recentBooks";
	private static final String RECENT_TAGS_NODE = "workspaceTree.recentTags";

	private static final String[] ROOT_NODE_CHILDREN = new String[] {
	        OPEN_DOCUMENTS_NODE,
	        RECENT_DOCUMENTS_NODE
	};


	private static final String[] OPEN_NODE_CHILDREN = new String[] { 
		OPEN_SIGNALS_NODE, 
		OPEN_MONITORS_NODE, 
		OPEN_BOOKS_NODE,
		OPEN_TAGS_NODE
	};

	private static final String[] RECENT_NODE_CHILDREN = new String[] { 
		RECENT_SIGNALS_NODE,
		RECENT_MONITORS_NODE,
		RECENT_BOOKS_NODE,
		RECENT_TAGS_NODE
	};
	
	private static final ManagedDocumentType[] OPEN_TYPES = new ManagedDocumentType[] { 
		ManagedDocumentType.SIGNAL,
		ManagedDocumentType.MONITOR,
		ManagedDocumentType.BOOK,
		ManagedDocumentType.TAG
	};

	private static final ManagedDocumentType[] RECENT_TYPES = new ManagedDocumentType[] { 
		ManagedDocumentType.SIGNAL,
		ManagedDocumentType.MONITOR,
		ManagedDocumentType.BOOK,
		ManagedDocumentType.TAG
	};

	private DocumentManager documentManager;
	private MRUDRegistry mrudRegistry;

	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public MRUDRegistry getMrudRegistry() {
		return mrudRegistry;
	}

	public void setMrudRegistry(MRUDRegistry mrudRegistry) {
		this.mrudRegistry = mrudRegistry;
	}

	public Object getRoot() {
		return ROOT_NODE;
	}

	public int getChildCount(Object parent) {
		if (parent == ROOT_NODE) {
			return ROOT_NODE_CHILDREN.length;
		}
		else if (parent == OPEN_DOCUMENTS_NODE) {
			return OPEN_TYPES.length;
		}
		else if (parent == RECENT_DOCUMENTS_NODE) {
			return RECENT_TYPES.length;
		} else if (parent == OPEN_SIGNALS_NODE) {
			return documentManager.getDocumentCount(ManagedDocumentType.SIGNAL);
		} else if (parent == OPEN_MONITORS_NODE) {
			return documentManager.getDocumentCount(ManagedDocumentType.MONITOR);
		} else if (parent == OPEN_BOOKS_NODE) {
			return documentManager.getDocumentCount(ManagedDocumentType.BOOK);
		} else if (parent == OPEN_TAGS_NODE) {
			return documentManager.getDocumentCount(ManagedDocumentType.TAG);
		} else if (parent == RECENT_SIGNALS_NODE) {
			return mrudRegistry.getMRUDEntryCount(ManagedDocumentType.SIGNAL);
		} else if (parent == RECENT_MONITORS_NODE) {
			return mrudRegistry.getMRUDEntryCount(ManagedDocumentType.MONITOR);
		} else if (parent == RECENT_BOOKS_NODE) {
			return mrudRegistry.getMRUDEntryCount(ManagedDocumentType.BOOK);
		} else if (parent == RECENT_TAGS_NODE) {
			return mrudRegistry.getMRUDEntryCount(ManagedDocumentType.TAG);
		}
		return 0;
	}

	public Object getChild(Object parent, int index) {
		if (parent == ROOT_NODE) {
			return ROOT_NODE_CHILDREN[index];
		}
		else if (parent == OPEN_DOCUMENTS_NODE) {
			return OPEN_NODE_CHILDREN[index];
		}
		else if (parent == RECENT_DOCUMENTS_NODE) {
			return RECENT_NODE_CHILDREN[index];
		} else if (parent == OPEN_SIGNALS_NODE) {
			return documentManager.getDocumentAt(ManagedDocumentType.SIGNAL, index);
		} else if (parent == OPEN_MONITORS_NODE) {
			return documentManager.getDocumentAt(ManagedDocumentType.MONITOR, index);
		} else if (parent == OPEN_BOOKS_NODE) {
			return documentManager.getDocumentAt(ManagedDocumentType.BOOK, index);
		} else if (parent == OPEN_TAGS_NODE) {
			return documentManager.getDocumentAt(ManagedDocumentType.TAG, index);
		} else if (parent == RECENT_SIGNALS_NODE) {
			return mrudRegistry.getMRUDEntryAt(ManagedDocumentType.SIGNAL, index);
		} else if (parent == RECENT_MONITORS_NODE) {
			return mrudRegistry.getMRUDEntryAt(ManagedDocumentType.MONITOR, index);
		} else if (parent == RECENT_BOOKS_NODE) {
			return mrudRegistry.getMRUDEntryAt(ManagedDocumentType.BOOK, index);
		} else if (parent == RECENT_TAGS_NODE) {
			return mrudRegistry.getMRUDEntryAt(ManagedDocumentType.TAG, index);
		}
		return null;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == ROOT_NODE) {
			return getArrayIndex(ROOT_NODE_CHILDREN, child);
		}
		else if (parent == OPEN_DOCUMENTS_NODE && (child instanceof String)) {
			for (int i = 0; i<OPEN_TYPES.length; i++) {
				if (child == OPEN_NODE_CHILDREN[i]) {
					return i;
				}
			}
		}
		else if (parent == RECENT_DOCUMENTS_NODE && (child instanceof String)) {
			for (int i = 0; i<RECENT_TYPES.length; i++) {
				if (child == RECENT_NODE_CHILDREN[i]) {
					return i;
				}
			}
		} else if (parent == OPEN_SIGNALS_NODE && (child instanceof Document)) {
			return documentManager.getIndexOfDocument(ManagedDocumentType.SIGNAL, (Document) child);
		} else if (parent == OPEN_MONITORS_NODE && (child instanceof Document)) {
			return documentManager.getIndexOfDocument(ManagedDocumentType.MONITOR, (Document) child);
		} else if (parent == OPEN_BOOKS_NODE && (child instanceof Document)) {
			return documentManager.getIndexOfDocument(ManagedDocumentType.BOOK, (Document) child);
		} else if (parent == OPEN_TAGS_NODE && (child instanceof Document)) {
			return documentManager.getIndexOfDocument(ManagedDocumentType.TAG, (Document) child);
		} else if (parent == RECENT_SIGNALS_NODE && (child instanceof MRUDEntry)) {
			return mrudRegistry.getIndexOfMRUDEntry(ManagedDocumentType.SIGNAL, (MRUDEntry) child);
		} else if (parent == RECENT_MONITORS_NODE && (child instanceof MRUDEntry)) {
			return mrudRegistry.getIndexOfMRUDEntry(ManagedDocumentType.MONITOR, (MRUDEntry) child);
		} else if (parent == RECENT_BOOKS_NODE && (child instanceof MRUDEntry)) {
			return mrudRegistry.getIndexOfMRUDEntry(ManagedDocumentType.BOOK, (MRUDEntry) child);
		} else if (parent == RECENT_TAGS_NODE && (child instanceof MRUDEntry)) {
			return mrudRegistry.getIndexOfMRUDEntry(ManagedDocumentType.TAG, (MRUDEntry) child);
		}
		return -1;
	}

	public boolean isLeaf(Object parent) {
		if (parent instanceof String) {
			return false;
		}
		return true;
	}

	private int getArrayIndex(Object[] arr, Object o) {
		for (int i=0; i<arr.length; i++) {
			if (arr[i] == o) {
				return i;
			}
		}
		return -1;
	}

	public TreePath getTreePathToRoot(Document document) {
		return new TreePath(getObjectPathToRoot(document));
	}

	public Object[] getObjectPathToRoot(Document document) {
		ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
		if (type == null) {
			return new Object[0];
		}
		return new Object[] {
		               ROOT_NODE,
		               OPEN_DOCUMENTS_NODE,
		               OPEN_NODE_CHILDREN[getArrayIndex(OPEN_TYPES, type)],
		               document
		       };
	}

	public Object[] getObjectPathToRootFromParent(Document document) {
		ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
		if (type == null) {
			return new Object[0];
		}
		return new Object[] {
		               ROOT_NODE,
		               OPEN_DOCUMENTS_NODE,
		               OPEN_NODE_CHILDREN[getArrayIndex(OPEN_TYPES, type)]
		       };
	}

	public Object[] getObjectPathToRootFromParent(MRUDEntry entry) {
		ManagedDocumentType type = entry.getDocumentType();
		if (type == null) {
			return new Object[0];
		}
		return new Object[] {
		               ROOT_NODE,
		               RECENT_DOCUMENTS_NODE,
		               RECENT_NODE_CHILDREN[getArrayIndex(RECENT_TYPES, type)]
		       };
	}

	public Object getParentObject(Document document) {
		ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
		if (type == null) {
			return new Object[0];
		}
		return OPEN_NODE_CHILDREN[getArrayIndex(OPEN_TYPES, type)];
	}

	@Override
	public void documentAdded(DocumentManagerEvent e) {
		Document document = e.getDocument();
		fireTreeNodesInserted(
		        this,
		        getObjectPathToRootFromParent(document),
		        new int[] { e.getInTypeIndex() },
		        new Object[] { document }
		);
	}

	@Override
	public void documentRemoved(DocumentManagerEvent e) {
		Document document = e.getDocument();
		fireTreeNodesRemoved(
		        this,
		        getObjectPathToRootFromParent(document),
		        new int[] { e.getInTypeIndex() },
		        new Object[] { document }
		);
	}

	@Override
	public void documentPathChanged(DocumentManagerEvent e) {
		Document document = e.getDocument();
		fireTreeNodesChanged(
		        this,
		        getObjectPathToRootFromParent(document),
		        new int[] { e.getInTypeIndex() },
		        new Object[] { document }
		);
	}

	@Override
	public void mrudEntryRegistered(MRUDRegistryEvent e) {
		MRUDEntry entry = e.getEntry();
		fireTreeNodesInserted(
		        this,
		        getObjectPathToRootFromParent(entry),
		        new int[] { e.getInTypeIndex() },
		        new Object[] { entry }
		);
	}

	@Override
	public void mrudEntryRemoved(MRUDRegistryEvent e) {
		MRUDEntry entry = e.getEntry();
		fireTreeNodesRemoved(
		        this,
		        getObjectPathToRootFromParent(entry),
		        new int[] { e.getInTypeIndex() },
		        new Object[] { entry }
		);
	}

}
