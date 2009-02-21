/* BookAverageMethodConfigurer.java created 2007-11-02
 * 
 */

package org.signalml.app.method.bookaverage;

import java.awt.Window;
import org.apache.log4j.Logger;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodConfigurer;
import org.signalml.exception.SignalMLException;
import org.signalml.method.Method;
import org.signalml.method.bookaverage.BookAverageData;

/** BookAverageMethodConfigurer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookAverageMethodConfigurer implements InitializingMethodConfigurer {

	protected static final Logger logger = Logger.getLogger(BookAverageMethodConfigurer.class);
	
	private BookAverageMethodDialog dialog;
	private Window dialogParent;
		
	@Override
	public void initialize(ApplicationMethodManager manager) {		
		dialogParent = manager.getDialogParent();
		dialog = new BookAverageMethodDialog(manager.getMessageSource(), dialogParent);
	}

	@Override
	public boolean configure(Method method, Object methodDataObj) throws SignalMLException {
		
		BookAverageData data = (BookAverageData) methodDataObj;
				
		boolean dialogOk = dialog.showDialog(data, true);
		if( !dialogOk ) {
			return false;
		}
								
		return true;
		
	}

}
