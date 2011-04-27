package org.signalml.app.worker.amplifiers;

/**
 * Discovery state - either a discovered device or a message.
 *
 * @author Tomasz Sawicki
 */
public class DiscoveryState {

        /**
         * Device info.
         */
        private DeviceInfo info;

        /**
         * Message.
         */
        private String message;

        /**
         * Constructor used when a device is discovered.
         *
         * @param instance discovered device.
         */
        public DiscoveryState(DeviceInfo info) {

                this.info = info;
                this.message = null;
        }

        /**
         * Constructor used when a message is passed.
         *
         * @param message the message
         */
        public DiscoveryState(String message) {

                this.info = null;
                this.message = message;
        }

        public DeviceInfo getInfo() {
                return info;
        }

        public String getMessage() {
                return message;
        }
}
