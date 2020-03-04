package pl.edu.fuw.fid.signalanalysis.waveform;

/**
 * Simple mouse listener for ImageChart(Panel).
 * Function mouseMoved will be called with 0 &le; dx, dy &lt; 1.
 *
 * @author ptr@mimuw.edu.pl
 */
public interface ImageChartPanelListener {

	public void mouseExited();

	public void mouseMoved(double dx, double dy);

}
