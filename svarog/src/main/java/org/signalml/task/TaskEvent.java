/* TaskEvent.java created 2007-09-12
 *
 */
package org.signalml.task;

import java.util.EventObject;
import org.signalml.method.TrackableMethod;

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

	/**
	 * Creates new Event with specified Task source, type and status of Event.
	 * @param source Task which caused this Event
	 * @param type type of Event
	 * @param status status of Event
	 */
	public TaskEvent(Object source, TaskEventType type, TaskStatus status) {
		super(source);
		this.type = type;
		this.status = status;
	}

        /**
         * Creates new Event with specified Task source, type and  status of Event and result of Task.
         * @param source Task which caused this Event
         * @param type type of Event
         * @param status status of Event
	 * @param result result of Task
         */
	public TaskEvent(Object source, TaskEventType type, TaskStatus status, TaskResult result) {
		super(source);
		this.type = type;
		this.status = status;
		this.result = result;
	}

        /**
         * Creates new Event with specified Task source, type and status of Event and spacial message.
         * @param source Task which caused this Event
         * @param type type of Event
         * @param status status of Event
	 * @param message message
         */
	public TaskEvent(Object source, TaskEventType type, TaskStatus status, MessageSourceResolvable message) {
		super(source);
		this.type = type;
		this.status = status;
		this.message = message;
	}

        /**
         * Creates new Event with specified Task source, type and status of Event and spacial message.
	 * It also sets tickers and limit of tickers of source task.
         * @param source Task which caused this Event
         * @param type type of Event
         * @param status status of Event
         * @param tickerLimits limits of tickers of Task which caused this Event
	 * @param tickers tickers of Task which caused this Event
         */
	public TaskEvent(Object source, TaskEventType type, TaskStatus status, int[] tickerLimits, int[] tickers) {
		super(source);
		this.type = type;
		this.tickerLimits = tickerLimits;
		this.status = status;
		this.tickers = tickers;
	}

	/**
	 * Returns Task which caused this Event.
	 * @return Task which cause this Event
	 */
	public Task getTask() {
		return (Task) source;
	}

	/**
	 * Returns type of this Event.
	 * @return type of this Event
	 */
	public TaskEventType getType() {
		return type;
	}

	/**
	 * Returns status of Task which caused this Event.
	 * @return status of Task which caused this Event
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * Returns result of Task which caused this Event.
	 * @return result of Task which caused this Event
	 */
	public TaskResult getResult() {
		return result;
	}

        /**
         * Retrieves the message set during construction.
         *
         * @return the message, might be null
         */
	public MessageSourceResolvable getMessage() {
		return message;
	}

        /**
         *  Returns the limits (maximum values) for the tickers
         *  associated with this task. For methods which aren't
         *  trackable an empty array should be returned. For trackable
         *  methods the length of the array should correspond to what
         *  is returned by {@link TrackableMethod#getTickerCount()}
         *  for the executed method.
         *
         * @return the ticker limits
         */
	public int[] getTickerLimits() {
		return tickerLimits;
	}

        /**
         *  Returns the current values for the tickers associated with
         *  this task. For methods which aren't trackable an empty
         *  array should be returned. For trackable methods the length
         *  of the array should correspond to what is returned by
         *  {@link TrackableMethod#getTickerCount()} for the executed
         *  method.
         *
         * @return the ticker values
         */
	public int[] getTickers() {
		return tickers;
	}

}
