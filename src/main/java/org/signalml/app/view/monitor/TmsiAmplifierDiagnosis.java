/* TmsiAmplifierDiagnosis.java created 2010-10-26
 *
 */

package org.signalml.app.view.monitor;

import java.util.HashMap;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.domain.signal.RoundBufferMultichannelSampleSource;

/**
 * A {@link GenericAmplifierDiagnosis} class for a TMSI-porti7 amplifier
 *
 * @author Tomasz Sawicki
 */
public class TmsiAmplifierDiagnosis extends GenericAmplifierDiagnosis {

        /**
         * A constant which says how many samples will be tested to check if the signal
         * from a channel is valid.
         */
        private static final int SAMPLES_TESTED = 100;

        /**
         * A round buffer containing samples from the amplifier
         */
        private RoundBufferMultichannelSampleSource roundBuffer;
        private int channelCount;
        private double[] offset;

        /**
         * An array of values (one for each channel) used to check if a sample from
         * that channel is valid. If |sample - offset| is less than maxAmpC then the sample is valid
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
                        maxAmpC[i] = 0.99 * Math.abs(ampNull) * Math.abs(gain[i]);
        }

        @Override
        public HashMap<String, Boolean> signalState() {
               
                if (roundBuffer.getReceivedSampleCount() < SAMPLES_TESTED)
                        return null;

                HashMap<String, Boolean> channels = new HashMap<String, Boolean>();
                
                for (int i = 0; i < channelCount; i++)
                        channels.put(roundBuffer.getLabel(i), channelValid(i));
                
                return channels;
        }

        /**
         * Method which checks if signal from a given channel is valid. It gets
         * {@link #SAMPLES_TESTED} samples, then calls {@link #sampleValid(double, int)} method for each one
         *
         * @param channel Number of the channel
         * @return false if all samples were invalid, true if at least one was valid
         */
        private boolean channelValid(int channel) {

                double[] samples = new double[SAMPLES_TESTED];
                roundBuffer.getSamples(channel, samples, roundBuffer.getSampleCount(channel) - SAMPLES_TESTED, SAMPLES_TESTED, 0);

                for (int i = 0; i < SAMPLES_TESTED; i++)
                        if (sampleValid(samples[i], channel)) return true;

                return false;
        }

        /**
         * Method which checks if a sample is valid.
         *
         * @param sample Value of the sample
         * @param channel Number of the channel from which the sample was collected
         * @return true if the sample is valid, false if it's not
         */
        private boolean sampleValid(double sample, int channel) {
                
                return Math.abs(sample - offset[channel]) < maxAmpC[channel];
        }
}