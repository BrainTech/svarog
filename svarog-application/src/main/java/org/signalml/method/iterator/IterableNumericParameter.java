/* IterableNumericParameter.java created 2007-12-05
 * 
 */

package org.signalml.method.iterator;

/** IterableNumericParameter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface IterableNumericParameter extends IterableParameter {

	Comparable<? extends Number> getMinimum();
	
	Comparable<? extends Number> getMaximum();
	
	Number getStepSize();
	
}
