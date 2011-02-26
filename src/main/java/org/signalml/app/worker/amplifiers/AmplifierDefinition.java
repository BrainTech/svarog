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
        private List<Integer> availableFrequencies;
        
        /**
         * Available channel numbers.
         */
        private List<Integer> channelNumbers;

        /**
         * Channels' gain.
         */
        private List<Double> channelGain;

        /**
         * Channels' offset.
         */
        private List<Double> channelOffset;

        /**
         * Driver path.
         */
        private String driverPath;

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

                this.availableFrequencies = new ArrayList<Integer>();
                this.channelGain = new ArrayList<Double>();
                this.channelOffset = new ArrayList<Double>();
                this.channelNumbers = new ArrayList<Integer>();
                this.driverPath = "";
                this.match = "";
                this.name = "";
                this.protocol = USB;
        }

        @Override
        public String toString() {
                return name;
        }

        @Override
        public String getName() {
                return name;
        }

        public List<Integer> getAvailableFrequencies() {
                return availableFrequencies;
        }

        public List<Double> getChannelGain() {
                return channelGain;
        }

        public List<Integer> getChannelNumbers() {
                return channelNumbers;
        }

        public List<Double> getChannelOffset() {
                return channelOffset;
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

        @Override
        public void setName(String name) {
                this.name = name;
        }

        public void setAvailableFrequencies(List<Integer> availableFrequencies) {
                this.availableFrequencies = availableFrequencies;
        }

        public void setChannelGain(List<Double> channelGain) {
                this.channelGain = channelGain;
        }

        public void setChannelNumbers(List<Integer> channelNumbers) {
                this.channelNumbers = channelNumbers;
        }

        public void setChannelOffset(List<Double> channelOffset) {
                this.channelOffset = channelOffset;
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
}