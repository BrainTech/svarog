package pl.edu.fuw.fid.signalanalysis;

import java.awt.image.BufferedImage;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ImageChart extends XYChart<Number, Number> {

	private final ImageView imageView = new ImageView();
	private Task<Image> lastTask;
	private volatile long lastTaskId = 0;
	private final ImageRenderer renderer;

	public ImageChart(Axis<Number> xAxis, Axis<Number> yAxis, ImageRenderer renderer) {
		super(xAxis, yAxis);
		this.renderer = renderer;
		setData(FXCollections.<Series<Number,Number>>emptyObservableList());
		getPlotChildren().add(this.imageView);
		this.imageView.relocate(0, 0);
	}

	public void refreshChartImage() {
		final int width = (int) getXAxis().getWidth();
		final int height = (int) getYAxis().getHeight();
		final double xMin = this.getXAxis().getValueForDisplay(0).doubleValue();
		final double xMax = this.getXAxis().getValueForDisplay(width).doubleValue();
		final double yMin = this.getYAxis().getValueForDisplay(0).doubleValue();
		final double yMax = this.getYAxis().getValueForDisplay(height).doubleValue();

		if (width > 0 && height > 0) {
			final long id = ++lastTaskId;
			if (lastTask != null) {
				lastTask.cancel(true);
			}
			final Task<Image> task = new Task<Image>() {
				@Override
				protected Image call() throws Exception {
					BufferedImage image = renderer.renderImage(width, height, xMin, xMax, yMin, yMax, new ImageRendererStatus(this));
					return (image == null) ? null : SwingFXUtils.toFXImage(image, null);
				}
			};
			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					if (lastTaskId == id) {
						Image image = task.getValue();
						if (image != null) {
							imageView.setImage(image);
							startFadeTransition(1.0);
						}
					}
				}
			});
			lastTask = task;
			startFadeTransition(0.0);
			new Thread(task).start();
		}
	}

	private void startFadeTransition(double finalOpacity) {
		FadeTransition fade = new FadeTransition(Duration.millis(500), imageView);
		fade.setToValue(finalOpacity);
		fade.play();
	}

	@Override
	protected void dataItemAdded(Series series, int itemIndex, Data item) {
		throw new UnsupportedOperationException("this operation is not supported");
	}

	@Override
	protected void dataItemRemoved(Data item, Series series) {
		throw new UnsupportedOperationException("this operation is not supported");
	}

	@Override
	protected void layoutPlotChildren() {
		refreshChartImage();
	}

	@Override
	protected void dataItemChanged(Data item) {
		throw new UnsupportedOperationException("this operation is not supported");
	}

	@Override
	protected void seriesAdded(Series series, int seriesIndex) {
		throw new UnsupportedOperationException("this operation is not supported");
	}

	@Override
	protected void seriesRemoved(Series series) {
		throw new UnsupportedOperationException("this operation is not supported");
	}

}
