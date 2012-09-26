/* TaskStatus.java created 2007-09-12
 *
 */
package org.signalml.task;

import java.io.Serializable;

import org.signalml.app.view.I18nMessage;
import org.signalml.app.view.I18nMessageStringWrapper;

import static org.signalml.app.util.i18n.SvarogI18n._;

/** An enumeration of possible task statuses.
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum TaskStatus implements Serializable {

	/** The task is new and was never submitted for execution.
	 */
	NEW(100),

	/** The task is currently being executed.
	 */
	ACTIVE(100),

	/** The task is waiting to be executed.
	 */
	ACTIVE_WAITING(90),

	/** The task is executing, but has been signalled to suspend.
	 */
	REQUESTING_SUSPEND(100),

	/** The task is suspended.
	 */
	SUSPENDED(50),

	/** The task is executing, but has been signalled to abort.
	 */
	REQUESTING_ABORT(100),

	/** The task has been aborted.
	 */
	ABORTED(0),

	/** The task encountered an error.
	 */
	ERROR(10),

	/** The task has successfully finished with a result.
	 */
	FINISHED(20);

	private int importance;

	private TaskStatus() {
	}

	private TaskStatus(int importance) {
		this.importance = importance;
	}

	/** Returns the <i>importance</i> of the status. This is used for sorting of tasks.
	 *
	 *
	 */
	public int getImportance() {
		return importance;
	}

	/** Returns true if the status is NEW.
	 */
	public boolean isNew() {
		return (this == NEW);
	}

	/** Returns true if the status is ACTIVE.
	 */
	public boolean isActiveAndRunning() {
		return (this == ACTIVE);
	}

	/** Returns true if the status is ACTIVE_WAITING.
	 */
	public boolean isActiveAndWaiting() {
		return (this == ACTIVE_WAITING);
	}

	/** Returns true if the status is ACTIVE or ACTIVE_WAITING.
	 */
	public boolean isActive() {
		return (this == ACTIVE || this == ACTIVE_WAITING);
	}

	/** Returns true if the status is ACTIVE, ACTIVE_WAITING, REQUESTING_ABORT or REQUESTING_SUSPEND.
	 */
	public boolean isRunning() {
		return (this == ACTIVE || this == ACTIVE_WAITING || this == REQUESTING_ABORT || this == REQUESTING_SUSPEND);
	}

	/** Returns true if the status is SUSPENDED.
	 */
	public boolean isSuspended() {
		return (this == SUSPENDED);
	}

	/** Returns true if the status is REQUESTING_ABORT.
	 */
	public boolean isRequestingAbort() {
		return (this == REQUESTING_ABORT);
	}

	/** Returns true if the status is REQUESTING_SUSPEND.
	 */
	public boolean isRequestingSuspend() {
		return (this == REQUESTING_SUSPEND);
	}

	/** Returns true if the status is ABORTED.
	 */
	public boolean isAborted() {
		return (this == ABORTED);
	}

	/** Returns true if the status is ERROR.
	 */
	public boolean isError() {
		return (this == ERROR);
	}

	/** Returns true if the status is FINISHED.
	 */
	public boolean isFinished() {
		return (this == FINISHED);
	}

	/** Returns true if the status is FINISHED or ERROR.
	 */
	public boolean isResultAvailable() {
		return (this == FINISHED || this == ERROR);
	}

	/** Returns true if the status is NEW.
	 */
	public boolean isStartable() {
		return (this == NEW);
	}

	/** Check whether the task can be requested to abort. This is
	 * possible if the task is or will soon be active or suspended.
	 * @return true if the task can be aborted (is ACTIVE,
	 * ACTIVE_WAITING, REQUESTING_SUSPEND or SUSPENDED)
	 * @see org.signalml.task.LocalTask#abort
	 */
	public boolean isAbortable() {
		return (this == ACTIVE || this == ACTIVE_WAITING || this == REQUESTING_SUSPEND || this == SUSPENDED);
	}

	/** Returns true if the status is SUSPENDED.
	 */
	public boolean isResumable() {
		return (this == SUSPENDED);
	}

	/** Returns true if the status is ACTIVE, ACTIVE_WAITING.
	 */
	public boolean isSuspendable() {
		return (this == ACTIVE || this == ACTIVE_WAITING);
	}

	/**
	 * Returns a short status description.
	 */
	public I18nMessage getShortStatus() {
		return new I18nMessageStringWrapper(getShortStatusString());
	}

	/**
	 * Returns a long status description (typically 1 sentence).
	 */
	public I18nMessage getLongStatus() {
		return new I18nMessageStringWrapper(getLongStatusString());
	}

	private String getShortStatusString() {
		switch (this) {
		case NEW:
			return _("New");
		case ACTIVE:
			return _("Active");
		case ACTIVE_WAITING:
			return _("Waiting");
		case REQUESTING_SUSPEND:
			return _("Requesting suspend");
		case SUSPENDED:
			return _("Suspended");
		case REQUESTING_ABORT:
			return _("Requesting abort");
		case ABORTED:
			return _("Aborted");
		case ERROR:
			return _("Error");
		case FINISHED:
			return _("Finished");
		default:
			throw new IllegalStateException();
		}
	}

	private String getLongStatusString() {
		switch (this) {
		case NEW:
			return _("New task");
		case ACTIVE:
			return _("Task is running");
		case ACTIVE_WAITING:
			return _("Task is waiting to run");
		case REQUESTING_SUSPEND:
			return _("Task is requesting suspend");
		case SUSPENDED:
			return _("Task has been suspended");
		case REQUESTING_ABORT:
			return _("Task is requesting abort");
		case ABORTED:
			return _("Task has been aborted");
		case ERROR:
			return _("Task encountered an error");
		case FINISHED:
			return _("Task has finished");
		default:
			throw new IllegalStateException();
		}
	}
}
