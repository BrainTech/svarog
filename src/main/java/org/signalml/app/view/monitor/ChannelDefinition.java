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

        public ChannelDefinition(int number, float gain, float offset) {
                this.number = number;
                this.gain = gain;
                this.offset = offset;
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