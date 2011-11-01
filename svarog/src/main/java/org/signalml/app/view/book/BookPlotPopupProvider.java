/* BookPlotPopupProvider.java created 2008-02-23
 *
 */

package org.signalml.app.view.book;

import javax.swing.JPopupMenu;


/** BookPlotPopupProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookPlotPopupProvider {

	// FIXME is this class needed?
	@SuppressWarnings("unused")
	private BookPlot plot;
	private JPopupMenu plotPopupMenu;

	public BookPlotPopupProvider(BookPlot plot) {
		this.plot = plot;
	}

	public JPopupMenu getPlotPopupMenu() {

		if (plotPopupMenu == null) {
			plotPopupMenu = new JPopupMenu();
		}

		return plotPopupMenu;
	}
}
