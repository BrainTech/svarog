/* SelectionSignalTool.java created 2007-12-07
 *
 */

package org.signalml.app.view.signal;

import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.SignalTool;

/** SelectionSignalTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SelectionSignalTool extends SignalTool {
	SignalSelectionType getSelectionType();
}
