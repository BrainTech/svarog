package org.signalml.app.worker.amplifiers;

/**
 * Discovery state - either a discovered device or a message.
 *
 * @author Tomasz Sawicki
 */
public class DiscoveryState {

        /**
         * Amplifier instance.
         */
        private AmplifierInstance instance;

        /**
         * Message.
         */
        private String message;

        /**
         * Constructor used when a device is discovered.
         *
         * @param instance discovered device.
         */
        public DiscoveryState(AmplifierInstance instance) {

                this.instance = instance;
                this.message = null;
        }

        /**
         * Constructor used when a message is passed.
         *
         * @param message the message
         */
        public DiscoveryState(String message) {

                this.instance = null;
                this.message = message;
        }

        public AmplifierInstance getInstance() {
                return instance;
        }

        public String getMessage() {
                return message;
        }
}
