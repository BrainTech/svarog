package pl.edu.fuw.fid.signalanalysis.ica;

import org.apache.commons.math.linear.RealMatrix;
import org.signalml.app.document.signal.RawSignalDocument;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.raw.RawSignalDescriptor;

/**
 * @author ptr@mimuw.edu.pl
 */
public class IcaSignalDocument extends RawSignalDocument {

	private final RealMatrix icaMatrix;
	private final RealMatrix montageMatrix;
	private final Montage sourceMontage;

	public IcaSignalDocument(RawSignalDescriptor descriptor, RealMatrix icaMatrix, RealMatrix montageMatrix, Montage sourceMontage) {
		super(descriptor);
		this.icaMatrix = icaMatrix;
		this.montageMatrix = montageMatrix;
		this.sourceMontage = sourceMontage;
	}

	public RealMatrix getIcaMatrix(boolean withSourceMontage) {
		RealMatrix matrix = icaMatrix;
		if (withSourceMontage && montageMatrix != null) {
			matrix = matrix.multiply(montageMatrix);
		}
		return matrix;
	}

	public Montage getSourceMontage() {
		return sourceMontage;
	}

}
