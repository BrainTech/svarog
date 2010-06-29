/* TaskEventAdapter.java created 2007-09-12
 *
 */
package org.signalml.task;

/** An empty implementation of {@link TaskEventListener}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskEventAdapter implements TaskEventListener {

	@Override
	public void taskAborted(TaskEvent ev) {}

	@Override
	public void taskFinished(TaskEvent ev) {}

	@Override
	public void taskResumed(TaskEvent ev) {}

	@Override
	public void taskStarted(TaskEvent ev) {}

	@Override
	public void taskSuspended(TaskEvent ev) {}

	@Override
	public void taskRequestChanged(TaskEvent ev) {
	}

	@Override
	public void taskMessageSet(TaskEvent ev) {}

	@Override
	public void taskTickerUpdated(TaskEvent ev) {}

}
