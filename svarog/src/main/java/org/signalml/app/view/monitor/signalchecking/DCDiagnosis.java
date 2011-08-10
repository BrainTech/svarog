package org.signalml.app.view.monitor.signalchecking;

import java.util.HashMap;
import org.signalml.app.document.MonitorSignalDocument;

/**
 * A {@link GenericAmplifierDiagnosis} class that checks whether average
 * signal value is greater than some constant value.
 * <ul>
 * Parameters that must be passed (keys):
 * <li>{@link #LIMIT_VALUES}</li>
 * <li>parameters inherited from {@link GenericAmplifierDiagnosis}:
 * <ul><li>{@link GenericAmplifierDiagnosis#SAMPLES_TESTED_FACTOR}</li></ul>
 * </ul>
 * If average must be obtained from all samples, than SAMPLES_TEST_FACTOR shoud
 * equal checking delay (in seconds)
 * @author Tomasz Sawicki
 */
public class DCDiagnosis extends GenericAmplifierDiagnosis {

        /**
         * {@link #limitValues} parameter key.
         */
        public static final String LIMIT_VALUES = "LimitValues";
        /**
         * Limit values for each channel. Must be passed as a parameter.
         */
        private double[] limitValues;

        /**
         * Constructor.
         * @param monitorSignalDocument {@link GenericAmplifierDiagnosis#monitorSignalDocument}
         * @param parameters {@link GenericAmplifierDiagnosis#parameters}
         */
        public DCDiagnosis(MonitorSignalDocument monitorSignalDocument, HashMap<String, Object> parameters) {
                super(monitorSignalDocument, parameters);
                //limitValues = (double[]) getParameters().get(LIMIT_VALUES);
                limitValues = new double[getChannelCount()];
                for (double a : limitValues) a = 3;
        }

        /**
         * Returns an information on each channel based on the information
         * from the {@link MonitorSignalDocument} object.
         *
         * @return a HashMap<String, ChannelState> - the key is channel's label,
         * the value - channel state.
         */
        @Override
        public HashMap<String, ChannelState> signalState() {

                if (!areEnoughSamples()) {
                        return null;
                }

                HashMap<String, ChannelState> channels = new HashMap<String, ChannelState>();

                /*for (int i = 0; i < getRoundBuffer().getChannelCount(); i++) {
                        //double average = getAverageForChannel(i);
                        //boolean valid = average > limitValues[i];
                        //double max = 5/4 * limitValues[i]; // TODO CHANGE THIS
                        //ChannelState state = new ChannelState(valid, new AdditionalChannelData((int)max, (int)limitValues[i], (int)average, SignalCheckingMethod.DC));
                        //channels.put(getLabel(i), state);
                }*/

                // Currently returns random values
                for (int i = 0; i < getRoundBuffer().getChannelCount(); i++) {
                        java.util.Random rand = new java.util.Random();
                        boolean channelValid = true;
                        AdditionalChannelData data = new AdditionalChannelData(rand.nextInt(40000) + 60000, rand.nextInt(5000) - 10000, rand.nextInt(40000) + 30000, rand.nextInt(100000), SignalCheckingMethod.FFT);
                        channels.put(getLabel(i), new ChannelState(channelValid, data));
                }

                return channels;
        }

        /**
         * Gets average value from last {@link GenericAmplifierDiagnosis#samplesToTest} samples.
         * @param channelNo number of channel
         * @return average value for chosen channel
         */
        private double getAverageForChannel(int channelNo) {

                double[] samples = getSamplesForAChannel(channelNo);
                for (int i = 1; i < samples.length; i++) {
                        samples[0] += samples[i];
                }
                return samples[0] / samples.length;
        }
}
