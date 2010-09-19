/* BadFilterParametersException.java created 2010-09-19
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

/**
 * This exception provides information on specialistic mathematical functions
 * errors.
 *
 * @author Piotr Szachewicz
 */
public class SpecialMathException extends Exception {

	public SpecialMathException(String problem){
		super(problem);
	}
	
}
