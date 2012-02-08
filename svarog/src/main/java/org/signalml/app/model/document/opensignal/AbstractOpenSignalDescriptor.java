package org.signalml.app.model.document.opensignal;

import org.signalml.domain.montage.system.EegSystem;

public abstract class AbstractOpenSignalDescriptor {

	protected SignalParameters signalParameters;
	protected EegSystem eegSystem;
	
	public EegSystem getEegSystem() {
		return eegSystem;
	}

	public void setEegSystem(EegSystem eegSystem) {
		this.eegSystem = eegSystem;
	}

	public SignalParameters getSignalParameters() {
		return signalParameters;
	}

	public void setSignalParameters(SignalParameters signalParameters) {
		this.signalParameters = signalParameters;
	}
	
}
