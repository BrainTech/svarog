/* BookAverageMethodConsumer.java created 2007-10-23
 *
 */

package org.signalml.app.method.bookaverage;

import org.signalml.app.method.MethodResultConsumer;
import org.signalml.method.Method;
import org.signalml.plugin.export.SignalMLException;

/** BookAverageMethodConsumer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookAverageMethodConsumer implements MethodResultConsumer {
	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {
		return false;
	}
}
