package org.signalml.app.worker.signal;

public class TriggerPush {
	private int startSample;
	private int endSample;

	public TriggerPush(int startSample, int endSample) {
		super();
		this.startSample = startSample;
		this.endSample = endSample;
	}

	public int getStartSample() {
		return startSample;
	}
	public void setStartSample(int startSample) {
		this.startSample = startSample;
	}
	public int getEndSample() {
		return endSample;
	}
	public void setEndSample(int endSample) {
		this.endSample = endSample;
	}

	public int getLength() {
		return endSample - startSample;
	}

}
