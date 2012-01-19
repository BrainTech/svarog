package org.signalml.app.model.monitor;

import org.signalml.app.model.document.opensignal.OpenMonitorDescriptor;
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
        /**
         * Wheter bci was started.
         */
        private boolean bciStarted;

        public AmplifierInstance getAmplifierInstance() {
                return amplifierInstance;
        }

        public OpenMonitorDescriptor getOpenMonitorDescriptor() {
                return openMonitorDescriptor;
        }

        public void setAmplifierInstance(AmplifierInstance amplifierInstance) {
                this.amplifierInstance = amplifierInstance;
                bciStarted = false;
        }

        public void setOpenMonitorDescriptor(OpenMonitorDescriptor openMonitorDescriptor) {
                this.openMonitorDescriptor = openMonitorDescriptor;
        }

        public AmplifierConnectionDescriptor() {
                openMonitorDescriptor = new OpenMonitorDescriptor();
        }

        public boolean isBciStarted() {
                return bciStarted;
        }

        public void setBciStarted(boolean bciStarted) {
                this.bciStarted = bciStarted;
        }
}
