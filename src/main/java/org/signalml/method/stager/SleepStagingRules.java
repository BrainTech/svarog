/* SleepStagingRules.java created 2008-02-13
 * 
 */

package org.signalml.method.stager;

import org.springframework.context.MessageSourceResolvable;

/** SleepStagingRules
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum SleepStagingRules implements MessageSourceResolvable {

	RK,
	AASM
	
	;
	
	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "sleepStagingRules." + name() };
	}

	@Override
	public String getDefaultMessage() {
		return name();
	}	
	
}
