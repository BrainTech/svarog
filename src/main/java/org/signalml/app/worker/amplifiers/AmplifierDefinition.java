package org.signalml.app.worker.amplifiers;

import java.util.List;

import org.signalml.app.config.preset.Preset;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;

/**
 * Definition of an amplifier.
 *
 * @author Tomasz Sawicki
 */
@XStreamAlias("amplifierDefinition")
public class AmplifierDefinition implements Preset {

        public static final String USB = "USB";
        public static final String BLUETOOTH = "Bluetooth";

        /**
         * Amplifier's communication protocol.
         */
        private String protocol;

        /**
         * Amplifier's match string.
         */
        private String match;

        /**
         * Amplifier's id.
         */
        private String name;

        /**
         * Available sampling frequencies.
         */
        private List<Float> availableFrequencies;
        
        /**
         * Available channel numbers.
         */
        private List<Integer> channelNumbers;

        /**
         * Channels' gain.
         */
        private List<Float> calibrationGain;

        /**
         * Channels' offset.
         */
        private List<Float> calibrationOffset;

        /**
         * Driver path.
         */
        private String driverPath;

        /**
         * Amplifier null.
         */
        private Double amplifierNull;

        /**
         * Returns the number of channels.
         *
         * @return number of channels
         */
        public int getChannelCount() {
                return channelNumbers.size();
        }

        /**
         * Default constructor.
         */
        public AmplifierDefinition() {

                this.availableFrequencies = new ArrayList<Float>();
                this.calibrationGain = new ArrayList<Float>();
                this.calibrationOffset = new ArrayList<Float>();
                this.channelNumbers = new ArrayList<Integer>();
                this.driverPath = "";
                this.match = "";
                this.name = "";
                this.protocol = USB;
                this.amplifierNull = 0.0;
        }

        @Override
        public String toString() {
                return name;
        }

        @Override
        public String getName() {
                return name;
        }

        public List<Float> getAvailableFrequencies() {
                return availableFrequencies;
        }

        public List<Float> getCalibrationGain() {
                return calibrationGain;
        }

        public List<Float> getCalibrationOffset() {
                return calibrationOffset;
        }

        public List<Integer> getChannelNumbers() {
                return channelNumbers;
        }

        public String getDriverPath() {
                return driverPath;
        }

        public String getMatch() {
                return match;
        }

        public String getProtocol() {
                return protocol;
        }

        public Double getAmplifierNull() {
                return amplifierNull;
        }

        @Override
        public void setName(String name) {
                this.name = name;
        }

        public void setAvailableFrequencies(List<Float> availableFrequencies) {
                this.availableFrequencies = availableFrequencies;
        }

        public void setCalibrationGain(List<Float> calibrationGain) {
                this.calibrationGain = calibrationGain;
        }

        public void setCalibrationOffset(List<Float> calibrationOffset) {
                this.calibrationOffset = calibrationOffset;
        }

        public void setChannelNumbers(List<Integer> channelNumbers) {
                this.channelNumbers = channelNumbers;
        }

        public void setDriverPath(String driverPath) {
                this.driverPath = driverPath;
        }

        public void setMatch(String match) {
                this.match = match;
        }

        public void setProtocol(String protocol) {
                this.protocol = protocol;
        }

        public void setAmplifierNull(Double amplifierNull) {
                this.amplifierNull = amplifierNull;
        }
}