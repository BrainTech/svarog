package pl.edu.fuw.fid.signalanalysis.waveform;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import pl.edu.fuw.fid.signalanalysis.AsyncStatus;

/**
 * Asynchronous result (promise) of ImageRenderer's computation.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ImageRendererStatus implements AsyncStatus {

	private final DoubleProperty progress;
	private final Task task;

	public ImageRendererStatus(Task task, DoubleProperty progress) {
		this.progress = progress;
		this.task = task;
	}

	@Override
	public boolean isCancelled() {
		return task.isCancelled();
	}

	@Override
	public void setProgress(final double progress) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ImageRendererStatus.this.progress.set(progress);
			}
		});
	}

}
