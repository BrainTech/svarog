/* ExampleMethodConsumer.java created 2007-10-23
 * 
 */

package org.signalml.app.method.example;

import javax.swing.JOptionPane;

import org.signalml.app.method.MethodResultConsumer;
import org.signalml.exception.SignalMLException;
import org.signalml.method.Method;
import org.signalml.method.example.ExampleResult;
import org.springframework.context.support.MessageSourceAccessor;

/** ExampleMethodConsumer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExampleMethodConsumer implements MethodResultConsumer {

	private MessageSourceAccessor messageSource;
	
	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {
		 JOptionPane.showMessageDialog(null, messageSource.getMessage("exampleMethod.result", new Object[] { ((ExampleResult) methodResult).getResult() } ) );
		 return false;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}
	
}
