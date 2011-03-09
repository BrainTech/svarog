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
        private double gain;

        /**
         * Channel offset.
         */
        private double offset;

        public ChannelDefinition(int number, double gain, double offset) {
                this.number = number;
                this.gain = gain;
                this.offset = offset;
        }

        public double getGain() {
                return gain;
        }

        public int getNumber() {
                return number;
        }

        public double getOffset() {
                return offset;
        }

        public void setGain(double gain) {
                this.gain = gain;
        }

        public void setNumber(int number) {
                this.number = number;
        }

        public void setOffset(double offset) {
                this.offset = offset;
        }
}
