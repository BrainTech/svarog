/* OpenSignalDescriptor.java created 2011-03-06
 *
 */

package org.signalml.app.model;

import org.signalml.app.view.opensignal.FileOpenSignalMethod;
import org.signalml.app.view.opensignal.SignalSource;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.SignalType;

/**
 * Describes the signal to be opened and the montage to be applied to the signal
 * directly after opening.
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalDescriptor {

	/**
	 * The source of the signal (file, openBCI, amplifier).
	 */
	private SignalSource signalSource;

	/**
	 * The descriptor of the file signal to be opened.
	 */
	private OpenFileSignalDescriptor openFileSignalDescriptor;

	/**
	 * Descriptor of the openBCI signal to be opened.
	 */
	private OpenMonitorDescriptor openMonitorDescriptor;

	/**
	 * Descriptor of the signal to be opened which will come from an
	 * amplifier.
	 */
	private AmplifierConnectionDescriptor amplifierConnectionDescriptor;

	/**
	 * Montage to be applied to the signal directly after opening.
	 */
	private Montage montage;

	public OpenSignalDescriptor() {
		signalSource = SignalSource.FILE;

		openFileSignalDescriptor = new OpenFileSignalDescriptor();
		openMonitorDescriptor = new OpenMonitorDescriptor();
                amplifierConnectionDescriptor = new AmplifierConnectionDescriptor();

		montage = SignalType.EEG_10_20.getConfigurer().createMontage(openFileSignalDescriptor.getRawSignalDescriptor().getChannelCount());
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

	/**
	 * Returns the descriptor of the signal to be opened from an amplifier.
	 * @return the descriptor of the signal from an amplifier
	 */
	public AmplifierConnectionDescriptor getAmplifierConnectionDescriptor() {
		return amplifierConnectionDescriptor;
	}

	/**
	 * Sets the descriptor of the signal from the amplifier.
	 * @param amplifierConnectionDescriptor the descriptor of the signal from
	 * the amplifier
	 */
	public void setAmplifierConnectionDescriptor(AmplifierConnectionDescriptor amplifierConnectionDescriptor) {
		this.amplifierConnectionDescriptor = amplifierConnectionDescriptor;
	}

	/**
	 * Returns the descriptor of the signal from a file.
	 * @return the descriptor of the signal from a file
	 */
	public OpenFileSignalDescriptor getOpenFileSignalDescriptor() {
		return openFileSignalDescriptor;
	}

	/**
	 * Sets the descriptor of the signal from a file
	 * @param openFileSignalDescriptor the descriptor of the signal from a file
	 */
	public void setOpenFileSignalDescriptor(OpenFileSignalDescriptor openFileSignalDescriptor) {
		this.openFileSignalDescriptor = openFileSignalDescriptor;
	}

	/**
	 * Returns the signal to be opened using an existing openBCI system.
	 * @return the signal to be opened using an existing openBCI system
	 */
	public OpenMonitorDescriptor getOpenMonitorDescriptor() {
		return openMonitorDescriptor;
	}

	/**
	 * Sets the signal to be opened using an existing openBCI system.
	 * @param openMonitorDescriptor the signal to be opened using an existing
	 * openBCI system.
	 */
	public void setOpenMonitorDescriptor(OpenMonitorDescriptor openMonitorDescriptor) {
		this.openMonitorDescriptor = openMonitorDescriptor;
	}

	/**
	 * Returns the source of the signal to be opened (file/openBCI/amplifier).
	 * @return the signal source of the signal to be opened
	 */
	public SignalSource getSignalSource() {
		return signalSource;
	}

	/**
	 * Sets the source of the signal to be opened (file/openBCI/amplifier).
	 * @param signalSource the source of the signal to be opened
	 */
	public void setSignalSource(SignalSource signalSource) {
		this.signalSource = signalSource;
	}

}