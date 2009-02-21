/* SourceMontageChannelsDataFlavor.java created 2008-01-04
 * 
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;

/** SourceMontageChannelsDataFlavor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageChannelsDataFlavor extends DataFlavor {

	public SourceMontageChannelsDataFlavor() {
		super( SourceChannelIndices.class, "sourceChannels" );
	}
		
}
