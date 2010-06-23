/* ZoomSignalTool.java created 2007-10-15
 * 
 */
package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import org.signalml.app.config.ZoomSignalSettings;
import org.signalml.app.util.IconUtils;

/** ZoomSignalTool
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ZoomSignalTool extends SignalTool {

	private SignalPlot plot;
	private ZoomSignalPlot zoomPlot;
	
	private ZoomSignalSettings settings;
	
	public ZoomSignalTool(SignalView signalView) {
		super(signalView);
		zoomPlot = new ZoomSignalPlot();
		settings = new ZoomSignalSettings();
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
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
						
			Point point = e.getPoint();
			int channel = plot.toChannelSpace(point);
			if( e.isControlDown() ) {
				Dimension zoomSize = settings.getZoomSize();
				int width = Math.min( 600, zoomSize.width * 2 );
				int height = Math.min( 600, zoomSize.height * 2 );
				Dimension size = new Dimension( width, height);
				zoomPlot.setPreferredSize(size);
				zoomPlot.setFactor(2*settings.getFactor());
			} else {
				zoomPlot.setPreferredSize(settings.getZoomSize());
				zoomPlot.setFactor(settings.getFactor());
			}
			zoomPlot.setParameters(plot, point, channel);
			showZoom(point);
			engaged = true;
			e.consume();
			
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if( SwingUtilities.isLeftMouseButton(e) ) {
			hideZoom();
			engaged = false;
			plot = null;
			e.consume();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {

		if( plot != null ) {
			if( SwingUtilities.isLeftMouseButton(e) ) {
				Point point = e.getPoint();
				Rectangle r = new Rectangle(point.x, point.y, 1, 1);
				((SignalPlot)e.getSource()).scrollRectToVisible(r);
				if( settings.isChannelSwitching() ) {
					int channel = plot.toChannelSpace(point);
					zoomPlot.setParameters(point, channel);				
				} else {
					zoomPlot.setFocusPoint(point);
				}
				positionZoom(point, null);
			}
		}
		
	}
	
	public ZoomSignalSettings getSettings() {
		return settings;
	}

	public void setSettings(ZoomSignalSettings settings) {
		if( settings == null ) {
			throw new NullPointerException( "No settings" );
		}
		this.settings = settings;
	}

	private void positionZoom(Point point, JLayeredPane layeredPane) {
		if( plot != null ) {
			if( layeredPane == null ) {
				layeredPane = plot.getRootPane().getLayeredPane();
			}
			Point location = SwingUtilities.convertPoint(plot, point, layeredPane);
			Dimension size = zoomPlot.getPreferredSize();
			zoomPlot.setBounds(location.x-(size.width/2), location.y-(size.height/2), size.width, size.height);
		}
	}
	
	private void showZoom(Point point) {
		if( plot != null ) {
			zoomPlot.setVisible(true);
			JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
			positionZoom(point, layeredPane);
			layeredPane.add(zoomPlot, new Integer(JLayeredPane.DRAG_LAYER));
		}
	}
	
	private void hideZoom() {
		if( plot != null ) {
			JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
			layeredPane.remove(zoomPlot);
			plot.getRootPane().repaint();
		}
	}
			
}
