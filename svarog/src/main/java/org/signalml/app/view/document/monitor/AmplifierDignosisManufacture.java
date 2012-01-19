/* AmplifierDignosisManufacture.java created 2010-10-26
 *
 */

package org.signalml.app.view.document.monitor;

import org.signalml.app.document.MonitorSignalDocument;

/**
 * Class has only one static method which returns a {@link GenericAmplifierDiagnosis}
 * object for a given amplifier type.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierDignosisManufacture {

        /**
         * A static method which returns a {@link GenericAmplifierDiagnosis} object.
         *
         * @param amplifierName name of an amplifier mode
         * @param monitorSignalDocument a {@link MonitorSignalDocument} object representing the
         * currently open monitor document
         * @return a {@link GenericAmplifierDiagnosis} object for the given amplifier model
         */
        public static GenericAmplifierDiagnosis getAmplifierDiagnosis(String amplifierName, MonitorSignalDocument monitorSignalDocument) {

                if (amplifierName.contentEquals("TMSI-porti7"))
                        return new TmsiAmplifierDiagnosis(monitorSignalDocument);

                else return null;
        }
}