/* XYShapeHighlightingRenderer.java created 2007-12-18
 * 
 */

package org.signalml.app.view.roc;

import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/** XYShapeHighlightingRenderer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class XYShapeHighlightingRenderer extends XYLineAndShapeRenderer {

	private static final long serialVersionUID = 1L;

	private int mainSeries;
	
	private int highlightedSeries = -1;
	private int highlightedItem = -1;
		
	public XYShapeHighlightingRenderer(boolean lines, boolean shapes, int mainSeries) {
		super(lines, shapes);
		this.mainSeries = mainSeries;
	}

	public int getHighlightedSeries() {
		return highlightedSeries;
	}

	public void setHighlightedSeries(int highlightedSeries) {
		if( this.highlightedSeries != highlightedSeries ) {
			this.highlightedSeries = highlightedSeries;
			notifyListeners(new RendererChangeEvent(this));
		}
	}

	public int getHighlightedItem() {
		return highlightedItem;
	}

	public void setHighlightedItem(int highlightedItem) {
		if( this.highlightedItem != highlightedItem ) {
			this.highlightedItem = highlightedItem;
			notifyListeners(new RendererChangeEvent(this));
		}
	}
	
	public void setHighlight(int highlightedSeries, int highlightedItem) {
		if( this.highlightedSeries != highlightedSeries || this.highlightedItem != highlightedItem ) {
			this.highlightedItem = highlightedItem;
			this.highlightedSeries = highlightedSeries;
			notifyListeners(new RendererChangeEvent(this));			
		}		
	}

	public void clearHighlight() {
		if( this.highlightedSeries != -1 || this.highlightedItem != -1 ) {
			this.highlightedItem = -1;
			this.highlightedSeries = -1;
			notifyListeners(new RendererChangeEvent(this));			
		}		
	}
	
	@Override
	public boolean getItemShapeVisible(int series, int item) {
		if( series == mainSeries ) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean getItemShapeFilled(int series, int item) {
		if( highlightedSeries >= 0 && highlightedItem >= 0 && series == highlightedSeries && item == highlightedItem ) {
			return true;
		}
		return false;
	}
	
}
