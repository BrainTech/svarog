package org.signalml.app.view.monitor.signalchecking;

import java.util.HashMap;
import org.apache.commons.math.complex.Complex;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.fft.FourierTransform;

/**
 * A {@link GenericAmplifierDiagnosis} class that checks whether FFT of some
 * frequencies is greater than FFT of other frequencies.
 *
 * @author Tomasz Sawicki
 */
public class FFTDiagnosis extends GenericAmplifierDiagnosis {

        /**
         * Frequency of each element in the result of fft.
         */
        double[] frequencies;

        /**
         * Constructor.
         * @param monitorSignalDocument {@link GenericAmplifierDiagnosis#monitorSignalDocument}
         * @param parameters {@link GenericAmplifierDiagnosis#parameters}
         */
        public FFTDiagnosis(MonitorSignalDocument monitorSignalDocument, HashMap<String, Object> parameters) {

                super(monitorSignalDocument, parameters);
                frequencies = calculateFrequencies(getSamplesToTest(), getMonitorSignalDocument().getSamplingFrequency());
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

                HashMap<String, ChannelState> channels = new HashMap<String, ChannelState>();

                // Currently returns random values
                for (int i = 0; i < getRoundBuffer().getChannelCount(); i++) {
                        java.util.Random rand = new java.util.Random();
                        boolean channelValid = true;
                        AdditionalChannelData data = new AdditionalChannelData(rand.nextInt(40000) + 60000, rand.nextInt(5000) - 10000, rand.nextInt(40000) + 30000, rand.nextInt(100000), SignalCheckingMethod.FFT);
                        channels.put(getLabel(i), new ChannelState(channelValid, data));
                }

                //for (int i = 0; i < getRoundBuffer().getChannelCount(); i++) {
                //        channels.put(getLabel(i), checkChannel(i));
                //}

                return channels;
        }

        /**
         * Calculates FFT and checks whether given channel is valid.
         * @param channelNo channel number
         * @return channel state
         */
        private ChannelState checkChannel(int channelNo) {

                double[] samples = getSamplesForAChannel(channelNo);
                Complex[] fft = calculateFFT(samples);

                // ...
                
                return null;
        }

        /**
         * Calculates frequency of each element of array returned by {@link FourierTransform#forwardFFTComplex(org.apache.commons.math.complex.Complex[])}
         * @param N number of samples
         * @param samplingFrequency sampling frequency
         * @return array containing frequencies
         */
        private double[] calculateFrequencies(int N, double samplingFrequency) {

                double step = samplingFrequency / N;
                double[] result = new double[N];
                for (int i = 0; i < (N + 1) / 2; i++) {
                        result[i] = step * i;
                }
                for (int i = (N + 1) / 2; i < N; i++) {
                        result[i] = -(step * (N - i));
                }
                return result;
        }

        /**
         * Calculates FFT for chosen samples
         * @param samples samples
         * @return FFT
         */
        private Complex[] calculateFFT(double[] samples) {
		FourierTransform fourierTransform = new FourierTransform();
		return fourierTransform.forwardFFT(samples);
        }
}
