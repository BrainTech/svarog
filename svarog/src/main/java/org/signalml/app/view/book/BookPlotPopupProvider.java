/* BookPlotPopupProvider.java created 2008-02-23
 *
 */

package org.signalml.app.view.book;

import javax.swing.JPopupMenu;

import org.springframework.context.support.MessageSourceAccessor;

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

	private MessageSourceAccessor messageSource;

	public BookPlotPopupProvider(BookPlot plot) {
		this.plot = plot;
	}

	public JPopupMenu getPlotPopupMenu() {

		if (plotPopupMenu == null) {
			plotPopupMenu = new JPopupMenu();
		}

		return plotPopupMenu;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

}
