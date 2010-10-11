/* TaggingSignalTool.java created 2007-11-13
 *
 */

package org.signalml.app.view.signal;

import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.SignalTool;

/** TaggingSignalTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TaggingSignalTool extends SignalTool {
	SignalSelectionType getTagType();
}
