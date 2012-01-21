package org.signalml.app.model.document.opensignal;

public abstract class AbstractOpenSignalDescriptor {

	protected SignalParameters signalParameters;
	//protected String[] channelLabels;

	public SignalParameters getSignalParameters() {
		return signalParameters;
	}

	public void setSignalParameters(SignalParameters signalParameters) {
		this.signalParameters = signalParameters;
	}
	
}
