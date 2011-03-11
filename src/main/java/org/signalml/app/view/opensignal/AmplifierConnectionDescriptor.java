package org.signalml.app.view.opensignal;

import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.worker.amplifiers.AmplifierInstance;

/**
 * Describes connection to an amplifier.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierConnectionDescriptor {

        /**
         * The open monitor descriptor.
         */
        private OpenMonitorDescriptor openMonitorDescriptor;

        /**
         * The amplifier instance.
         */
        private AmplifierInstance amplifierInstance;

        public AmplifierInstance getAmplifierInstance() {
                return amplifierInstance;
        }

        public OpenMonitorDescriptor getOpenMonitorDescriptor() {
                return openMonitorDescriptor;
        }

        public void setAmplifierInstance(AmplifierInstance amplifierInstance) {
                this.amplifierInstance = amplifierInstance;
        }

        public void setOpenMonitorDescriptor(OpenMonitorDescriptor openMonitorDescriptor) {
                this.openMonitorDescriptor = openMonitorDescriptor;
        }

        public AmplifierConnectionDescriptor() {
                openMonitorDescriptor = new OpenMonitorDescriptor();
        }
}
