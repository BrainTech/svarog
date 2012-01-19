/* GenericAmplifierDiagnosis.java created 2010-10-26
 *
 */

package org.signalml.app.view.document.monitor;

import java.util.HashMap;
import org.signalml.app.document.MonitorSignalDocument;

/**
 * An abstract class representic an object that - when given a {@link MonitorSignalDocument}
 * object - will check if the signal from an amplifier is OK. Classes for given amplifier models
 * will derive from this class.
 *
 * @author Tomasz Sawicki
 */
public abstract class GenericAmplifierDiagnosis {
        
        private MonitorSignalDocument monitorSignalDocument;

        /**
         * Constructor. The only parameter is a {@link MonitorSignalDocument} object.
         *
         * @param monitorSignalDocument represents the currently open monitor document
         */
        public GenericAmplifierDiagnosis(MonitorSignalDocument monitorSignalDocument) {

                this.monitorSignalDocument = monitorSignalDocument;
        }

        /**
         * Returns an information on each channel based on the information
         * from the {@link MonitorSignalDocument} object.
         *
         * @return a HashMap<String, Boolean> - the key is channel's label,
         * the value - true if the signal is OK, false it it's not. If there weren't
         * enough samples in the system to test the signal state, the return value
         * is null.
         */
        public abstract HashMap<String, Boolean> signalState();
}
