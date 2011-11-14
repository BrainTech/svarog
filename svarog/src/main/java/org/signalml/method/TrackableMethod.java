/* TrackableMethod.java created 2007-10-18
 *
 */

package org.signalml.method;


/**
 *  This interface is to be implemented by those {@link Method methods} which support progress
 *  monitoring. The method's {@link Method#compute(Object, MethodExecutionTracker)} method
 *  should call appropriate progress monitoring method on its {@link MethodExecutionTracker} object.
 *  Note that this is possible even if the method doesn't implement the TrackableMethod interface, this
 *  however may lead to errors and the controling application is not required to display
 *  such information unless TrackableMethod is implemented.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TrackableMethod {

	/**
         *  Returns the number of tickers this method requires.
	 *
	 * @return the ticker count
	 */
	int getTickerCount();

	/**
         *  Returns the label for the given ticker. May use provided SvarogI18n for
	 *  internationalization.
	 *
	 * @param ticker the number of the ticker
	 * @return the label for the ticker
	 */
	String getTickerLabel(int ticker);

}
