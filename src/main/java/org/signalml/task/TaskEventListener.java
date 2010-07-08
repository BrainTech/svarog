/* TaskEventListener.java created 2007-09-12
 *
 */
package org.signalml.task;

import java.util.EventListener;

/** A listener listening for {@link TaskEvent TaskEvents}
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TaskEventListener extends EventListener {

        /**
         * Invoked when Task becomes started.
         */
	void taskStarted(TaskEvent ev);

        /**
         * Invoked when Task becomes suspended.
         */
	void taskSuspended(TaskEvent ev);

        /**
         * Invoked when Task becomes resumed.
         */
	void taskResumed(TaskEvent ev);

        /**
         * Invoked when Task becomes aborted.
         */
	void taskAborted(TaskEvent ev);

        /**
         * Invoked when Task becomes finished.
         */
	void taskFinished(TaskEvent ev);

        /**
         * Invoked when Task has it's ticker updated.
         */
	void taskTickerUpdated(TaskEvent ev);

        /**
         * Invoked when Task has it's message changed.
         */
	void taskMessageSet(TaskEvent ev);

        /**
         * Invoked when Task has it's request changed.
         */
	void taskRequestChanged(TaskEvent ev);

}
