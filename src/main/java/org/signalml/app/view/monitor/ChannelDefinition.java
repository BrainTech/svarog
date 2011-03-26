package org.signalml.app.view.monitor;

/**
 * Definition of a channel.
 *
 * @author Tomasz Sawicki
 */
public class ChannelDefinition {

        /**
         * Channel number.
         */
        private int number;

        /**
         * Channel gain.
         */
        private float gain;

        /**
         * Channel offset.
         */
        private float offset;

        /**
         * Default name.
         */
        private String defaultName;

        public ChannelDefinition(int number, float gain, float offset, String defaultName) {
                this.number = number;
                this.gain = gain;
                this.offset = offset;
                this.defaultName = defaultName;
        }

        public String getDefaultName() {
                return defaultName;
        }

        public float getGain() {
                return gain;
        }

        public int getNumber() {
                return number;
        }

        public float getOffset() {
                return offset;
        }

        public void setDefaultName(String defaultName) {
                this.defaultName = defaultName;
        }

        public void setGain(float gain) {
                this.gain = gain;
        }

        public void setNumber(int number) {
                this.number = number;
        }

        public void setOffset(float offset) {
                this.offset = offset;
        }
}