/* ViewerTablePane.java created 2007-09-14
 * 
 */
package org.signalml.app.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/** ViewerTablePane
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerTablePane extends JPanel {

	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane;
	private JTable table;	

	public ViewerTablePane(JTable table) {
		super(new BorderLayout());
		this.table = table;
		scrollPane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
		add(scrollPane,BorderLayout.CENTER);
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}
	
}
