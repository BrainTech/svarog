package pl.edu.fuw.fid.signalanalysis;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.signalml.domain.montage.Montage;

/**
 * @author ptr@mimuw.edu.pl
 */
public class SignalAnalysisTools {

	public static final double THRESHOLD = 1.0e-9;

	public static RealMatrix extractMatrixFromMontage(Montage montage) {
		int outputs = montage.getMontageChannelCount();
		int inputs = montage.getSourceChannelCount();
		RealMatrix result = new Array2DRowRealMatrix(outputs, inputs);
		for (int i=0; i<outputs; ++i) {
			float[] coeffs = montage.getReferenceAsFloat(i);
			for (int k=0; k<inputs; ++k) {
				result.setEntry(i, k, coeffs[k]);
			}
		}
		return result;
	}

	public static RealMatrix extractMatrixFromMontage(Montage montage, int[] selectedOutputs) {
		int outputs = selectedOutputs.length;
		int inputs = montage.getSourceChannelCount();
		RealMatrix result = new Array2DRowRealMatrix(outputs, inputs);
		for (int i=0; i<outputs; ++i) {
			float[] coeffs = montage.getReferenceAsFloat(selectedOutputs[i]);
			for (int k=0; k<inputs; ++k) {
				result.setEntry(i, k, coeffs[k]);
			}
		}
		return result;
	}

}
