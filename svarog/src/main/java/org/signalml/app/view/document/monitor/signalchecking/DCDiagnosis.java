package org.signalml.app.view.document.monitor.signalchecking;

import java.util.HashMap;
import org.signalml.app.document.MonitorSignalDocument;

/**
 * A {@link GenericAmplifierDiagnosis} class that checks whether average
 * signal value is greater than some constant value.
 * <ul>
 * Parameters that must be passed (keys):
 * <li>parameters inherited from {@link GenericAmplifierDiagnosis}:
 * <ul><li>{@link GenericAmplifierDiagnosis#SAMPLES_TESTED_FACTOR}</li></ul>
 * </ul>
 * If average must be obtained from all samples, than SAMPLES_TEST_FACTOR shoud
 * equal checking delay (in seconds)
 * @author Tomasz Sawicki
 */
public class DCDiagnosis extends GenericAmplifierDiagnosis {
        
        private double minimum = 0.0;
        private double maximum = 10000;
        private double limit = 5000;

        /**
         * Constructor.
         * @param monitorSignalDocument {@link GenericAmplifierDiagnosis#monitorSignalDocument}
         * @param parameters {@link GenericAmplifierDiagnosis#parameters}
         */
        public DCDiagnosis(MonitorSignalDocument monitorSignalDocument, HashMap<String, Object> parameters) {
                super(monitorSignalDocument, parameters);
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

                for (int i = 0; i < getRoundBuffer().getChannelCount(); i++) {
                        double average = getAverageForChannel(i);
                        
                        AdditionalChannelData additionalChannelData = new AdditionalChannelData(maximum, minimum, limit, average, SignalCheckingMethod.DC);
                        ChannelState channelState = new ChannelState(true, additionalChannelData);
                        channels.put(getLabel(i), channelState);
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
                double sum = 0;
                for (int i = 1; i < samples.length; i++) {
                        sum += Math.abs(samples[i]);
                }
                return sum / samples.length;
        }
}
