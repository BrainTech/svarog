package pl.edu.fuw.fid.signalanalysis.waveform;

import java.awt.image.BufferedImage;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ImageChart extends XYChart<Number, Number> {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ImageChart.class);

	private final ImageView imageView = new ImageView();
	private final ProgressIndicator waiting = new ProgressIndicator();
	private Task<Image> lastTask;
	private volatile long lastTaskId = 0;
	private final ImageRenderer renderer;

	public ImageChart(Axis<Number> xAxis, Axis<Number> yAxis, ImageRenderer renderer) {
		super(xAxis, yAxis);
		this.renderer = renderer;
		setData(FXCollections.<Series<Number,Number>>emptyObservableList());
		getPlotChildren().add(this.imageView);
		this.imageView.relocate(0, 0);
		waiting.setMaxWidth(100);
		waiting.setMaxHeight(100);
		getPlotChildren().add(this.waiting);
	}

	// must be called from JavaFX thread
	public void hideImage() {
		imageView.setOpacity(0.0);
	}

	// must be called from JavaFX thread
	public void refreshChartImage() {
		final int width = (int) getXAxis().getWidth();
		final int height = (int) getYAxis().getHeight();

		if (width > 0 && height > 0) {
			final long id = ++lastTaskId;
			if (lastTask != null) {
				lastTask.cancel(true);
			}
			final Task<Image> task = new Task<Image>() {
				@Override
				protected Image call() throws Exception {
					try {
						BufferedImage image = renderer.renderImage(getXAxis(), getYAxis(), new ImageRendererStatus(this, waiting.progressProperty()));
						return (image == null) ? null : SwingFXUtils.toFXImage(image, null);
					} catch (Exception ex) {
						logger.warn(ex);
						throw ex;
					}
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
							waiting.setVisible(false);
						}
					}
				}
			});
			lastTask = task;
			startFadeTransition(0.0);
			waiting.relocate(0.5*width, 0.5*height);
			waiting.setVisible(true);
			new Thread(task).start();
		}
	}

	public TimeFrequency getTimeFrequency(int x, int y) {
		return renderer.getTimeFrequency(x, y);
	}

	public ProgressIndicator getProgressIndicator() {
		return waiting;
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

	public void setOnCursorOnChart(EventHandler<? super MouseEvent> value) {
		imageView.setOnMouseMoved(value);
	}

	public void setOnCursorOffChart(EventHandler<? super MouseEvent> value) {
		imageView.setOnMouseExited(value);
	}

}
