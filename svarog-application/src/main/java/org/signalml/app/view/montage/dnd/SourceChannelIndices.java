/* SourceChannelIndices.java created 2008-01-04
 * 
 */

package org.signalml.app.view.montage.dnd;

/** SourceChannelIndices
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceChannelIndices {

	private int[] sourceChannels;

	public SourceChannelIndices(int[] sourceChannels) {
		if( sourceChannels == null ) {
			throw new NullPointerException( "No source channels" );
		}
		this.sourceChannels = sourceChannels;
	}

	public int[] getSourceChannels() {
		return sourceChannels;
	}
		
}
