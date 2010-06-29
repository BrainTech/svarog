/* BookAverageMethodConsumer.java created 2007-10-23
 *
 */

package org.signalml.app.method.bookaverage;

import org.signalml.app.method.MethodResultConsumer;
import org.signalml.exception.SignalMLException;
import org.signalml.method.Method;
import org.springframework.context.support.MessageSourceAccessor;

/** BookAverageMethodConsumer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookAverageMethodConsumer implements MethodResultConsumer {

	private MessageSourceAccessor messageSource;

	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {
		return false;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

}
