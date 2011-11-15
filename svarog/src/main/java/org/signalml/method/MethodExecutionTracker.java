/* MethodExecutionTracker.java created 2007-12-05
 *
 */

package org.signalml.method;

/**
 *  This interface is implemented by classes used to control method execution and
 *  receive progress feedback.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MethodExecutionTracker {


	/**
         *  Returns true if the controlling code requests the method to abort computations.
	 *
	 * @return true if an abortion request is posted
	 */
	boolean isRequestingAbort();

	/**
         *  Returns true if the controlling code requests the method to suspend computations.
	 *
	 * @return true if an suspention request is posted
	 */
	boolean isRequestingSuspend();

	/**
	 *  Posts a task message, which may be displayed by any controling application.
	 *
	 *  <p>Typically this method will be called from within the {@link Method#compute(Object, MethodExecutionTracker)} method to
	 *  indicate the current stage or status of the ongoing computation.
	 *
	 * @param message the new message
	 */
	void setMessage(String message);

	/**
	 *  Retrieves the last message set by the computation code with the {@link #setMessage(String)}
	 *  method. Initially the Task has no message and null is returned
	 *
	 * @return the message or null if no message has been posted
	 */
	String getMessage();

	/**
         *  Returns the limits (maximum values) for the tickers associated with this task. For methods which
	 *  aren't trackable an empty array should be returned. For trackable methods the length of the array
	 *  should correspond to what is returned by {@link TrackableMethod#getTickerCount()} for the executed
	 *  method.
	 *
	 * @return the ticker limits
	 */
	int[] getTickerLimits();

	/**
         *  Sets the limits (maximum values) for the tickers associated with this task. The method
	 *  should generally throw IndexOutOfBoundsException if the method is not trackable or if
	 *  the given array is longer than the ticker count for the method.
	 *
	 * @param initial the array of ticker limits
	 */
	void setTickerLimits(int[] initial);


	/**
         *  Sets a single ticker limit. See {@link #setTickerLimits(int[])}.
	 *
	 * @param index the index of the ticker
	 * @param limit the new limit
	 */
	void setTickerLimit(int index, int limit);

	/**
         *  Returns the current values for the tickers associated with this task. For methods which
	 *  aren't trackable an empty array should be returned. For trackable methods the length of the array
	 *  should correspond to what is returned by {@link TrackableMethod#getTickerCount()} for the executed
	 *  method.
	 *
	 * @return the ticker values
	 */
	int[] getTickers();

	/**
         *  Resets all ticker values to 0.
	 */
	void resetTickers();

	/**
         *  Sets the current values for the tickers associated with this task. The method
	 *  should generally throw IndexOutOfBoundsException if the method is not trackable or if
	 *  the given array is longer than the ticker count for the method.
	 *
	 * @param current the array of ticker values
	 */
	void setTickers(int[] current);

	/**
         *  Sets a single ticker value. See {@link #setTickers(int[])}.
	 *
	 * @param index the index of the ticker
	 * @param value the new value
	 */
	void setTicker(int index, int value);

	/**
         *  Advances the given ticker by one.
	 *
	 * @param index the index of the ticker
	 */
	void tick(int index);

	/**
         *  Advances the given ticker by the given value
	 *
	 * @param index the index of the ticker
	 * @param step the increase
	 */
	void tick(int index, int step);

	/**
         *  Should return the expected number of seconds until given ticker is complete. This
	 *  should return <code>null</code> if the expected time is unknown or uncertain.
	 *
	 * @param index the index of the ticker
	 * @return expected time in seconds or <code>null</code> when unknown.
	 */
	Integer getExpectedSecondsUntilComplete(int index);

}
