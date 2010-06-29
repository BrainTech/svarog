/* IterableParameter.java created 2007-12-05
 *
 */

package org.signalml.method.iterator;

import org.signalml.method.InputDataException;
import org.springframework.context.MessageSourceResolvable;

/** IterableParameter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface IterableParameter extends MessageSourceResolvable {

	String getName();

	Class<?> getValueClass();

	Object getValue();
	void setValue(Object value) throws InputDataException;

	Object setIterationValue(Object startValue, Object endValue, int iteration, int totalIterations);

	Object getDefaultStartValue();
	Object getDefaultEndValue();

}
