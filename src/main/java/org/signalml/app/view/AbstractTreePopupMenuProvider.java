/* AbstractTreePopupMenuProvider.java created 2007-11-17
 *
 */

package org.signalml.app.view;

import org.springframework.context.support.MessageSourceAccessor;

/** AbstractTreePopupMenuProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractTreePopupMenuProvider implements TreePopupMenuProvider {

	protected MessageSourceAccessor messageSource;

	public AbstractTreePopupMenuProvider(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

}
