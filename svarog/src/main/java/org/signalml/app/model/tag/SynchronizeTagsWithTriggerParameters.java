package org.signalml.app.model.tag;

import org.signalml.app.document.signal.SignalDocument;

/**
 * The parameters for synchronizing tags with a trigger channel.
 * @author Piotr Szachewicz
 */
public class SynchronizeTagsWithTriggerParameters {

	private SignalDocument signalDocument;
	private double thresholdValue;
	private int triggerChannel;

	public SignalDocument getSignalDocument() {
		return signalDocument;
	}
	public void setSignalDocument(SignalDocument signalDocument) {
		this.signalDocument = signalDocument;
	}
	public double getThresholdValue() {
		return thresholdValue;
	}
	public void setThresholdValue(double thresholdValue) {
		this.thresholdValue = thresholdValue;
	}
	public int getTriggerChannel() {
		return triggerChannel;
	}
	public void setTriggerChannel(int triggerChannel) {
		this.triggerChannel = triggerChannel;
	}

}
