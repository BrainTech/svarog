/* OpenSignalDescriptor.java created 2011-03-06
 *
 */

package org.signalml.app.model;

import org.signalml.app.view.opensignal.AmplifierConnectionDescriptor;
import org.signalml.app.view.opensignal.SignalSource;
import org.signalml.domain.montage.Montage;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalDescriptor {

	private SignalSource signalSource;

	private OpenFileSignalDescriptor openFileSignalDescriptor;
	private OpenMonitorDescriptor openMonitorDescriptor;
	private AmplifierConnectionDescriptor amplifierConnectionDescriptor;

	private Montage montage;

	public void setMontage(Montage montage) {
		this.montage = montage;
	}

	public Montage getMontage() {
		return montage;
	}

	public AmplifierConnectionDescriptor getAmplifierConnectionDescriptor() {
		return amplifierConnectionDescriptor;
	}

	public void setAmplifierConnectionDescriptor(AmplifierConnectionDescriptor amplifierConnectionDescriptor) {
		this.amplifierConnectionDescriptor = amplifierConnectionDescriptor;
	}

	public OpenFileSignalDescriptor getOpenFileSignalDescriptor() {
		return openFileSignalDescriptor;
	}

	public void setOpenFileSignalDescriptor(OpenFileSignalDescriptor openFileSignalDescriptor) {
		this.openFileSignalDescriptor = openFileSignalDescriptor;
	}

	public OpenMonitorDescriptor getOpenMonitorDescriptor() {
		return openMonitorDescriptor;
	}

	public void setOpenMonitorDescriptor(OpenMonitorDescriptor openMonitorDescriptor) {
		this.openMonitorDescriptor = openMonitorDescriptor;
	}

	public SignalSource getSignalSource() {
		return signalSource;
	}

	public void setSignalSource(SignalSource signalSource) {
		this.signalSource = signalSource;
	}

}
