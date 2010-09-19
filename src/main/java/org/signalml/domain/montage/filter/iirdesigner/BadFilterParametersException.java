/* BadFilterParametersException.java created 2010-09-19
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

/**
 * This exception class indicates that the filter designer cannot design the
 * filter for the given parameters.
 *
 * @author Piotr Szachewicz
 */
public class BadFilterParametersException extends Exception {

	public BadFilterParametersException(String problem){
		super(problem);
	}
	
}
