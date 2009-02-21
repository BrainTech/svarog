/* SourceMontageEventListener.java created 2007-11-23
 * 
 */

package org.signalml.domain.montage;

import java.util.EventListener;

/** SourceMontageEventListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SourceMontageListener extends EventListener {

	void sourceMontageChannelAdded( SourceMontageEvent ev );

	void sourceMontageChannelRemoved( SourceMontageEvent ev );

	void sourceMontageChannelChanged( SourceMontageEvent ev );
	
}
