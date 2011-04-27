/* AbstractTablePopupMenuProvider.java created 2007-11-17
 *
 */

package org.signalml.app.view;

import org.springframework.context.support.MessageSourceAccessor;

/** AbstractTablePopupMenuProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractTablePopupMenuProvider implements TablePopupMenuProvider {

	protected MessageSourceAccessor messageSource;

	public AbstractTablePopupMenuProvider(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

}
