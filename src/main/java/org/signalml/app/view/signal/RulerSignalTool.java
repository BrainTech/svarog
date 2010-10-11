/* RulerSignalTool.java created 2007-10-05
 *
 */
package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.signal.AbstractSignalTool;

/** RulerSignalTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RulerSignalTool extends AbstractSignalTool {

	private Point dragStart = null;
	private SignalPlot plot;
	private RulerMeasurmentPlot measurmentPlot;
	private boolean measurmentVisible = false;

	public RulerSignalTool(SignalView signalView) {
		super(signalView);
		measurmentPlot = new RulerMeasurmentPlot();
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {

			Object source = e.getSource();
			if (!(source instanceof SignalPlot)) {
				plot = null;
				return;
			}
			plot = (SignalPlot) source;

			dragStart = e.getPoint();
			Point origin = plot.getViewport().getViewPosition();
			measurmentPlot.setStartParameters(dragStart, origin);
			setEngaged(true);
			e.consume();

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			dragStart = null;
			hideMeasurment();
			setEngaged(false);
			plot = null;
			e.consume();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (plot != null) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point point = e.getPoint();
				Rectangle r = new Rectangle(point.x, point.y, 1, 1);
				((SignalPlot)e.getSource()).scrollRectToVisible(r);
				measureTo(point);
			}
		}

	}

	private void showMeasurment() {
		if (plot != null) {
			if (!measurmentVisible) {
				Dimension size = plot.getViewport().getExtentSize();
				JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
				Point location = SwingUtilities.convertPoint(plot.getViewport(), new Point(0,0), layeredPane);
				measurmentPlot.setBounds(location.x, location.y, size.width, size.height);
				layeredPane.add(measurmentPlot, new Integer(JLayeredPane.DRAG_LAYER));
				measurmentVisible = true;
			}
		}
	}

	private void hideMeasurment() {
		if (plot != null) {
			if (measurmentVisible) {
				JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
				layeredPane.remove(measurmentPlot);
				measurmentVisible = false;
				plot.repaint();
			}
		}
	}

	private void measureTo(Point point) {
		if (plot != null) {
			Dimension size = plot.getSize();
			Point corrPoint = new Point(
			        Math.max(0, Math.min(size.width, point.x)),
			        Math.max(0, Math.min(size.height, point.y))
			);
			Point origin = plot.getViewport().getViewPosition();
			Point pixelSize = new Point(
			        Math.abs(corrPoint.x - dragStart.x),
			        Math.abs(corrPoint.y - dragStart.y)
			);
			Point2D signalSize = plot.toSignalSpace(pixelSize);
			measurmentPlot.setEndParameters(
			        corrPoint,
			        (float)(Math.round(signalSize.getX()*1000F) / 1000F),
			        (float)(Math.round(signalSize.getY()*1000F) / 1000F),
			        origin
			);
			showMeasurment();
		}
	}

}
