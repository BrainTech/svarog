/* EvokedPotentialMethodConsumer.java created 2008-01-12
 *
 */

package org.signalml.app.method.ep;

import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodResultConsumer;
import org.signalml.method.Method;
import org.signalml.plugin.export.SignalMLException;

/** EvokedPotentialMethodConsumer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialMethodConsumer implements InitializingMethodResultConsumer {

	private EvokedPotentialResultDialog dialog;

	@Override
	public void initialize(ApplicationMethodManager manager) {
		dialog = new EvokedPotentialResultDialog(manager.getDialogParent(), true);
		dialog.setFileChooser(manager.getFileChooser());
	}

	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {

		return dialog.showDialog(methodResult, true);

	}

}
