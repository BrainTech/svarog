/* VisualReferenceHorizontalBin.java created 2007-12-01
 * 
 */

package org.signalml.app.view.montage;

import java.awt.Dimension;
import java.awt.Point;

/** VisualReferenceHorizontalBin
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceHorizontalBin extends VisualReferenceBin {

	private Dimension cellSize;
	
	// this ignores max height
	
	@Override
	protected void validate() {

		int cnt = channels.size();
		if( cnt == 0 ) {
			size = new Dimension(0,0);
			valid = true;
			return;
		}
		
		cellSize = getCellSize();
		
		// make it square
		if( cellSize.width > cellSize.height ) {
			cellSize.height = cellSize.width;
		}
		else if( cellSize.height > cellSize.width ) {
			cellSize.width = cellSize.height;
		}
				
		// calculate size
		size = new Dimension( margin.left + margin.right + cnt*cellSize.width + (cnt-1)*hGap, HEADER_HEIGHT + margin.top + margin.bottom + cellSize.height );
				
		valid = true;
		
	}

	@Override
	protected void reposition() {
		
		int cnt = channels.size();
		if( cnt == 0 ) {
			positioned = true;
			return;
		}
		
		if( !valid ) {
			validate();
		}
		
		Point location = getLocation();
		
		if( location != null ) {
			
			int col = 0;
			int x,y;
		
			y = location.y + HEADER_HEIGHT + margin.top;
					
			for( VisualReferenceSourceChannel channel : channels ) {
				x = location.x + margin.left + col*(cellSize.width+hGap);
				channel.setLocation( new Point( x,y ) );
				col++;					
			}
			
			positioned = true;
			
		}
				
	}	

}
