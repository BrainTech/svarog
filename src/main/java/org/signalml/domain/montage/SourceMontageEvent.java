/* SourceMontageEvent.java created 2007-11-23
 * 
 */

package org.signalml.domain.montage;

import java.util.EventObject;

/** SourceMontageEvent
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int channel;
	
	public SourceMontageEvent(Object source, int channel) {
		super(source);
		this.channel = channel;
	}

	public int getChannel() {
		return channel;
	}

}
