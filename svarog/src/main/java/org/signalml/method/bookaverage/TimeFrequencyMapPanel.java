package org.signalml.method.bookaverage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;
import org.signalml.app.view.book.wignermap.WignerMapImageProvider;
import org.signalml.app.view.book.wignermap.WignerMapPalette;
import org.signalml.app.view.book.wignermap.WignerMapPaletteComboBoxCellRenderer;
import org.signalml.app.view.book.wignermap.WignerMapScaleComboBoxCellRenderer;
import org.signalml.domain.book.WignerMapScaleType;

/**
 * @author ptr@mimuw.edu.pl
 */
public class TimeFrequencyMapPanel extends JPanel {

	private final XYPlot plot;
	private final int width, height;
	private final double[][] normed, scaled;
	private final JComboBox paletteComboBox;
	private final JComboBox scaleComboBox;

	public TimeFrequencyMapPanel(double[][] data, double freqMin, double freqMax, double timeRange) {
		super(new BorderLayout());
		ItemListener refresh = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				refreshImage();
			}
		};

		this.width = data.length;
		this.height = data[0].length;
		this.normed = new double[width][height];
		this.scaled = new double[width][height];
		this.scaleComboBox = createScaleComboBox(refresh);
		this.paletteComboBox = createPaletteComboBox(refresh);
		this.plot = createPlot(freqMin, freqMax, timeRange);

		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int ix=0; ix<width; ++ix) for (int iy=0; iy<height; ++iy) {
			min = Math.min(min, data[ix][iy]);
			max = Math.max(max, data[ix][iy]);
		}
		double range = max - min;
		for (int ix=0; ix<width; ++ix) for (int iy=0; iy<height; ++iy) {
			normed[ix][iy] = (data[ix][iy] - min) / range;
		}

		refreshImage();
		ChartPanel chartPanel = new ChartPanel(new JFreeChart(plot));
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		topPanel.add(scaleComboBox);
		topPanel.add(paletteComboBox);
		this.add(chartPanel, BorderLayout.CENTER);
		this.add(topPanel, BorderLayout.PAGE_START);
	}

	private static XYPlot createPlot(double freqMin, double freqMax, double timeRange) {
		ValueAxis ax = new NumberAxis("time (s)");
		ax.setRange(0, timeRange);
		ValueAxis ay = new NumberAxis("frequency (Hz)");
		ay.setRange(freqMin, freqMax);
		XYPlot result = new XYPlot(new XYSeriesCollection(), ax, ay, null) {
			@Override
			public boolean isDomainZoomable() {
				return false;
			}
			@Override
			public boolean isRangeZoomable() {
				return false;
			}
		};
		result.setBackgroundImageAlpha(1.0f);
		return result;
	}

	private static JComboBox createPaletteComboBox(ItemListener itemListener) {
		JComboBox result = new JComboBox(WignerMapPalette.values());
		result.setRenderer(new WignerMapPaletteComboBoxCellRenderer());
		result.addItemListener(itemListener);
		return result;
	}

	private static JComboBox createScaleComboBox(ItemListener itemListener) {
		JComboBox result = new JComboBox(WignerMapScaleType.values());
		result.setRenderer(new WignerMapScaleComboBoxCellRenderer());
		result.addItemListener(itemListener);
		return result;
	}

	private void refreshImage() {
		final WignerMapScaleType scale = (WignerMapScaleType) scaleComboBox.getSelectedItem();
		final WignerMapPalette palette = (WignerMapPalette) paletteComboBox.getSelectedItem();
		WignerMapImageProvider mip = new WignerMapImageProvider();

		if (scale == WignerMapScaleType.SQRT) {
			for (int ix=0; ix<width; ++ix) for (int iy=0; iy<height; ++iy) {
				scaled[ix][iy] = Math.sqrt(normed[ix][iy]);
			}
		} else if (scale == WignerMapScaleType.LOG) {
			double log2 = Math.log(2.0);
			for (int ix=0; ix<width; ++ix) for (int iy=0; iy<height; ++iy) {
				scaled[ix][iy] = Math.log(1.0 + normed[ix][iy]) / log2;
			}
		} else {
			for (int ix=0; ix<width; ++ix) for (int iy=0; iy<height; ++iy) {
				scaled[ix][iy] = normed[ix][iy];
			}
		}

		BufferedImage image = mip.getImage(scaled, width, height, palette);
		plot.setBackgroundImage(image);
	}

}
