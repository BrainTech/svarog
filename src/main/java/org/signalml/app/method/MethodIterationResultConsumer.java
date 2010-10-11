/* MethodIterationResultConsumer.java created 2007-12-06
 *
 */

package org.signalml.app.method;

import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.MethodIteratorData;
import org.signalml.method.iterator.MethodIteratorResult;
import org.signalml.plugin.export.SignalMLException;

/** MethodIterationResultConsumer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MethodIterationResultConsumer {

	void consumeIterationResult(IterableMethod method, MethodIteratorData data, MethodIteratorResult result) throws SignalMLException;

}
