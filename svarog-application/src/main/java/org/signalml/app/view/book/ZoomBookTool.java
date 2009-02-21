/* ZoomBookTool.java created 2008-03-06
 * 
 */
package org.signalml.app.view.book;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import org.signalml.app.util.IconUtils;

/** ZoomBookTool
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ZoomBookTool extends BookTool {

	public static final double MIN_POSITION_RANGE = 0.01;
	public static final double MIN_FREQUENCY_RANGE = 0.01; 
	
	private Point dragStart = null;
	private BookPlot plot;
	private ZoomBookAreaPlot zoomAreaPlot;
	private boolean plotVisible = false;
	private boolean wasDragged = false;
	
	private boolean zoomOut = false;
	
	private boolean preserveRatio = true;
	
	public ZoomBookTool(BookView bookView) {
		super(bookView);
		zoomAreaPlot = new ZoomBookAreaPlot();
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
	}
	
	private void startZooming (MouseEvent e) {

		Object source = e.getSource();
		if( !(source instanceof BookPlot) ) {
			plot = null;
			return;
		}
		plot = (BookPlot) source;
		
		dragStart = e.getPoint();
		zoomAreaPlot.setFrame( null );
		engaged = true;
		e.consume();
	}
	
	private void finishZooming(MouseEvent e) {
		hidePlot();
		
		Point point = e.getPoint();
		
		if( wasDragged && !point.equals(dragStart) ) {
			dragZoom( point );
		} else {
			pointZoom( point );
		}
		
		zoomOut = false;
		dragStart = null;
		engaged = false;
		plot = null;
		e.consume();

	}
	
	@Override
	public void mousePressed(MouseEvent e) {			
		if( SwingUtilities.isLeftMouseButton(e) ) {

			if( e.isControlDown() ) {
				zoomOut = true;
			}
			
//			if (e.isShiftDown()) {
//				wasDragged = true;
//			}
			
			startZooming(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if( SwingUtilities.isLeftMouseButton(e) ) {
			finishZooming(e);
			wasDragged = false;
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		super.mouseWheelMoved(e);
		
		zoomOut = e.getWheelRotation() > 0 ? true : false;

		wasDragged = false;

		startZooming(e);
		finishZooming(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {

		if( plot != null ) {		
			if( SwingUtilities.isLeftMouseButton(e) ) {
				Point point = e.getPoint();
				measureTo(point);
				wasDragged = true;
			}
		}
		
	}

	private void showPlot() {
		if( plot != null ) {		
			if( !plotVisible ) {
				Rectangle mapRectangle = plot.getMapRectangle();
				if( mapRectangle == null ) {
					return;
				}
				JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
				Point location = SwingUtilities.convertPoint(plot, mapRectangle.getLocation(), layeredPane);
				zoomAreaPlot.setBounds(location.x, location.y, mapRectangle.width, mapRectangle.height);
				layeredPane.add(zoomAreaPlot, new Integer(JLayeredPane.DRAG_LAYER));
				plotVisible = true;
			}
		}
	}
	
	private void hidePlot() {
		if( plot != null ) {
			if( plotVisible ) {
				JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
				layeredPane.remove(zoomAreaPlot);
				plotVisible = false;
				plot.repaint();
			}
		}
	}
	
	private void measureTo(Point point) {
		if( plot != null ) {
			
			Rectangle mapRectangle = plot.getMapRectangle();
			if( mapRectangle == null ) {
				return;
			}
						
			// TODO consider setting bounds rather than internal frame - may be faster 
			zoomAreaPlot.setFrame( getFrame( mapRectangle, point ) );
			
			showPlot();
			
		}
	}
	
	private Rectangle getFrame( Rectangle mapRectangle, Point point ) {

		Rectangle frame = new Rectangle();
		frame.x = Math.min( dragStart.x, point.x );
		frame.y = Math.min( dragStart.y, point.y );
		
//		int width = point.x - dragStart.x;
//		int height = point.y - dragStart.y;
//		
//		if (width / mapRectangle.width < height / mapRectangle.height) {
//			frame.width = width;
//			frame.height = mapRectangle.height * (width / mapRectangle.width);
//		} else {
//			frame.width = mapRectangle.width * (width / mapRectangle.height);
//			frame.height = height;
//		}
		
		frame.width = Math.abs( point.x - dragStart.x );
		frame.height = Math.abs( point.y - dragStart.y );
		
		frame = mapRectangle.intersection(frame);			
		
		frame.translate(-mapRectangle.x, -mapRectangle.y);

		return frame;
	}
	
	private void pointZoom(Point point) {

		Rectangle mapRectangle = plot.getMapRectangle();
		if( mapRectangle == null ) {
			return;
		}
		if( !mapRectangle.contains(point) ) {
			return;
		}
		
		Point mapPoint = new Point( point );
		mapPoint.translate(-mapRectangle.x, -mapRectangle.y);
		
		double centerPosition = plot.toPosition(mapPoint.x);
		double centerFrequency = plot.toFrequency(mapPoint.y);
		
		double positionRange = plot.getMaxPosition() - plot.getMinPosition(); 
		double frequencyRange = plot.getMaxFrequency() - plot.getMinFrequency();

		
		if( zoomOut ) {
			
			positionRange *= 2;
			frequencyRange *= 2;
						
		} else {
			
			positionRange /= 2;
			frequencyRange /= 2;
			
		}
		
		zoom(centerPosition, centerFrequency, positionRange, frequencyRange);
						
	}

	private void dragZoom(Point point) {

		Rectangle mapRectangle = plot.getMapRectangle();
		if( mapRectangle == null ) {
			return;
		}
		
		if( zoomOut ) {

			plot.setZoom(
					0,
					plot.getSegment().getSegmentLength(),
					0,
					bookView.getDocument().getBook().getSamplingFrequency()/2
			);
			
		} else {

			Rectangle frame = getFrame(mapRectangle, point);
			
			double minPosition = plot.toPosition(frame.x);
			double maxPosition = plot.toPosition(frame.x+frame.width-1);
			
			double minFrequency = plot.toFrequency(frame.y+frame.height-1);
			double maxFrequency = plot.toFrequency(frame.y);
			
			double positionRange = maxPosition - minPosition;
			double frequencyRange = maxFrequency - minFrequency;
					
			zoom( minPosition + positionRange/2, minFrequency + frequencyRange/2, positionRange, frequencyRange );
			
		}
						
	}
	
	private void zoom( double centerPosition, double centerFrequency, double positionRange, double frequencyRange ) {
		
		double maxPosition = plot.getSegment().getSegmentLength();
		double maxFrequency = bookView.getDocument().getBook().getSamplingFrequency()/2;
		
		if( positionRange > maxPosition ) {
			positionRange = maxPosition;
		}
		else if( positionRange < MIN_POSITION_RANGE ) {
			positionRange = MIN_POSITION_RANGE;
		}
		
		if( frequencyRange > maxFrequency ) {
			frequencyRange = maxFrequency;
		}
		else if( frequencyRange < MIN_FREQUENCY_RANGE ) {
			frequencyRange = MIN_FREQUENCY_RANGE;
		}
		
		double newMinPosition = centerPosition - positionRange/2;
		if( newMinPosition < 0 ) {
			newMinPosition = 0;
		}
		double newMaxPosition = newMinPosition + positionRange;
		if( newMaxPosition > maxPosition ) {
			newMaxPosition = maxPosition;
			newMinPosition = newMaxPosition - positionRange;
		}
		
		double newMinFrequency = centerFrequency - frequencyRange/2;
		if( newMinFrequency < 0 ) {
			newMinFrequency = 0;
		}
		double newMaxFrequency = newMinFrequency + frequencyRange;
		if( newMaxFrequency > maxFrequency ) {
			newMaxFrequency = maxFrequency;
			newMinFrequency = newMaxFrequency - frequencyRange;
		}
		
		plot.setZoom(newMinPosition, newMaxPosition, newMinFrequency, newMaxFrequency);
				
	}

	public boolean isPreserveRatio() {
		return preserveRatio;
	}

	public void setPreserveRatio(boolean preserveRatio) {
		this.preserveRatio = preserveRatio;
	}

		
}
