/* AbstractTreeModel.java created 2007-09-12
 *
 */
package org.signalml.plugin.export.view;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * The abstract model for a {@link AbstractViewerTree tree} in the left
 * tabbed pane.
 * Contains the list of {@link TreeModelListener listeners} that wait for
 * events of:
 * <ul>
 * <li>addition, removal or change of nodes,</li>
 * <li>change in tree structure.</li>
 * </ul>
 * 
 * @see TreeModel
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractTreeModel implements TreeModel {

	/**
	 * The list of listeners. Only {@link TreeModelListener}s
	 * are held in it. 
	 */
	protected EventListenerList listenerList = new EventListenerList();

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	/**
	 * Does nothing because the tree is not editable
	 */
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// tree not editable
	}

	/**
	 * Informs listeners that tree nodes have changed.
	 * The parameters are just like in here:
	 * {@link TreeModelEvent#TreeModelEvent(Object, Object[], int[], Object[])}
	 * (this event passed to listeners).
	 */
	protected void fireTreeNodesChanged(Object source, Object[] path,
	                                    int[] childIndices,
	                                    Object[] children) {
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TreeModelListener.class) {
				if (e == null) {
					e = new TreeModelEvent(source, path,childIndices, children);
				}
				((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
			}
		}
	}

	/**
	 * Informs listeners that tree nodes have been inserted.
	 * The parameters are just like in here:
	 * {@link TreeModelEvent#TreeModelEvent(Object, Object[], int[], Object[])}
	 * (this event passed to listeners).
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path,
	                                     int[] childIndices,
	                                     Object[] children) {
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TreeModelListener.class) {
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
			}
		}
	}

	/**
	 * Informs listeners that tree nodes have been removed.
	 * The parameters are just like in here:
	 * {@link TreeModelEvent#TreeModelEvent(Object, Object[], int[], Object[])}
	 * (this event passed to listeners).
	 */
	protected void fireTreeNodesRemoved(Object source, Object[] path,
	                                    int[] childIndices,
	                                    Object[] children) {
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TreeModelListener.class) {
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
			}
		}
	}

	/**
	 * Informs listeners that the structure of the tree changed.
	 * The parameters are just like in here:
	 * {@link TreeModelEvent#TreeModelEvent(Object, Object[], int[], Object[])}
	 * (this event passed to listeners).
	 */
	protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TreeModelListener.class) {
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
			}
		}
	}

	/**
	 * Informs listeners that the structure of the tree changed.
	 * The parameters are just like in here:
	 * {@link TreeModelEvent#TreeModelEvent(Object, Object[])}
	 * (this event passed to listeners).
	 */
	protected void fireTreeStructureChanged(Object source, Object[] path) {
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TreeModelListener.class) {
				if (e == null) {
					e = new TreeModelEvent(source, path);
				}
				((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
			}
		}
	}



}
