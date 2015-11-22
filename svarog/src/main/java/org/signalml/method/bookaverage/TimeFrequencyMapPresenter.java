package org.signalml.method.bookaverage;

import java.awt.Window;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;
import org.signalml.app.view.book.wignermap.WignerMapImageProvider;
import org.signalml.app.view.book.wignermap.WignerMapPalette;

/**
 * @author ptr@mimuw.edu.pl
 */
public class TimeFrequencyMapPresenter {

	private final Window dialogParent;

	public TimeFrequencyMapPresenter(Window dialogParent) {
		this.dialogParent = dialogParent;
	}

	public void showResults(double[][] data, double freqMin, double freqMax, double timeRange) {
		int width = data.length;
		int height = data[0].length;
		WignerMapImageProvider mip = new WignerMapImageProvider();
		BufferedImage image = mip.getImage(data, width, height, WignerMapPalette.RAINBOW);

		ValueAxis ax = new NumberAxis("time (s)");
		ax.setRange(0, timeRange);
		ValueAxis ay = new NumberAxis("frequency (Hz)");
		ay.setRange(freqMin, freqMax);
		XYPlot plot = new XYPlot(new XYSeriesCollection(), ax, ay, null) {
			@Override
			public boolean isDomainZoomable() {
				return false;
			}
			@Override
			public boolean isRangeZoomable() {
				return false;
			}
		};
		plot.setBackgroundImage(image);
		plot.setBackgroundImageAlpha(1.0f);

		JDialog dialog = new JDialog(dialogParent, "Average time-frequency map");
		dialog.add(new ChartPanel(new JFreeChart(plot)));
		dialog.pack();
		dialog.setVisible(true);
	}

}
