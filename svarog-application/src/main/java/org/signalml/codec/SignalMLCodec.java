/* SignalMLCodec.java created 2007-09-18
 * 
 */

package org.signalml.codec;

/** SignalMLCodec
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalMLCodec {

	String getSourceUID();
	
	String getFormatName();
	void setFormatName( String formatName );

	SignalMLCodecReader createReader() throws SignalMLCodecException;	
		
}
