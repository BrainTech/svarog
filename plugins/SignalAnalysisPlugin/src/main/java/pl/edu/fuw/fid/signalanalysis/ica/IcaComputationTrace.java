package pl.edu.fuw.fid.signalanalysis.ica;

import org.apache.commons.math.linear.RealMatrix;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.domain.montage.Montage;

/**
 * @author ptr@mimuw.edu.pl
 */
public class IcaComputationTrace {

	public final SignalDocument document;
	public final RealMatrix icaMatrix;
	public final Montage montage;
	public final int[] selectedChannels;

	public IcaComputationTrace(SignalDocument document, RealMatrix icaMatrix, Montage montage, int[] selectedChannels) {
		this.document = document;
		this.icaMatrix = icaMatrix;
		this.montage = montage;
		this.selectedChannels = selectedChannels;
	}

}
