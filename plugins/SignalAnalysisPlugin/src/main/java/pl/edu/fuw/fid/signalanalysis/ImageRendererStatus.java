package pl.edu.fuw.fid.signalanalysis;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ImageRendererStatus {

	private final DoubleProperty progress;
	private final Task task;

	public ImageRendererStatus(Task task, DoubleProperty progress) {
		this.progress = progress;
		this.task = task;
	}

	public boolean isCancelled() {
		return task.isCancelled();
	}

	public void setProgress(final double progress) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ImageRendererStatus.this.progress.set(progress);
			}
		});
	}

}
