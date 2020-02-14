package pl.edu.fuw.fid.signalanalysis.waveform;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.signalml.app.util.IconUtils;
import org.signalml.task.TaskStatus;
import pl.edu.fuw.fid.signalanalysis.AsyncStatus;

/**
 * Subclass of JFreeChart adapted to display time-frequency data
 * as an image laid over an empty chart.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ImageChart extends JFreeChart {

	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ImageChart.class);

	private final double minFrequency;

	private final ImageRenderer renderer;

	private final AtomicInteger lastTaskId;

	private final Image waitingImage;

	private Runnable onComputationFinished;

	public ImageChart(ImageRenderer renderer, double tMin, double tMax, double fMin, double fMax) {
		super(createPlot(tMin, tMax, fMin, fMax));
		this.minFrequency = fMin;
		this.renderer = renderer;
		this.lastTaskId = new AtomicInteger(0);
		this.waitingImage = IconUtils.getLargeTaskIcon(TaskStatus.ACTIVE_WAITING).getImage();
		removeLegend();
	}

	public void setMaxFrequency(double fMax, boolean logarithmic) {
		NumberAxis yAxis = createRangeAxis(minFrequency, fMax, logarithmic);
		getXYPlot().setRangeAxis(yAxis);
	}

	/**
	 * Set a callback to be run whenever asynchronous image computation completes.
	 * This can be used to refresh a panel including this chart.
	 *
	 * @param onComputationFinished callable, will be run in Swing thread
	 */
	public void setOnComputationFinished(Runnable onComputationFinished) {
		this.onComputationFinished = onComputationFinished;
	}

	@Override
	public void draw(Graphics2D g2, Rectangle2D chartArea, Point2D anchor, ChartRenderingInfo info) {
		super.draw(g2, chartArea, anchor, info);
		Rectangle2D area = info.getPlotInfo().getDataArea();

		NumberAxis xAxis = (NumberAxis) getXYPlot().getDomainAxis();
		NumberAxis yAxis = (NumberAxis) getXYPlot().getRangeAxis();

		BufferedImage oldImage = renderer.fetchImage(
			(int) area.getWidth(),
			(int) area.getHeight(),
			xAxis,
			yAxis
		);
		if (oldImage != null) {
			g2.drawImage(oldImage, (int) area.getMinX(), (int) area.getMinY(), null);
		} else {
			g2.drawImage(
				waitingImage,
				(int) area.getCenterX() - waitingImage.getWidth(null) / 2,
				(int) area.getCenterY() - waitingImage.getHeight(null) / 2,
				null
			);

			final long taskId = lastTaskId.incrementAndGet();
			Runnable r = () -> {
				try {
					AsyncStatus ass = new AsyncStatus() {
						@Override
						public boolean isCancelled() {
							return lastTaskId.get() > taskId;
						}
						@Override
						public void setProgress(double progress) {
							// nothing here
						}
					};
					final BufferedImage newImage = renderer.renderImage(
						(int) area.getWidth(),
						(int) area.getHeight(),
						xAxis,
						yAxis,
						ass
					);
					if (onComputationFinished != null && newImage != null && lastTaskId.get() == taskId) {
						SwingUtilities.invokeLater(() -> {
							onComputationFinished.run();
						});
					}
				} catch (Exception ex) {
					LOGGER.error(ex);
				}
			};
			new Thread(r).start();
		}
	}

	private static XYPlot createPlot(double tMin, double tMax, double fMin, double fMax) {
		NumberAxis xAxis = new NumberAxis();
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xAxis.setRange(tMin, tMax);
		NumberAxis yAxis = createRangeAxis(fMin, fMax, false);

		return new XYPlot(new DefaultXYDataset(), xAxis, yAxis, new XYBlockRenderer());
	}

	private static NumberAxis createRangeAxis(double fMin, double fMax, boolean logarithmic) {
		final String label = "frequency [Hz]";
		NumberAxis yAxis = logarithmic ? new LogarithmicAxis(label) : new NumberAxis(label);
		yAxis.setRange(fMin, fMax);
		yAxis.setFixedDimension(50);
		return yAxis;
	}
}
