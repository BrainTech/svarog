/* RawSignalSampleType.java created 2008-01-18
 * 
 */

package org.signalml.domain.signal.raw;

import org.springframework.context.MessageSourceResolvable;

/** RawSignalSampleType
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum RawSignalSampleType implements MessageSourceResolvable {

	DOUBLE( 64 ),
	FLOAT( 32 ),
	INT( 32 ),
	SHORT( 16 )
	
	;
	
	private int bitWidth;

	private RawSignalSampleType(int bitWidth) {
		this.bitWidth = bitWidth;
	}

	public int getBitWidth() {
		return bitWidth;
	}	
	
	public int getByteWidth() {
		return (int) Math.ceil( ((double) bitWidth) / 8 );
	}
	
	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "sampleType." + name() };
	}

	@Override
	public String getDefaultMessage() {
		return name();
	}
	
}
