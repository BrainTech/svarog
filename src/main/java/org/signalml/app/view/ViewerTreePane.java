/* ViewerWorkspaceTreePane.java created 2007-09-14
 * 
 */
package org.signalml.app.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/** ViewerWorkspaceTreePane
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerTreePane extends JPanel {

	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane;
	private JTree tree;

	
	public ViewerTreePane(JTree tree) {
		super(new BorderLayout());

		this.tree = tree;
		scrollPane = new JScrollPane(tree,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane,BorderLayout.CENTER);
		
	}
	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public JTree getTree() {
		return tree;
	}

	public void setTree(JTree tree) {
		this.tree = tree;
	}	
	
}
