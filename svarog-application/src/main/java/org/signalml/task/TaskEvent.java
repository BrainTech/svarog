/* TaskEvent.java created 2007-09-12
 * 
 */
package org.signalml.task;

import java.util.EventObject;

import org.springframework.context.MessageSourceResolvable;

/** An event corresponding to a change in the state of a {@link Task}.
 *
 * @see TaskEventListener
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	/** Possible event types.
	 *
	 */
	public enum TaskEventType {
		
		TASK_STARTED,
		TASK_SUSPENDED,
		TASK_RESUMED,
		TASK_ABORTED,
		TASK_FINISHED,
		TASK_REQUEST_CHANGED,
		TASK_MESSAGE_SET,
		TASK_TICKER_UPDATED
		
	}
	
	// this event must have the type inside, because some events are pipelined down a common channel
	private TaskEventType type;
	
	private TaskStatus status;
	private TaskResult result;
	private MessageSourceResolvable message;
	private int[] tickerLimits;
	private int[] tickers;
		
	public TaskEvent(Object source, TaskEventType type, TaskStatus status) {
		super(source);
		this.type = type;
		this.status = status;
	}
	
	public TaskEvent(Object source, TaskEventType type, TaskStatus status, TaskResult result) {
		super(source);
		this.type = type;
		this.status = status;
		this.result = result;
	}
	
	public TaskEvent(Object source, TaskEventType type, TaskStatus status, MessageSourceResolvable message) {
		super(source);
		this.type = type;
		this.status = status;
		this.message = message;
	}
	
	public TaskEvent(Object source, TaskEventType type, TaskStatus status, int[] tickerLimits, int[] tickers) {
		super(source);
		this.type = type;
		this.tickerLimits = tickerLimits;
		this.status = status;
		this.tickers = tickers;
	}

	public Task getTask() {
		return (Task) source;
	}

	public TaskEventType getType() {
		return type;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public TaskResult getResult() {
		return result;
	}

	public MessageSourceResolvable getMessage() {
		return message;
	}

	public int[] getTickerLimits() {
		return tickerLimits;
	}

	public int[] getTickers() {
		return tickers;
	}	

}
