package org.signalml.app.worker.amplifiers;

/**
 * Definition of an amplifier.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierDefinition {

        public static final String USB = "USB";
        public static final String BLUETOOTH = "BT";

        /**
         * Amplifier's communication protocol.
         */
        private String protocol;

        /**
         * Amplifier's match string.
         */
        private String match;

        /**
         * Amplifier's id.
         */
        private String id;

        public String getId() {
                return id;
        }

        public String getMatch() {
                return match;
        }

        public String getProtocol() {
                return protocol;
        }

        public void setId(String id) {
                this.id = id;
        }

        public void setMatch(String match) {
                this.match = match;
        }

        public void setProtocol(String protocol) {
                this.protocol = protocol;
        }
}