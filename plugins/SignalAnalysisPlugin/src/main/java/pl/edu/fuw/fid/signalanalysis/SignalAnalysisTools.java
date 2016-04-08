package pl.edu.fuw.fid.signalanalysis;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SvarogAccessSignal;

/**
 * Several, mostly self-explanatory functions used in multiple places
 * by Signal Analysis plugin.
 *
 * @author ptr@mimuw.edu.pl
 */
public class SignalAnalysisTools {

	public static final double THRESHOLD = 1.0e-9;
	public static final double MIN_WAVELET_FREQ = 2.0;

	public static File createRawTemporaryFileFromData(SvarogAccessSignal signalAccess, RealMatrix data) throws IOException {
		File newFile = signalAccess.getTemporaryFile(".raw");
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(newFile));

		// write multiplexed multichannel data
		for (int i=0; i<data.getColumnDimension(); ++i) {
			double[] values = data.getColumn(i);
			for (int j=0; j<values.length; ++j) {
				dos.writeDouble(values[j]);
			}
		}
		dos.close();
		return newFile;
	}

	public static Integer parsePositiveInteger(Object object) {
		Integer result = null;
		if (object instanceof Integer) {
			Integer number = (Integer) object;
			if (number > 0) {
				result = number;
			}
		} else if (object instanceof String) try {
			String string = (String) object;
			result = Integer.parseInt(string);
		} catch (NumberFormatException ex) {
			// nothing here, returning null
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

	public static RealMatrix extractDataFromSignal(MultichannelSampleSource source, ExportedSignalSelection selection, int[] channels) {
		RealMatrix data;
		if (channels == null) {
			channels = new int[source.getChannelCount()];
			for (int i=0; i<channels.length; ++i) {
				channels[i] = i;
			}
		}
		int sampleCount = source.getSampleCount(channels[0]);
		if (selection != null) {
			int start = (int) Math.round(selection.getPosition() * source.getSamplingFrequency());
			int length = (int) Math.round(selection.getLength() * source.getSamplingFrequency());
			if (start < 0 || length <= 0 || start + length > sampleCount) {
				throw new IllegalArgumentException("invalid selection");
			}
			double[] buffer = new double[length];
			data = new Array2DRowRealMatrix(channels.length, length);
			for (int i=0; i<channels.length; ++i) {
				source.getSamples(channels[i], buffer, start, length, 0);
				data.setRow(i, buffer);
			}
		} else {
			double[] buffer = new double[sampleCount];
			data = new Array2DRowRealMatrix(channels.length, sampleCount);
			for (int i=0; i<channels.length; ++i) {
				source.getSamples(channels[i], buffer, 0, sampleCount, 0);
				data.setRow(i, buffer);
			}
		}
		return data;
	}

	public static String[] generateIcaComponentNames(int componentCount) {
		String[] componentNames = new String[componentCount];
		for (int i=0; i<componentCount; ++i) {
			componentNames[i] = "ICA-"+(i+1);
		}
		return componentNames;
	}

}
