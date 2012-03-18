package org.signalml.app.model.document.opensignal;

import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.system.EegSystem;

public abstract class AbstractOpenSignalDescriptor {

	protected SignalParameters signalParameters = new SignalParameters();
	protected EegSystem eegSystem;

	/**
	 * an array of labels of signal channels
	 */
	private String[] channelLabels;	

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
	 * 
	 * @param montage
	 *            montage to be applied
	 */
	public void setMontage(Montage montage) {
		this.montage = montage;
	}

	/**
	 * Returns the montage to be applied to the document directly after opening.
	 * 
	 * @return the montage to be applied
	 */
	public Montage getMontage() {
		return montage;
	}

	/**
	 * Returns an array of labels of signal channels
	 * 
	 * @return an array of labels of signal channels
	 */
	public String[] getChannelLabels() {
		return channelLabels;
	}

	/**
	 * Sets an array of labels of signal channels
	 * 
	 * @param channelLabels
	 *            an array of labels of signal channels
	 */
	public void setChannelLabels(String[] channelLabels) {
		this.channelLabels = channelLabels;
	}

}
