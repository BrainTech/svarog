/* SignalDocumentFocusSelector.java created 2007-11-17
 *
 */

package org.signalml.app.action.selector;

import org.signalml.app.document.signal.SignalDocument;

/** SignalDocumentFocusSelector
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalDocumentFocusSelector extends DocumentFocusSelector {

	SignalDocument getActiveSignalDocument();

}
