/* ResolvableFault.java created 2008-02-19
 * 
 */

package org.signalml.method.mp5.remote;

import java.util.List;

/** ResolvableFault
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ResolvableFault {

	private String messageCode;
	private List<String> messageArguments;

	public String getMessageCode() {
		return messageCode;
	}
	
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public List<String> getMessageArguments() {
		return messageArguments;
	}
	
	public String[] getMessageArgumentsArray() {
		if( messageArguments == null ) {
			return new String[0];
		}
		String[] arr = new String[messageArguments.size()];
		messageArguments.toArray(arr);
		return arr;
	}

	public void setMessageArguments(List<String> messageArguments) {
		this.messageArguments = messageArguments;
	}
	
}
