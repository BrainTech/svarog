/* BookToTagMethodConfigurer.java created 2007-11-02
 *
 */

package org.signalml.app.method.booktotag;

import java.awt.Window;
import org.apache.log4j.Logger;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodConfigurer;
import org.signalml.method.Method;
import org.signalml.method.booktotag.BookToTagData;
import org.signalml.plugin.export.SignalMLException;

/** BookToTagMethodConfigurer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookToTagMethodConfigurer implements InitializingMethodConfigurer {

	protected static final Logger logger = Logger.getLogger(BookToTagMethodConfigurer.class);

	private BookToTagMethodDialog dialog;
	private Window dialogParent;

	@Override
	public void initialize(ApplicationMethodManager manager) {
		dialogParent = manager.getDialogParent();
		dialog = new BookToTagMethodDialog(manager.getMessageSource(), dialogParent);
	}

	@Override
	public boolean configure(Method method, Object methodDataObj) throws SignalMLException {

		BookToTagData data = (BookToTagData) methodDataObj;

		boolean dialogOk = dialog.showDialog(data, true);
		if (!dialogOk) {
			return false;
		}

		return true;

	}

}
