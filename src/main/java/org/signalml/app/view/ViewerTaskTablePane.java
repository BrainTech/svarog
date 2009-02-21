/* ViewerTaskTablePane.java created 2008-02-07
 * 
 */

package org.signalml.app.view;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;


/** ViewerTaskTablePane
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerTaskTablePane extends JPanel {

	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane;
	private ViewerTaskTable taskTable;
		
	private JToolBar toolBar;

	public ViewerTaskTablePane(ViewerTaskTable table) {
		super(new BorderLayout());
		this.taskTable = table;
	}

	public void initialize() {
		
		setBorder( new EmptyBorder(3,3,3,3) );
		
		add(getToolBar(), BorderLayout.NORTH);
		add(getScrollPane(),BorderLayout.CENTER);
	
	}
	
	
	public JScrollPane getScrollPane() {
		if( scrollPane == null ) {
			scrollPane = new JScrollPane(taskTable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);					
		}
		return scrollPane;
	}

	public JToolBar getToolBar() {
		if( toolBar == null ) {
			
			toolBar = new JToolBar(JToolBar.HORIZONTAL);
			toolBar.setFloatable(false);
			toolBar.add(Box.createHorizontalGlue());			
			
			toolBar.add( taskTable.getSuspendAllTasksAction() );
			toolBar.add( taskTable.getResumeAllTasksAction() );
			toolBar.add( taskTable.getAbortAllTasksAction() );
			toolBar.addSeparator();
			toolBar.add( taskTable.getRemoveAllFinishedTasksAction() );
			toolBar.add( taskTable.getRemoveAllAbortedTasksAction() );
			toolBar.add( taskTable.getRemoveAllFailedTasksAction() );
			toolBar.addSeparator();
			toolBar.add( taskTable.getRemoveAllTasksAction() );
			
		}
		return toolBar;
	}
	
	public JTable getTaskTable() {
		return taskTable;
	}
		
}
