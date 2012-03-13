package org.signalml.app.model.document.opensignal;

import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.system.EegSystem;

public abstract class AbstractOpenSignalDescriptor {

	protected SignalParameters signalParameters = new SignalParameters();
	protected EegSystem eegSystem;
	
	/**
	 * Montage to be applied to the signal directly after opening.
	 */
	private Montage montage;
	
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
	
	/**
	 * Sets a montage to be applied to the document directly after opening.
	 * @param montage montage to be applied
	 */
	public void setMontage(Montage montage) {
		this.montage = montage;
	}

	/**
	 * Returns the montage to be applied to the document directly after
	 * opening.
	 * @return the montage to be applied
	 */
	public Montage getMontage() {
		return montage;
	}

}
