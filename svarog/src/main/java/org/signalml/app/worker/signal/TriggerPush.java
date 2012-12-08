package org.signalml.app.worker.signal;

public class TriggerPush {
	private int startSample;
	private int endSample;

	private boolean startedProperly = true;
	private boolean endedProperly = true;

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

	public boolean isStartedProperly() {
		return startedProperly;
	}

	public void setStartedProperly(boolean startedProperly) {
		this.startedProperly = startedProperly;
	}

	public boolean isEndedProperly() {
		return endedProperly;
	}

	public void setEndedProperly(boolean endedProperly) {
		this.endedProperly = endedProperly;
	}

}
