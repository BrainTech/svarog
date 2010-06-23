package org.signalml.app.worker;


public class WorkerResult {

	public WorkerResult(boolean success, String message) {
		this.success = success;
		this.message = message;
	}
	public boolean success;
	public String message;

}
