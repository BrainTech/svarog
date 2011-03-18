/* OpenSignalDescriptor.java created 2011-03-06
 *
 */

package org.signalml.app.model;

import org.signalml.app.view.opensignal.FileOpenSignalMethod;
import org.signalml.app.view.opensignal.SignalSource;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.SignalType;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalDescriptor {

	private SignalSource signalSource;

	private OpenFileSignalDescriptor openFileSignalDescriptor;
	private OpenMonitorDescriptor openMonitorDescriptor = new OpenMonitorDescriptor();
	private AmplifierConnectionDescriptor amplifierConnectionDescriptor;

	private Montage montage;

	public OpenSignalDescriptor() {
		signalSource = SignalSource.FILE;
		openFileSignalDescriptor = new OpenFileSignalDescriptor();
		openFileSignalDescriptor.setMethod(FileOpenSignalMethod.RAW);

		montage = SignalType.EEG_10_20.getConfigurer().createMontage(openFileSignalDescriptor.getRawSignalDescriptor().getChannelCount());


		openMonitorDescriptor.setSamplingFrequency(256.0F);
		openMonitorDescriptor.setPageSize(33.3F);
		openMonitorDescriptor.setMultiplexerAddress("127.0.0.1");
		openMonitorDescriptor.setMultiplexerPort(39393);

                amplifierConnectionDescriptor = new AmplifierConnectionDescriptor();
	}

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
