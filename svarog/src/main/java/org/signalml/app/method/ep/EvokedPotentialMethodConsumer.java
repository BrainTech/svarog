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
	private ApplicationMethodManager appManager;

	@Override
	public void initialize(ApplicationMethodManager manager) {
		appManager = manager;
	}

	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {
		dialog = new EvokedPotentialResultDialog(appManager.getDialogParent(), false);
		dialog.setFileChooser(appManager.getFileChooser());
		return dialog.showDialog(methodResult, true);

	}

}
