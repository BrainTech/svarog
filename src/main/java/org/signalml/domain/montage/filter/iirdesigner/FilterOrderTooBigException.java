/* FilterOrderTooBigException.java created 2010-09-28
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

/**
 * This exception class indicates that the order of the filter designed for the
 * given parameters is too big - the filter may not work as intended.
 *
 * @author Piotr Szachewicz
 */
public class FilterOrderTooBigException extends BadFilterParametersException {

	public FilterOrderTooBigException(String problem) {
		super(problem);
	}

}
