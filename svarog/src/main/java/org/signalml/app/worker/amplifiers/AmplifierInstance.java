package org.signalml.app.worker.amplifiers;

/**
 * Instance of an amplifier, detected by the system.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierInstance {

        /**
         * The definition of this amplifier.
         */
        private AmplifierDefinition definition;

        /**
         * The address of this amplifier.
         */
        private String address;

        public AmplifierInstance(AmplifierDefinition definition, String address) {

                this.definition = definition;
                this.address = address;
        }

        public String getAddress() {
                return address;
        }

        public AmplifierDefinition getDefinition() {
                return definition;
        }

        @Override
        public String toString() {
                return definition.getName() + " (" + address + ")";
        }
}