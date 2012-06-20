/* BadFilterParametersException.java created 2010-09-19
 *
 */

package org.signalml.math.iirdesigner;

/**
 * This exception class indicates that the filter designer cannot design the
 * filter for the given parameters.
 *
 * @author Piotr Szachewicz
 */
public class BadFilterParametersException extends Exception {

	/**
	 * Constructs a new exception with the specified detail message.
	 *
	 * @param problem the detail message
	 */
	public BadFilterParametersException(String problem) {
		super(problem);
	}

}