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

        @Override
        public String toString() {

                String retval = "no.: " + String.valueOf(number) + "; ";
                retval += "gain: " + String.valueOf(gain) + "; ";
                retval += "offset: " + String.valueOf(offset);

                return retval;
        }
}
