/* MontageSampleFilterType.java created 2008-02-01
 * 
 */

package org.signalml.domain.montage.filter;

import org.springframework.context.MessageSourceResolvable;

/** MontageSampleFilterType
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum SampleFilterType implements MessageSourceResolvable {

	TIME_DOMAIN,
	FFT
	
	;
	
	private final static Object[] ARGUMENTS = new Object[0];
	
	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return new String[] { "sampleFilterType." + this.toString() };
	}

	@Override
	public String getDefaultMessage() {
		return this.toString();
	}
	
}
