/* GenericAmplifierDiagnosis.java created 2010-10-26
 *
 */
package org.signalml.app.view.document.monitor.signalchecking;

import java.util.HashMap;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.domain.signal.RoundBufferMultichannelSampleSource;

/**
 * An abstract class representing an object that - when given a {@link MonitorSignalDocument}
 * object - will check if the signal from an amplifier is OK. Classes for given amplifier models
 * will derive from this class.
 *
 * @author Tomasz Sawicki
 */
public abstract class GenericAmplifierDiagnosis {

        /**
         * {@link #samplesTestedFactor} parameter key.
         */
        public static final String SAMPLES_TESTED_FACTOR = "SamplesTestedFactor";
        /**
         * The bigger this constant is the more samples will be tested (number
         * of samples tested = sampling frequency * this factor). Needs to be passed as a parameter.
         */
        private double samplesTestedFactor;
        /**
         * Monitor signal document.
         */
        private MonitorSignalDocument monitorSignalDocument;
        /**
         * Amp parameters.
         */
        private HashMap<String, Object> parameters;
        /**
         * A round buffer containing samples from the amplifier.
         */
        private RoundBufferMultichannelSampleSource roundBuffer;
        /**
         * How many samples should be tested.
         */
        private int samplesToTest;

        /**
         * Constructor.
         *
         * @param monitorSignalDocument represents the currently open monitor document
         */
        public GenericAmplifierDiagnosis(MonitorSignalDocument monitorSignalDocument, HashMap<String, Object> parameters) {

                this.monitorSignalDocument = monitorSignalDocument;
                this.parameters = parameters;
                this.roundBuffer = (RoundBufferMultichannelSampleSource) getMonitorSignalDocument().getSampleSource();
                samplesTestedFactor = Double.parseDouble(getParameters().get(SAMPLES_TESTED_FACTOR).toString());
                samplesToTest = (int) (getMonitorSignalDocument().getSamplingFrequency() * samplesTestedFactor);
        }

        /**
         * Returns an information on each channel based on the information
         * from the {@link MonitorSignalDocument} object.
         *
         * @return a HashMap<String, Boolean> - the key is channel's label,
         * the value - true if the signal is OK, false it it's not. If there weren't
         * enough samples in the system to test the signal state, the return value
         * is null.
         */
        public abstract HashMap<String, ChannelState> signalState();

        /**
         * Returns the monitor signal document.
         * @return the monitor signal document
         */
        public final MonitorSignalDocument getMonitorSignalDocument() {
                return monitorSignalDocument;
        }

        /**
         * Returns the parameters.
         * @return the parameters
         */
        public final HashMap<String, Object> getParameters() {
                return parameters;
        }

        /**
         * Returns the round buffer.
         * @return the round buffer
         */
        public final RoundBufferMultichannelSampleSource getRoundBuffer() {
                return roundBuffer;
        }

        /**
         * Returns {@link #samplesToTest}.
         * @return {@link #samplesToTest}
         */
        public final int getSamplesToTest() {
                return samplesToTest;
        }

        /**
         * Whether are there enough samples to make a test.
         * @return true if there are enough samples to make a test
         */
        protected boolean areEnoughSamples() {
                return (getRoundBuffer().getReceivedSampleCount() >= getSamplesToTest());
        }

        /**
         * Gets last {@link #samplesToTest} samples from a given channel.
         * @param channelNo channel number
         * @return last {@link #samplesToTest} samples from chosen channel
         */
        protected double[] getSamplesForAChannel(int channelNo) {
                double[] samples = new double[getSamplesToTest()];
                getRoundBuffer().getSamples(channelNo, samples, getRoundBuffer().getSampleCount(channelNo) - getSamplesToTest(), getSamplesToTest(), 0);
                return samples;
        }

        /**
         * Returns the channel count.
         * @return the channel count
         */
        protected int getChannelCount() {
                return roundBuffer.getChannelCount();
        }

        /**
         * Returns the label for the chosen chanel.
         * @param channelNo chosen chanel number
         * @return the label for the chosen chanel
         */
        protected String getLabel(int channelNo) {
                return roundBuffer.getLabel(channelNo);
        }

        /**
         * Returns max value which will be used in drawing {@link CheckSignalDisplay}.
         * @param currentValues current values
         * @param limits limit values
         * @return max value to draw
         */
        /*protected double getMaxValue(double[] currentValues, double[] limits) {

                double maxLimit = -1;
                double maxValue = -1;                
                boolean maxOver = false;

                for (int i = 0; i < currentValues.length; i++) {

                        if (currentValues[i] > maxValue) {
                                maxValue = currentValues[i];
                                if (currentValues[i] > limits[i]) {
                                        maxOver = true;
                                }
                        }
                        if (limits[i] > maxLimit) {
                                maxLimit = limits[i];
                        }                        
                }

                if (maxValue < maxLimit)
                        return 1.25 * maxLimit;
                else if ()
        }*/
}
