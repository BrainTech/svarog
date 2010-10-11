/* DocumentFocusSelector.java created 2007-11-17
 *
 */

package org.signalml.app.action.selector;

import org.signalml.plugin.export.signal.Document;

/** DocumentFocusSelector
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface DocumentFocusSelector extends ActionFocusSelector {

	Document getActiveDocument();

}
