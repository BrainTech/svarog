package pl.edu.fuw.fid.signalanalysis;

import javafx.concurrent.Task;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ImageRendererStatus {

	private final Task task;

	public ImageRendererStatus(Task task) {
		this.task = task;
	}

	public boolean isCancelled() {
		return task.isCancelled();
	}

}
