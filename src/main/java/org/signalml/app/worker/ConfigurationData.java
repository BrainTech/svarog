package org.signalml.app.worker;

/**
 * Contains all the data for the configuration.
 *
 * @author Tomasz Sawicki
 */
public class ConfigurationData {

        /**
         * Channel count.
         */
        private Integer channelCount;

        /**
         * Calibration gain array.
         */
        private float[] calibrationGain;

        /**
         * Calibration offset array.
         */
        private float[] calibrationOffset;

        /**
         * Sampling frequency.
         */
        private Float samplingFrequency;

        /**
         * Selected channels indecies.
         */
        private int[] selectedChannelsIndecies;

        /**
         * Channel labels.
         */
        private String[] channelLabels;

        public float[] getCalibrationGain() {
                return calibrationGain;
        }

        public float[] getCalibrationOffset() {
                return calibrationOffset;
        }

        public Integer getChannelCount() {
                return channelCount;
        }

        public String[] getChannelLabels() {
                return channelLabels;
        }

        public Float getSamplingFrequency() {
                return samplingFrequency;
        }

        public int[] getSelectedChannelsIndecies() {
                return selectedChannelsIndecies;
        }

        public void setCalibrationGain(float[] calibrationGain) {
                this.calibrationGain = calibrationGain;
        }

        public void setCalibrationOffset(float[] calibrationOffset) {
                this.calibrationOffset = calibrationOffset;
        }

        public void setChannelCount(Integer channelCount) {
                this.channelCount = channelCount;
        }

        public void setChannelLabels(String[] channelLabels) {
                this.channelLabels = channelLabels;
        }

        public void setSamplingFrequency(Float samplingFrequency) {
                this.samplingFrequency = samplingFrequency;
        }

        public void setSelectedChannelsIndecies(int[] selectedChannelsIndecies) {
                this.selectedChannelsIndecies = selectedChannelsIndecies;
        }
}
