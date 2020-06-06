/* TaskTableModel.java created 2007-09-11
 *
 */
package org.signalml.app.model.components;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import org.signalml.app.task.ApplicationTaskManager;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.task.AggregateTaskProgressComparator;
import org.signalml.task.AggregateTaskProgressInfo;
import org.signalml.task.Task;
import org.signalml.task.TaskEvent;
import org.signalml.task.TaskEventListener;
import org.signalml.task.TaskManagerEvent;
import org.signalml.task.TaskManagerListener;
import org.signalml.task.TaskStatus;
import org.signalml.task.TaskStatusImportanceComparator;

/**
 * TaskTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class TaskTableModel extends AbstractTableModel implements
	TaskManagerListener, TaskEventListener {

	private static final long serialVersionUID = 1L;

	public static final int STATUS_COLUMN = 0;
	public static final int METHOD_NAME_COLUMN = 1;
	public static final int CREATE_TIME_COLUMN = 2;
	public static final int PROGRESS_COLUMN = 3;
	public static final int MESSAGE_COLUMN = 4;
	private ApplicationTaskManager taskManager;

	private TableRowSorter<TaskTableModel> sorter = null;
	private Map<Task, AggregateTaskProgressInfo> taskToProgressMap = new HashMap<>();

	public TableRowSorter<TaskTableModel> getSorter() {
		if (sorter == null) {
			sorter = new TableRowSorter<>(this);
			sorter.setComparator(TaskTableModel.STATUS_COLUMN,
								 new TaskStatusImportanceComparator());
			sorter.setComparator(TaskTableModel.PROGRESS_COLUMN,
								 new AggregateTaskProgressComparator());
			sorter.setSortsOnUpdates(true);
		}
		return sorter;
	}

	@Override
	public Class<?> getColumnClass(int col) {

		switch (col) {

		case STATUS_COLUMN:
				return TaskStatus.class;

		case METHOD_NAME_COLUMN:
			return String.class;

		case CREATE_TIME_COLUMN:
			return Date.class;

		case PROGRESS_COLUMN:
			return AggregateTaskProgressInfo.class;

		case MESSAGE_COLUMN:
			return String.class;

		default:
			return Object.class;

		}

	}

	@Override
	public String getColumnName(int col) {

		switch (col) {

		case STATUS_COLUMN:
			return _("Status");

		case METHOD_NAME_COLUMN:
			return _("Method");

		case CREATE_TIME_COLUMN:
			return _("Created");

		case PROGRESS_COLUMN:
			return _("Progress");

		case MESSAGE_COLUMN:
			return _("Message");

		default:
			return "???";

		}
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public int getRowCount() {
		return taskManager.getTaskCount();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Task task = taskManager.getTaskAt(row);
		if (task == null) {
			return "???";
		}

		switch (col) {

		case STATUS_COLUMN:
			return task.getStatus();

		case METHOD_NAME_COLUMN:
			return task.getMethod().getName();

		case CREATE_TIME_COLUMN:
			return task.getTaskInfo().getCreateTime();

		case PROGRESS_COLUMN:
			AggregateTaskProgressInfo atpi = taskToProgressMap.get(task);
			if (atpi == null) {
				atpi = new AggregateTaskProgressInfo(task);
				taskToProgressMap.put(task, atpi);
			} else {
				atpi.update();
			}
			return atpi;

		case MESSAGE_COLUMN:
			String message = task.getMessage();
			if (message != null) {
				return message;
			} else {
				return "";
			}

		default:
			return "???";

		}

	}

	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(ApplicationTaskManager taskManager) {
		if (this.taskManager != null) {
			this.taskManager.removeTaskManagerListener(this);
		}
		this.taskManager = taskManager;
		if (taskManager != null) {
			taskManager.addTaskManagerListener(this);
		}
	}

	@Override
	public void taskAdded(TaskManagerEvent e) {
		taskManager.getEventProxyForTask(e.getTask())
		.addTaskEventListener(this);
		int index = e.getIndex();
		fireTableRowsInserted(index, index);
	}

	@Override
	public void taskRemoved(TaskManagerEvent e) {
		Task task = e.getTask();
		taskManager.getEventProxyForTask(task).removeTaskEventListener(this);
		taskToProgressMap.remove(task);
		int index = e.getIndex();
		fireTableRowsDeleted(index, index);
	}

	@Override
	public void taskAborted(TaskEvent ev) {
		int index = taskManager.getIndexOfTask(ev.getTask());
		if (index >= 0) {
			fireTableCellUpdated(index, STATUS_COLUMN);
		}
	}

	@Override
	public void taskFinished(TaskEvent ev) {
		int index = taskManager.getIndexOfTask(ev.getTask());
		if (index >= 0) {
			fireTableCellUpdated(index, STATUS_COLUMN);
		}
	}

	@Override
	public void taskResumed(TaskEvent ev) {
		int index = taskManager.getIndexOfTask(ev.getTask());
		if (index >= 0) {
			fireTableCellUpdated(index, STATUS_COLUMN);
		}
	}

	@Override
	public void taskStarted(TaskEvent ev) {
		int index = taskManager.getIndexOfTask(ev.getTask());
		if (index >= 0) {
			fireTableCellUpdated(index, STATUS_COLUMN);
		}
	}

	@Override
	public void taskSuspended(TaskEvent ev) {
		int index = taskManager.getIndexOfTask(ev.getTask());
		if (index >= 0) {
			fireTableCellUpdated(index, STATUS_COLUMN);
		}
	}

	@Override
	public void taskRequestChanged(TaskEvent ev) {
		int index = taskManager.getIndexOfTask(ev.getTask());
		if (index >= 0) {
			fireTableCellUpdated(index, STATUS_COLUMN);
		}
	}

	@Override
	public void taskMessageSet(TaskEvent ev) {
		int index = taskManager.getIndexOfTask(ev.getTask());
		if (index >= 0) {
			fireTableCellUpdated(index, MESSAGE_COLUMN);
		}
	}

	@Override
	public void taskTickerUpdated(TaskEvent ev) {
		int index = taskManager.getIndexOfTask(ev.getTask());
		if (index >= 0) {
			// fireTableCellUpdated( index, PROGRESS_COLUMN );

			// XXX for unknown reason firing the event for just one column
			// causes the sorter
			// to mix rows (only the progres column gets sorted in the view).
			// Cause of
			// problem unknown, changes to other rows seem to sort whole rows as
			// expected
			fireTableRowsUpdated(index, index);
		}
	}
}
