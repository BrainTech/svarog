package org.signalml.app.model;

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
        private AmplifierConnectionOpenMonitorDescriptor openMonitorDescriptor;
        /**
         * The amplifier instance.
         */
        private AmplifierInstance amplifierInstance;

        public AmplifierInstance getAmplifierInstance() {
                return amplifierInstance;
        }

        public AmplifierConnectionOpenMonitorDescriptor getOpenMonitorDescriptor() {
                return openMonitorDescriptor;
        }

        public void setAmplifierInstance(AmplifierInstance amplifierInstance) {
                this.amplifierInstance = amplifierInstance;
        }

        public void setOpenMonitorDescriptor(AmplifierConnectionOpenMonitorDescriptor openMonitorDescriptor) {
                this.openMonitorDescriptor = openMonitorDescriptor;
        }

        public AmplifierConnectionDescriptor() {
                openMonitorDescriptor = new AmplifierConnectionOpenMonitorDescriptor();
        }
}
