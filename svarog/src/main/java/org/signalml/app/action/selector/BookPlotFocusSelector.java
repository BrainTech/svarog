/* BookPlotFocusSelector.java created 2008-02-23
 *
 */

package org.signalml.app.action.selector;

import org.signalml.app.view.book.BookPlot;

/** BookPlotFocusSelector
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface BookPlotFocusSelector extends ActionFocusSelector {

	BookPlot getActiveBookPlot();

}
