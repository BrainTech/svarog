/* ViewerTaskTable.java created 2007-09-11
 * 
 */
package org.signalml.app.view;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.signalml.app.action.AbortAllTasksAction;
import org.signalml.app.action.AbortTaskAction;
import org.signalml.app.action.GetTaskErrorAction;
import org.signalml.app.action.GetTaskResultAction;
import org.signalml.app.action.RemoveAllAbortedTasksAction;
import org.signalml.app.action.RemoveAllFailedTasksAction;
import org.signalml.app.action.RemoveAllFinishedTasksAction;
import org.signalml.app.action.RemoveAllTasksAction;
import org.signalml.app.action.RemoveTaskAction;
import org.signalml.app.action.ResumeAllTasksAction;
import org.signalml.app.action.ResumeTaskAction;
import org.signalml.app.action.ShowTaskDialogAction;
import org.signalml.app.action.SuspendAllTasksAction;
import org.signalml.app.action.SuspendTaskAction;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.ActionFocusSupport;
import org.signalml.app.action.selector.TaskFocusSelector;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.model.TaskTableModel;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.method.SuspendableMethod;
import org.signalml.task.Task;
import org.signalml.task.TaskStatus;
import org.springframework.context.support.MessageSourceAccessor;

/** ViewerTaskTable
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerTaskTable extends JTable implements TaskFocusSelector {

	private static final long serialVersionUID = 1L;
	
	private ActionFocusSupport afSupport = new ActionFocusSupport(this);
	
	private MessageSourceAccessor messageSource;
	
	private JPopupMenu taskPopupMenu;
	private JPopupMenu suspendableTaskPopupMenu;
	private JPopupMenu finishedTaskPopupMenu;
	private JPopupMenu errorTaskPopupMenu;
	private JPopupMenu abortedTaskPopupMenu;
	
	private ActionFocusManager actionFocusManager;
	private ApplicationTaskManager taskManager;
	private ApplicationMethodManager methodManager;
	private ErrorsDialog errorsDialog;
	
	private ShowTaskDialogAction showTaskDialogAction;
	private AbortTaskAction abortTaskAction;
	private SuspendTaskAction suspendTaskAction;
	private ResumeTaskAction resumeTaskAction;
	private GetTaskResultAction getTaskResultAction;
	private GetTaskErrorAction getTaskErrorAction;
	private RemoveTaskAction removeTaskAction;
	
	private AbortAllTasksAction abortAllTasksAction;
	private SuspendAllTasksAction suspendAllTasksAction;
	private ResumeAllTasksAction resumeAllTasksAction;
	private RemoveAllTasksAction removeAllTasksAction;
	private RemoveAllFinishedTasksAction removeAllFinishedTasksAction;
	private RemoveAllAbortedTasksAction removeAllAbortedTasksAction;
	private RemoveAllFailedTasksAction removeAllFailedTasksAction;
	
	private Task activeTask;
	
	public ViewerTaskTable(TaskTableModel model, MessageSourceAccessor messageSource) {
		super(model, (TableColumnModel) null);
		this.messageSource = messageSource;
		
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		
		columnModel.setColumnSelectionAllowed(false);
		
		TableColumn tc;
		
		tc = new TableColumn(TaskTableModel.STATUS_COLUMN, 100);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		TaskStatusCellRenderer taskStatusCellRenderer = new TaskStatusCellRenderer();
		taskStatusCellRenderer.setMessageSource(messageSource);
		tc.setCellRenderer(taskStatusCellRenderer);
		columnModel.addColumn(tc);
		
		tc = new TableColumn(TaskTableModel.METHOD_NAME_COLUMN, 100);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		columnModel.addColumn(tc);
		
		tc = new TableColumn(TaskTableModel.CREATE_TIME_COLUMN, 100);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		DateTableCellRenderer dateTableCellRenderer = new DateTableCellRenderer();
		tc.setCellRenderer(dateTableCellRenderer);
		columnModel.addColumn(tc);
		
		tc = new TableColumn(TaskTableModel.PROGRESS_COLUMN, 100);		
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		tc.setResizable(false);
		tc.setCellRenderer(new ProgressTableCellRenderer());
		columnModel.addColumn(tc);
		
		tc = new TableColumn(TaskTableModel.MESSAGE_COLUMN, 200);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		columnModel.addColumn(tc);
		
		setColumnModel(columnModel);
		
		setColumnSelectionAllowed(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
		setRowHeight(20);
		
		setRowSorter(model.getSorter());

		getTableHeader().setReorderingAllowed(false);
		
		addMouseListener(new MouseEventHandler());
		
	}
		
	@Override
	public TaskTableModel getModel() {
		return (TaskTableModel) super.getModel();
	}

	@Override
	public JPopupMenu getComponentPopupMenu() {

		int row = getSelectedRow();
		Task task = null;
		if( row >= 0 ) {
			task = taskManager.getTaskAt( convertRowIndexToModel(row) );
		}
						
		return focus(task);
		
	}
		
	@Override
	public Task getActiveTask() {
		return activeTask;
	}

	@Override
	public void addActionFocusListener(ActionFocusListener listener) {
		afSupport.addActionFocusListener(listener);
	}

	@Override
	public void removeActionFocusListener(ActionFocusListener listener) {
		afSupport.removeActionFocusListener(listener);
	}

	private JPopupMenu focus(Task task) {

		JPopupMenu popupMenu = null;
		
		activeTask = task;
		
		if( task != null ) {
			TaskStatus status = task.getStatus();
			if( status.isAborted() ) {
				popupMenu = getAbortedTaskPopupMenu();
			} else if( status.isFinished() ) {
				popupMenu = getFinishedTaskPopupMenu();
			} else if( status.isError() ) {
				popupMenu = getErrorTaskPopupMenu();
			} else {
				if( task.getMethod() instanceof SuspendableMethod ) {
					popupMenu = getSuspendableTaskPopupMenu();
				} else {
					popupMenu = getTaskPopupMenu();
				}				
			}
		}
		
		afSupport.fireActionFocusChanged();

		return popupMenu;
		
	}
	
	private JPopupMenu getTaskPopupMenu() {
	
		if( taskPopupMenu == null ) {
			taskPopupMenu = new JPopupMenu();

			taskPopupMenu.add(getShowTaskDialogAction());
			taskPopupMenu.addSeparator();
			taskPopupMenu.add(getAbortTaskAction());
			taskPopupMenu.addSeparator();
			taskPopupMenu.add( createAllTasksSubmenu() );
			
		}
				
		return taskPopupMenu;
		
	}

	private JPopupMenu getFinishedTaskPopupMenu() {
		
		if( finishedTaskPopupMenu == null ) {
			finishedTaskPopupMenu = new JPopupMenu();

			finishedTaskPopupMenu.add(getShowTaskDialogAction());
			finishedTaskPopupMenu.addSeparator();
			finishedTaskPopupMenu.add(getGetTaskResultAction());
			finishedTaskPopupMenu.addSeparator();
			finishedTaskPopupMenu.add(getRemoveTaskAction());			
			finishedTaskPopupMenu.addSeparator();
			finishedTaskPopupMenu.add( createAllTasksSubmenu() );
			
		}
				
		return finishedTaskPopupMenu;
		
	}
	
	private JPopupMenu getErrorTaskPopupMenu() {
		
		if( errorTaskPopupMenu == null ) {
			errorTaskPopupMenu = new JPopupMenu();

			errorTaskPopupMenu.add(getShowTaskDialogAction());
			errorTaskPopupMenu.addSeparator();
			errorTaskPopupMenu.add(getGetTaskErrorAction());
			errorTaskPopupMenu.addSeparator();
			errorTaskPopupMenu.add(getRemoveTaskAction());			
			errorTaskPopupMenu.addSeparator();
			errorTaskPopupMenu.add( createAllTasksSubmenu() );
			
		}
				
		return errorTaskPopupMenu;
		
	}
	
	private JPopupMenu getAbortedTaskPopupMenu() {
		
		if( abortedTaskPopupMenu == null ) {
			abortedTaskPopupMenu = new JPopupMenu();

			abortedTaskPopupMenu.add(getShowTaskDialogAction());
			abortedTaskPopupMenu.addSeparator();
			abortedTaskPopupMenu.add(getRemoveTaskAction());			
			abortedTaskPopupMenu.addSeparator();
			abortedTaskPopupMenu.add( createAllTasksSubmenu() );
			
		}
				
		return abortedTaskPopupMenu;
		
	}
		
	private JPopupMenu getSuspendableTaskPopupMenu() {
		
		if( suspendableTaskPopupMenu == null ) {
			suspendableTaskPopupMenu = new JPopupMenu();

			suspendableTaskPopupMenu.add(getShowTaskDialogAction());
			suspendableTaskPopupMenu.addSeparator();
			suspendableTaskPopupMenu.add(getSuspendTaskAction());
			suspendableTaskPopupMenu.add(getResumeTaskAction());
			suspendableTaskPopupMenu.addSeparator();
			suspendableTaskPopupMenu.add(getAbortTaskAction());
			suspendableTaskPopupMenu.addSeparator();
			suspendableTaskPopupMenu.add( createAllTasksSubmenu() );
			
		}
				
		return suspendableTaskPopupMenu;
		
	}
	
	private JMenu createAllTasksSubmenu() {
				
		JMenu allTasksSubmenu = new JMenu( messageSource.getMessage("viewer.taskTable.allTasks") );
		
		allTasksSubmenu.add( getSuspendAllTasksAction() );
		allTasksSubmenu.add( getResumeAllTasksAction() );
		allTasksSubmenu.add( getAbortAllTasksAction() );
		allTasksSubmenu.addSeparator();
		allTasksSubmenu.add( getRemoveAllFinishedTasksAction() );
		allTasksSubmenu.add( getRemoveAllAbortedTasksAction() );
		allTasksSubmenu.add( getRemoveAllFailedTasksAction() );
		allTasksSubmenu.addSeparator();
		allTasksSubmenu.add( getRemoveAllTasksAction() );
			
		return allTasksSubmenu;
		
	}
	
	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}

	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(ApplicationTaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
	public ApplicationMethodManager getMethodManager() {
		return methodManager;
	}

	public void setMethodManager(ApplicationMethodManager methodManager) {
		this.methodManager = methodManager;
	}
	
	public ErrorsDialog getErrorsDialog() {
		return errorsDialog;
	}

	public void setErrorsDialog(ErrorsDialog errorsDialog) {
		this.errorsDialog = errorsDialog;
	}

	public ShowTaskDialogAction getShowTaskDialogAction() {
		if( showTaskDialogAction == null ) {
			showTaskDialogAction = new ShowTaskDialogAction(messageSource,this);
			showTaskDialogAction.setTaskManager(taskManager);
		}
		return showTaskDialogAction;
	}


	public AbortTaskAction getAbortTaskAction() {
		if( abortTaskAction == null ) {
			abortTaskAction = new AbortTaskAction(messageSource,this);
		}
		return abortTaskAction;
	}

	public SuspendTaskAction getSuspendTaskAction() {
		if( suspendTaskAction == null ) {
			suspendTaskAction = new SuspendTaskAction(messageSource,this);
		}
		return suspendTaskAction;
	}

	public ResumeTaskAction getResumeTaskAction() {
		if( resumeTaskAction == null ) {
			resumeTaskAction = new ResumeTaskAction(messageSource,this);
			resumeTaskAction.setTaskManager(taskManager);
		}
		return resumeTaskAction;
	}

	public GetTaskResultAction getGetTaskResultAction() {
		if( getTaskResultAction == null ) {
			getTaskResultAction = new GetTaskResultAction(messageSource,this);
			getTaskResultAction.setMethodManager(methodManager);
		}
		return getTaskResultAction;
	}
	
	public GetTaskErrorAction getGetTaskErrorAction() {
		if( getTaskErrorAction == null ) {
			getTaskErrorAction = new GetTaskErrorAction(messageSource,this);
			getTaskErrorAction.setErrorsDialog(errorsDialog);
		}
		return getTaskErrorAction;
	}

	public RemoveTaskAction getRemoveTaskAction() {
		if( removeTaskAction == null ) {
			removeTaskAction = new RemoveTaskAction(messageSource,this);
			removeTaskAction.setTaskManager(taskManager);
		}
		return removeTaskAction;
	}

	public AbortAllTasksAction getAbortAllTasksAction() {
		return abortAllTasksAction;
	}

	public void setAbortAllTasksAction(AbortAllTasksAction abortAllTasksAction) {
		this.abortAllTasksAction = abortAllTasksAction;
	}

	public SuspendAllTasksAction getSuspendAllTasksAction() {
		return suspendAllTasksAction;
	}

	public void setSuspendAllTasksAction(SuspendAllTasksAction suspendAllTasksAction) {
		this.suspendAllTasksAction = suspendAllTasksAction;
	}

	public ResumeAllTasksAction getResumeAllTasksAction() {
		return resumeAllTasksAction;
	}

	public void setResumeAllTasksAction(ResumeAllTasksAction resumeAllTasksAction) {
		this.resumeAllTasksAction = resumeAllTasksAction;
	}

	public RemoveAllTasksAction getRemoveAllTasksAction() {
		return removeAllTasksAction;
	}

	public void setRemoveAllTasksAction(RemoveAllTasksAction removeAllTasksAction) {
		this.removeAllTasksAction = removeAllTasksAction;
	}

	public RemoveAllFinishedTasksAction getRemoveAllFinishedTasksAction() {
		return removeAllFinishedTasksAction;
	}

	public void setRemoveAllFinishedTasksAction(RemoveAllFinishedTasksAction removeAllFinishedTasksAction) {
		this.removeAllFinishedTasksAction = removeAllFinishedTasksAction;
	}

	public RemoveAllAbortedTasksAction getRemoveAllAbortedTasksAction() {
		return removeAllAbortedTasksAction;
	}

	public void setRemoveAllAbortedTasksAction(RemoveAllAbortedTasksAction removeAllAbortedTasksAction) {
		this.removeAllAbortedTasksAction = removeAllAbortedTasksAction;
	}

	public RemoveAllFailedTasksAction getRemoveAllFailedTasksAction() {
		return removeAllFailedTasksAction;
	}

	public void setRemoveAllFailedTasksAction(RemoveAllFailedTasksAction removeAllFailedTasksAction) {
		this.removeAllFailedTasksAction = removeAllFailedTasksAction;
	}
	
	protected class MouseEventHandler extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			ViewerTaskTable table = (ViewerTaskTable) e.getSource();
			if( SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 1) ) {
				int index = table.rowAtPoint(e.getPoint());
				table.getSelectionModel().setSelectionInterval(index, index);
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			ViewerTaskTable table = (ViewerTaskTable) e.getSource();
			if( SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0 ) {
				int selRow = table.convertRowIndexToModel( table.getSelectedRow() );
				if( selRow >= 0 ) {
					focus(taskManager.getTaskAt(selRow));
					getShowTaskDialogAction().actionPerformed(new ActionEvent(table,0,"show"));
				}
			}
		}
			
	}
	
}
