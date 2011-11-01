/* ExampleMethodConsumer.java created 2007-10-23
 *
 */

package org.signalml.app.method.example;

import static org.signalml.app.SvarogApplication._;
import javax.swing.JOptionPane;

import org.signalml.app.method.MethodResultConsumer;
import org.signalml.method.Method;
import org.signalml.method.example.ExampleResult;
import org.signalml.plugin.export.SignalMLException;

/** ExampleMethodConsumer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExampleMethodConsumer implements MethodResultConsumer {
	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {
		JOptionPane.showMessageDialog(null, java.text.MessageFormat.format(_("The meaning of life turns out to be {0}"), new Object[] { ((ExampleResult) methodResult).getResult() }));
		return false;
	}
}
