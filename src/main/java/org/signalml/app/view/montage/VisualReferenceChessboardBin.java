/* VisualReferenceChessboardBin.java created 2007-12-01
 * 
 */

package org.signalml.app.view.montage;

import java.awt.Dimension;
import java.awt.Point;

/** VisualReferenceChessboardBin
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceChessboardBin extends VisualReferenceBin {

	private float overheadPerCell;
	private int vCnt;
	private Dimension cellSize;
	
	// this requires max height
	
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
		
		// calculate available height
		int avHeight = maxHeight - ( HEADER_HEIGHT + margin.top + margin.bottom );
		
		// calculate max cell count to fit vertically in the full row
		vCnt = (int) Math.floor( ((double) (avHeight + vGap)) / (cellSize.height+vGap) );
		if( vCnt > cnt ) {
			vCnt = cnt;
		}
		overheadPerCell = ((float) (avHeight - (vCnt*cellSize.height + (vCnt-1)*vGap))) / vCnt;
		
		int hCnt;
		if( vCnt > 1 ) {
			// calculate the number of row-pairs required
			int pCnt = (int) Math.ceil( ((double) cnt) / (2*vCnt-1) );
			
			// row-pair capacity
			int pCap = pCnt * (2*vCnt-1);
			
			// number of rows
			hCnt = pCnt * 2;
			
			// check if the last smaller row is needed
			if( (pCap - cnt) >= (vCnt - 1) ) {
				hCnt--;
			}
		} else {
			hCnt = cnt;
		}
		
		// calculate required size
		size = new Dimension( margin.left + margin.right + ((int) Math.ceil( hCnt*(((float) cellSize.width) ) + (hCnt-1)*(hGap + overheadPerCell) )), maxHeight );

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
		
			int row = 0;
			int col = 0;
			int colCap = vCnt;
			boolean even = false;
			float evenMargin = (cellSize.height + overheadPerCell + vGap) / 2;
			float x;
			float y;
			
			for( VisualReferenceSourceChannel channel : channels ) {
				x = location.x + margin.left + col*(cellSize.width+hGap+overheadPerCell) ;				
				y = location.y + HEADER_HEIGHT + margin.top + (2*row+1)*(overheadPerCell/2) + row * (cellSize.height+vGap);
				if( even ) {
					y += evenMargin;
				}
				channel.setLocation( new Point( (int) Math.round(x), (int) Math.round(y) ) );
				row++;
				if( row >= colCap ) {
					col++;
					row = 0;
					even = vCnt > 1 ? ( ( col % 2 ) == 1 ) : false;
					colCap = vCnt - ( even ? 1 : 0 );
				}
			}

			positioned = true;
			
		}
				
	}
	
	

}
