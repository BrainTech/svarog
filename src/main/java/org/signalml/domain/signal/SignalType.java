/* SignalType.java created 2007-10-23
 * 
 */

package org.signalml.domain.signal;

import org.signalml.domain.signal.eeg.EegSignalTypeConfigurer;
import org.springframework.context.MessageSourceResolvable;

/** SignalType
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum SignalType implements MessageSourceResolvable {

	OTHER( new GenericSignalTypeConfigurer() ), 
	EEG_10_20( new EegSignalTypeConfigurer() )
	
	;
	
	private final static Object[] ARGUMENTS = new Object[0];
	
	private SignalTypeConfigurer configurer;
		
	private SignalType() {
		this.configurer = null;
	}
	
	private SignalType(SignalTypeConfigurer configurer) {
		this.configurer = configurer;
	}
	
	public SignalTypeConfigurer getConfigurer() {
		return configurer;
	}
	
	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return new String[] { "signalType." + this.toString() };
	}

	@Override
	public String getDefaultMessage() {
		return this.toString();
	}
		
}
