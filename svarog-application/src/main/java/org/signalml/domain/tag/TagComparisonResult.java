/* TagComparisonResult.java created 2007-11-13
 * 
 */

package org.signalml.domain.tag;

import java.io.IOException;
import java.io.Writer;

import org.signalml.app.model.WriterExportableTable;

/** TagComparisonResult
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagComparisonResult implements WriterExportableTable {
	
	private TagStatistic topStatistic;
	private TagStatistic bottomStatistic;
	
	// arr[x+1][y+1] -> tag x in top tag is covered by tag in bottom tag
	// arr[x][0] -> tag x in top tag has no tag in bottom tag 
	// arr[0][y] -> tag y in bottom tag has no tag in top tag
	private float[][] styleOverlayMatrix;
	
	public TagComparisonResult(TagStyle[] topTagStyles, TagStyle[] bottomTagStyles, float topTotalLength, float bottomTotalLength) {

		topStatistic = new TagStatistic(topTagStyles, topTotalLength);
		bottomStatistic = new TagStatistic(bottomTagStyles, bottomTotalLength);
		
		styleOverlayMatrix = new float[topTagStyles.length+1][bottomTagStyles.length+1];
		
	}
		
	public TagStatistic getTopStatistic() {
		return topStatistic;
	}

	public TagStatistic getBottomStatistic() {
		return bottomStatistic;
	}

	public int getTopStyleCount() {
		return topStatistic.getStyleCount();
	}
	
	public TagStyle getTopStyleAt( int index ) {
		return topStatistic.getStyleAt(index);
	}
	
	public int getBottomStyleCount() {
		return bottomStatistic.getStyleCount();
	}
	
	public TagStyle getBottomStyleAt( int index ) {
		return bottomStatistic.getStyleAt(index);
	}
	
	public int indexOfTopStyle( TagStyle style ) {
		return topStatistic.indexOf(style);
	}
	
	public int indexOfBottomStyle( TagStyle style ) {
		return bottomStatistic.indexOf(style);
	}
	
	public void addBottomStyleTime(int index, float time) {
		bottomStatistic.addStyleTime(index, time);
	}

	public void addBottomStyleTime(TagStyle style, float time) {
		bottomStatistic.addStyleTime(style, time);
	}

	public float getBottomStyleTime(int index) {
		return bottomStatistic.getStyleTime(index);
	}

	public float getBottomStyleTime(TagStyle style) {
		return bottomStatistic.getStyleTime(style);
	}

	public void setBottomStyleTime(int index, float time) {
		bottomStatistic.setStyleTime(index, time);
	}

	public void setBottomStyleTime(TagStyle style, float time) {
		bottomStatistic.setStyleTime(style, time);
	}
	
	public void addTopStyleTime(int index, float time) {
		topStatistic.addStyleTime(index, time);
	}

	public void addTopStyleTime(TagStyle style, float time) {
		topStatistic.addStyleTime(style, time);
	}

	public float getTopStyleTime(int index) {
		return topStatistic.getStyleTime(index);
	}

	public float getTopStyleTime(TagStyle style) {
		return topStatistic.getStyleTime(style);
	}

	public void setTopStyleTime(int index, float time) {
		topStatistic.setStyleTime(index, time);
	}

	public void setTopStyleTime(TagStyle style, float time) {
		topStatistic.setStyleTime(style, time);
	}

	// -1 for none
	public void addStyleOverlay( int topIndex, int bottomIndex, float time ) {
		styleOverlayMatrix[topIndex+1][bottomIndex+1] += time;
	}
	
	// null for none
	public void addStyleOverlay( TagStyle topStyle, TagStyle bottomStyle, float time ) {
		
		int topIndex;
		int bottomIndex;
		
		if( topStyle == null ) {
			topIndex = -1;
		} else {
			topIndex = topStatistic.indexOf(topStyle);
			if( topIndex < 0 ) {
				throw new IllegalArgumentException("No such style [" + topStyle.toString() + "] in top tag" );
			}
		}
		
		if( bottomStyle == null ) {
			bottomIndex = -1;
		} else {
			bottomIndex = bottomStatistic.indexOf(bottomStyle);
			if( bottomIndex < 0 ) {
				throw new IllegalArgumentException("No such style [" + bottomStyle.toString() + "] in bottom tag" );
			}
		}
		
		addStyleOverlay(topIndex, bottomIndex, time);
		
	}
	
	// -1 for none
	public float getStyleOverlay( int topIndex, int bottomIndex ) {
		return styleOverlayMatrix[topIndex+1][bottomIndex+1];
	}

	// null for none
	public float getStyleOverlay( TagStyle topStyle, TagStyle bottomStyle ) {
		
		int topIndex;
		int bottomIndex;
		
		if( topStyle == null ) {
			topIndex = -1;
		} else {
			topIndex = topStatistic.indexOf(topStyle);
			if( topIndex < 0 ) {
				throw new IllegalArgumentException("No such style [" + topStyle.toString() + "] in top tag" );
			}
		}
		
		if( bottomStyle == null ) {
			bottomIndex = -1;
		} else {
			bottomIndex = bottomStatistic.indexOf(bottomStyle);
			if( bottomIndex < 0 ) {
				throw new IllegalArgumentException("No such style [" + bottomStyle.toString() + "] in bottom tag" );
			}
		}
		
		return getStyleOverlay(topIndex, bottomIndex);
		
	}
	
	// -1 for none
	public void setStyleOverlay( int topIndex, int bottomIndex, float time ) {
		styleOverlayMatrix[topIndex+1][bottomIndex+1] = time;
	}
		
	// null for none
	public void setStyleOverlay( TagStyle topStyle, TagStyle bottomStyle, float time ) {
		
		int topIndex;
		int bottomIndex;
		
		if( topStyle == null ) {
			topIndex = -1;
		} else {
			topIndex = topStatistic.indexOf(topStyle);
			if( topIndex < 0 ) {
				throw new IllegalArgumentException("No such style [" + topStyle.toString() + "] in top tag" );
			}
		}
		
		if( bottomStyle == null ) {
			bottomIndex = -1;
		} else {
			bottomIndex = bottomStatistic.indexOf(bottomStyle);
			if( bottomIndex < 0 ) {
				throw new IllegalArgumentException("No such style [" + bottomStyle.toString() + "] in bottom tag" );
			}
		}
		
		setStyleOverlay(topIndex, bottomIndex, time);
		
	}

	@Override
	public void export(Writer writer, String columnSeparator, String rowSeparator, Object userObject) throws IOException {
		
		boolean exportPercent = false;
		if( userObject != null && (userObject instanceof Boolean) ) {
			exportPercent = ((Boolean) userObject).booleanValue();
		}
		
		int topCnt = topStatistic.getStyleCount();
		int bottomCnt = bottomStatistic.getStyleCount();
		int i,e;
		float divider;
		
		writer.append(columnSeparator);
		writer.append("(none)");
		for( i=0; i<bottomCnt; i++ ) {
			writer.append(columnSeparator);
			writer.append(bottomStatistic.getStyleAt(i).getName());
		}
		writer.append(rowSeparator);
		
		for( i=0; i<topCnt+1; i++ ) {
			if( i == 0 ) {
				writer.append("(none)");
			} else {
				writer.append(topStatistic.getStyleAt(i-1).getName());
			}
			for( e=0; e<bottomCnt+1; e++ ) {
				writer.append(columnSeparator);
				if( exportPercent ) {
					divider = topStatistic.getStyleTime(i-1);
					if( divider == 0 ) {
						writer.append("-");
					} else {
						writer.append( Float.toString( 100 * ( styleOverlayMatrix[i][e] / divider ) ) );
					}					
				} else {
					writer.append( Float.toString( styleOverlayMatrix[i][e] ) );
				}
			}
			writer.append(rowSeparator);
		}
				
	}
	
}
