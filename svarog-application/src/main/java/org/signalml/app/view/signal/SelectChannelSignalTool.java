/* SelectChannelSignalTool.java created 2007-10-04
 * 
 */

package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.signalml.app.util.IconUtils;
import org.signalml.domain.signal.SignalSelectionType;

/** SelectChannelSignalTool
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SelectChannelSignalTool extends SignalTool implements SelectionSignalTool {

	public Float startPosition;
	
	private SignalPlot plot;
	
	public SelectChannelSignalTool(SignalView signalView) {
		super(signalView);
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
	}
	
	@Override
	public SignalSelectionType getSelectionType() {
		return SignalSelectionType.CHANNEL;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		
		if( SwingUtilities.isLeftMouseButton(e) ) {

			Object source = e.getSource();
			if( !(source instanceof SignalPlot) ) {
				plot = null;
				return;
			}
			plot = (SignalPlot) source;

			startPosition = plot.toTimeSpace(e.getPoint());
			engaged = true;
			e.consume();
		}
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if( SwingUtilities.isLeftMouseButton(e) ) {		
			selectTo( e.getPoint() );
			startPosition = null;
			engaged = false;
			plot = null;
			e.consume();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if( SwingUtilities.isLeftMouseButton(e) ) {
			Point point = e.getPoint();
			selectTo(point);
			Rectangle r = new Rectangle(point.x, point.y, 1, 1);
	        ((SignalPlot)e.getSource()).scrollRectToVisible(r);
		}
	}

	private void selectTo(Point point) {
		if( startPosition != null ) {
			Float endPosition = plot.toTimeSpace(point);
			if( endPosition != null ) {
				if( startPosition.equals(endPosition) ) {
					signalView.clearSignalSelection();
				} else {
					Integer channel = plot.toChannelSpace(point);
					if( channel != null ) {
						signalView.setSignalSelection(plot,plot.getChannelSelection(startPosition, endPosition, channel));
					}
				}
			}
		}		
	}
	
}
