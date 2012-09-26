/* ViewerWorkspaceTreePane.java created 2007-09-14
 *
 */
package org.signalml.plugin.export.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * The tab in the left panel (tree panel).
 * Contains the tree that is displayed within
 * this tab (in a scroll pane).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerTreePane extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the scroll pane that fills the tab and in which the tree is located
	 */
	private JScrollPane scrollPane;
	/**
	 * the tree that is displayed in this tab
	 */
	private JTree tree;

	/**
	 * Constructor.
	 * Crates a scroll pane with a tree in it and adds
	 * the scroll panel to this tab
	 * @param tree a tree to be displayed in this tab
	 */
	public ViewerTreePane(JTree tree) {
		super(new BorderLayout());

		this.tree = tree;
		scrollPane = new JScrollPane(tree,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane,BorderLayout.CENTER);

	}

	/**
	 * Returns the scroll pane which is displayed in this tab.
	 * TODO never used
	 * @return the scroll pane which is displayed in this tab
	 */
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	/**
	 * Returns the tree which is displayed in this tab (within a scroll pane).
	 * @return the tree which is displayed in this tab (within a scroll pane)
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * Sets the tree which is displayed in this tab (within a scroll pane).
	 * TODO never used
	 * @param tree the tree which is displayed in this tab (within a scroll pane)
	 */
	public void setTree(JTree tree) {
		this.tree = tree;
	}

}
