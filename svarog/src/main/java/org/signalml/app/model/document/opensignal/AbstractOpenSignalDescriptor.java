package org.signalml.app.model.document.opensignal;

import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.system.EegSystemName;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public abstract class AbstractOpenSignalDescriptor {

	protected SignalParameters signalParameters = new SignalParameters();

	@XStreamOmitField
	protected EegSystem eegSystem;
	protected EegSystemName eegSystemName;

	/**
	 * an array of labels of signal channels
	 */
	private String[] channelLabels;

	/**
	 * Montage to be applied to the signal directly after opening.
	 */
	private Montage montage;

	private boolean correctlyRead = false;

	/**
	 * After opening this signal tries to open a tag document
	 * having the same name but a tag extension.
	 */
	private boolean tryToOpenTagDocument;

	public EegSystem getEegSystem() {
		return eegSystem;
	}

	public void setEegSystem(EegSystem eegSystem) {
		this.eegSystem = eegSystem;
		this.eegSystemName = eegSystem.getEegSystemName();
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

	/**
	 * Returns the name of the {@link EegSystem EEG system} that
	 * is used for this signal.
	 * @return the name of the EEG system
	 */
	public EegSystemName getEegSystemName() {
		return eegSystemName;
	}

	/**
	 * Sets the name of the {@link EegSystem} that is used for this signal.
	 * @param eegSystemName the name of the EEG system
	 */
	public void setEegSystemName(EegSystemName eegSystemName) {
		this.eegSystemName = eegSystemName;
	}

	public void setCorrectlyRead(boolean correctlyRead) {
		this.correctlyRead = correctlyRead;
	}

	public boolean isCorrectlyRead() {
		return correctlyRead;
	}

	public boolean isTryToOpenTagDocument() {
		return tryToOpenTagDocument;
	}

	public void setTryToOpenTagDocument(boolean tryToOpenTagDocument) {
		this.tryToOpenTagDocument = tryToOpenTagDocument;
	}

}
