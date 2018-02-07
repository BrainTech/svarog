/* AmplifierDignosisManufacture.java created 2010-10-26
 *
 */

package org.signalml.app.view.document.monitor.signalchecking;

import java.util.HashMap;
import org.signalml.app.document.MonitorSignalDocument;

/**
 * Class has only one static method which returns a {@link GenericAmplifierDiagnosis}
 * object for a given signal checking method.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierDignosisManufacture {

	/**
	 * A static method which returns a {@link GenericAmplifierDiagnosis} object.
	 *
	 * @param method a {@link SignalCheckingMethod} object
	 * @param monitorSignalDocument a {@link MonitorSignalDocument} object representing the
	 * currently open monitor document
	 * @param parameters hashmap of parameters for the amp diagnosis
	 * @return a {@link GenericAmplifierDiagnosis} object for the given amplifier model
	 */
	public static GenericAmplifierDiagnosis getAmplifierDiagnosis(SignalCheckingMethod method, MonitorSignalDocument monitorSignalDocument, HashMap<String, Object> parameters) {

		if (method.isAmpNull())
			return new AmplifierNullDiagnosis(monitorSignalDocument, parameters);
		else if (method.isDC())
			return new DCDiagnosis(monitorSignalDocument, parameters);
		else if (method.isFFT())
			return new FFTDiagnosis(monitorSignalDocument, parameters);
		else if (method.isImpedance())
			return new ImpedanceDiagnosis(monitorSignalDocument, parameters);

		else return null;
	}
}