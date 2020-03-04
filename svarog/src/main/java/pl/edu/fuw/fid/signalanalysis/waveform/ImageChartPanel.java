package pl.edu.fuw.fid.signalanalysis.waveform;

import pl.edu.fuw.fid.signalanalysis.NonInteractiveChartPanel;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

/**
 * Chart panel for 2D time-frequency map (ImageChart).
 *
 * @author ptr@mimuw.edu.pl
 */
public class ImageChartPanel extends NonInteractiveChartPanel {

	private ImageChartPanelListener listener;

	public ImageChartPanel(JFreeChart chart) {
		super(chart);
	}

	public void setListener(ImageChartPanelListener listener) {
		this.listener = listener;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (listener == null) {
			return;
		}
		Point2D p = e.getPoint();
		Rectangle2D plotArea = getScreenDataArea();
		XYPlot plot = getChart().getXYPlot();
		if (plotArea.contains(p)) {
			double dx = (p.getX() - plotArea.getMinX()) / plotArea.getWidth();
			double dy = (p.getY() - plotArea.getMinY()) / plotArea.getHeight();
			listener.mouseMoved(dx, dy);
		} else {
			listener.mouseExited();
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (listener == null) {
			return;
		}
		listener.mouseExited();
	}

}
