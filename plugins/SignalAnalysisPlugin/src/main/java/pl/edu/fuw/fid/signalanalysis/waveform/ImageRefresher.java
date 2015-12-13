package pl.edu.fuw.fid.signalanalysis.waveform;

import java.awt.image.BufferedImage;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ImageRefresher {
	
	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ImageRefresher.class);

	private final ProgressIndicator waiting = new ProgressIndicator();
	private Task<Image> lastTask;
	private volatile long lastTaskId = 0;
	private final ImageRenderer computer;

	public ImageRefresher(ImageRenderer computer) {
		this.computer = computer;
		waiting.setMaxWidth(100);
		waiting.setMaxHeight(100);
	}
	
	public ProgressIndicator getProgressIndicator() {
		return waiting;
	}
	
	// must be called from JavaFX thread
	public void refreshChartImage(final ImageChart chart) {
		final int width = (int) chart.getXAxis().getWidth();
		final int height = (int) chart.getYAxis().getHeight();

		if (width > 0 && height > 0) {
			final long id = ++lastTaskId;
			if (lastTask != null) {
				lastTask.cancel(true);
			}
			final Task<Image> task = new Task<Image>() {
				@Override
				protected Image call() throws Exception {
					try {
						BufferedImage image = computer.renderImage(chart.getXAxis(), chart.getYAxis(), new ImageRendererStatus(this, waiting.progressProperty()));
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
							chart.showImage(image);
							waiting.setVisible(false);
						}
					}
				}
			});
			lastTask = task;
//			chart.startFadeTransition(0.0);
			waiting.relocate(0.5*width, 0.5*height);
			waiting.setVisible(true);
			new Thread(task).start();
		}
	}
}
