/* SignalMLCodecManagerListener.java created 2008-01-08
 * 
 */

package org.signalml.codec;

import java.util.EventListener;

/** SignalMLCodecManagerListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalMLCodecManagerListener extends EventListener {

	void codecAdded( SignalMLCodecManagerEvent ev );

	void codecRemoved( SignalMLCodecManagerEvent ev );
	
	void codecsChanged( SignalMLCodecManagerEvent ev );
		
}
