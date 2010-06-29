/* EvokedPotentialMethodConfigurer.java created 2008-01-12
 *
 */

package org.signalml.app.method.ep;

import java.awt.Window;

import org.apache.log4j.Logger;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodConfigurer;
import org.signalml.app.method.PresetEquippedMethodConfigurer;
import org.signalml.exception.SignalMLException;
import org.signalml.method.Method;

/** EvokedPotentialMethodConfigurer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialMethodConfigurer implements InitializingMethodConfigurer, PresetEquippedMethodConfigurer {

	protected static final Logger logger = Logger.getLogger(EvokedPotentialMethodConfigurer.class);

	private EvokedPotentialMethodDialog dialog;
	private PresetManager presetManager;
	private Window dialogParent;

	@Override
	public void initialize(ApplicationMethodManager manager) {
		dialogParent = manager.getDialogParent();
		dialog = new EvokedPotentialMethodDialog(manager.getMessageSource(), presetManager, dialogParent);
		dialog.setApplicationConfig(manager.getApplicationConfig());
	}

	@Override
	public boolean configure(Method method, Object methodDataObj) throws SignalMLException {

		EvokedPotentialApplicationData data = (EvokedPotentialApplicationData) methodDataObj;

		boolean dialogOk = dialog.showDialog(data, true);
		if (!dialogOk) {
			return false;
		}

		return true;

	}

	@Override
	public void setPresetManager(PresetManager presetManager) {
		this.presetManager = presetManager;
	}

}
