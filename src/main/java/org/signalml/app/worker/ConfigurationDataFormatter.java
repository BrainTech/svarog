package org.signalml.app.worker;

import java.util.HashMap;

/**
 * Contains all the data for the configuration.
 *
 * @author Tomasz Sawicki
 */
public class ConfigurationDataFormatter {

        /**
         * Channel count.
         */
        private Integer channelCount;
        private final String NUMBER_OF_CHANNELS = "NumOfChannels";

        /**
         * Calibration gain array.
         */
        private Float[] calibrationGain;
        private final String GAIN = "Gain";

        /**
         * Calibration offset array.
         */
        private Float[] calibrationOffset;
        private final String OFFSET = "Offset";

        /**
         * Sampling frequency.
         */
        private Float samplingFrequency;
        private final String SAMPLING_RATE = "SamplingRate";

        /**
         * Selected channels indecies.
         */
        private Integer[] selectedChannelsIndecies;
        private final String CHANNELS_TO_RECORD = "AmplifierChannelsToRecord";

        /**
         * Channel labels.
         */
        private String[] channelLabels;
        private final String CHANNELS_NAMES = "ChannelsNames";

        /**
         * Constructor sets all the values.
         *
         * @param channelCount {@link #channelCount}
         * @param calibrationGain {@link #calibrationGain}
         * @param calibrationOffset {@link #calibrationOffset}
         * @param samplingFrequency {@link #samplingFrequency}
         * @param selectedChannelsIndecies {@link #selectedChannelsIndecies}
         * @param channelLabels {@link #channelLabels}
         */
        public ConfigurationDataFormatter(Integer channelCount,
                                          Float[] calibrationGain,
                                          Float[] calibrationOffset,
                                          Float samplingFrequency,
                                          Integer[] selectedChannelsIndecies,
                                          String[] channelLabels) {

                this.channelCount = channelCount;
                this.calibrationGain = calibrationGain;
                this.calibrationOffset = calibrationOffset;
                this.samplingFrequency = samplingFrequency;
                this.selectedChannelsIndecies = selectedChannelsIndecies;
                this.channelLabels = channelLabels;
        }

        /**
         * Converts data to OpenBCI message format.
         *
         * @return map containing keys and values
         */
        public HashMap<String, String> formatData() {

                HashMap<String, String> retval = new HashMap<String, String>();

                retval.put(NUMBER_OF_CHANNELS, objectToString(channelCount));
                retval.put(GAIN, objectArrayToString(calibrationGain, " "));
                retval.put(OFFSET, objectArrayToString(calibrationOffset, " "));
                retval.put(SAMPLING_RATE, objectToString(samplingFrequency));
                retval.put(CHANNELS_TO_RECORD, objectArrayToString(selectedChannelsIndecies, " "));
                retval.put(CHANNELS_NAMES, objectArrayToString(channelLabels, ";"));

                return retval;
        }

        /**
         * Converts an array to string.
         *
         * @param input input array
         * @param separator the separator (" " or ";" ...)
         * @return output string
         */
        public String objectArrayToString(Object[] input, String separator) {

                String output = "";

                for (int i = 0; i < input.length; i++) {
                        output += String.valueOf(input[i]);
                        if (i < (input.length - 1)) {
                                output += separator;
                        }
                }

                return output;
        }

        /**
         * Converts an object to string
         *
         * @param input input object
         * @return outpu string
         */
        public String objectToString(Object input) {

                return String.valueOf(input);
        }
}