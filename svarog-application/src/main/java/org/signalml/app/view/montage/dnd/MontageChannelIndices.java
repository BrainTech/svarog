/* SourceChannelIndices.java created 2008-01-04
 * 
 */

package org.signalml.app.view.montage.dnd;

/** SourceChannelIndices
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageChannelIndices {

	private int[] montageChannels;

	public MontageChannelIndices(int[] montageChannels) {
		if( montageChannels == null ) {
			throw new NullPointerException( "No montage channels" );
		}
		this.montageChannels = montageChannels;
	}

	public int[] getMontageChannels() {
		return montageChannels;
	}
		
}
