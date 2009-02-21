/* MontageFocusSelector.java created 2007-11-24
 * 
 */

package org.signalml.app.action.selector;

import org.signalml.domain.montage.Montage;

/** MontageFocusSelector
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageFocusSelector extends SignalDocumentFocusSelector {

	Montage getActiveMontage();
	
}
