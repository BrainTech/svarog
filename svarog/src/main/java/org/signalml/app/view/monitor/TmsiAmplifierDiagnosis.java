/* TmsiAmplifierDiagnosis.java created 2010-10-26
 *
 */

package org.signalml.app.view.monitor;

import java.util.HashMap;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.domain.signal.RoundBufferMultichannelSampleSource;

/**
 * A {@link GenericAmplifierDiagnosis} class for a TMSI-porti7 amplifier.
 *
 * @author Tomasz Sawicki
 */
public class TmsiAmplifierDiagnosis extends GenericAmplifierDiagnosis {

        /**
         * The bigger this constant is the more samples will be tested.
         */
        private static final double SAMPLES_TESTED_FACTOR = 0.5;

        /**
         * Tolerance of the test. The smaller this constant is the more samples will
         * be stated as invalid.
         */
        private static final double TEST_TOLERANCE = 0.99;

        /**
         * Number of tested samples
         */
        private int samplesTested;

        /**
         * A round buffer containing samples from the amplifier.
         */
        private RoundBufferMultichannelSampleSource roundBuffer;

        /**
         * Number of channels in the signal.
         */
        private int channelCount;

        /**
         * Array containing information about channel's offset.
         */
        private double[] offset;

        /**
         * An array of values (one for each channel) used to check if a sample from
         * that channel is valid. If |sample - offset| is less than maxAmpC then the sample is valid.
         */
        private double[] maxAmpC;

        public TmsiAmplifierDiagnosis(MonitorSignalDocument monitorSignalDocument) {

                super(monitorSignalDocument);

                roundBuffer = (RoundBufferMultichannelSampleSource) monitorSignalDocument.getSampleSource();
                channelCount = roundBuffer.getChannelCount();

                maxAmpC = new double[channelCount];
                offset = monitorSignalDocument.getOffset();
                double ampNull = monitorSignalDocument.getAmplifierNull();
                double[] gain = monitorSignalDocument.getGain();

                for (int i = 0; i < channelCount; i++)
                        maxAmpC[i] = TEST_TOLERANCE * Math.abs(ampNull) * Math.abs(gain[i]);

                samplesTested = (int) (monitorSignalDocument.getSamplingFrequency() * SAMPLES_TESTED_FACTOR);
        }

        /**
         * Returns an information on each channel based on the information
         * from the {@link MonitorSignalDocument} object. Calls {@link #channelValid(int)}
         * for each channel in the signal.
         *
         * @return a HashMap<String, Boolean> - the key is channel's label,
         * the value - true if the signal is OK, false it it's not. If there weren't
         * enough samples in the system to test the signal state, the return value
         * is null.
         */
        @Override
        public HashMap<String, Boolean> signalState() {
               
                if (roundBuffer.getReceivedSampleCount() < samplesTested)
                        return null;

                HashMap<String, Boolean> channels = new HashMap<String, Boolean>();
                
                for (int i = 0; i < channelCount; i++)
                        channels.put(roundBuffer.getLabel(i), channelValid(i));
                
                return channels;
        }

        /**
         * Checks if signal from a given channel is valid. It gets
         * {@link #SAMPLES_TESTED} samples, then calls {@link #sampleValid(double, int)} method for each one.
         *
         * @param channel Number of the channel
         * @return false if all samples were invalid, true if at least one was valid
         */
        private boolean channelValid(int channel) {

                double[] samples = new double[samplesTested];
                roundBuffer.getSamples(channel, samples, roundBuffer.getSampleCount(channel) - samplesTested, samplesTested, 0);

                for (int i = 0; i < samplesTested; i++)
                        if (sampleValid(samples[i], channel)) return true;

                return false;
        }

        /**
         * Checks if a sample is valid.
         *
         * @param sample Value of the sample
         * @param channel Number of the channel from which the sample was collected
         * @return true if the sample is valid, false if it's not
         */
        private boolean sampleValid(double sample, int channel) {
                
                return Math.abs(sample - offset[channel]) < maxAmpC[channel];
        }
}