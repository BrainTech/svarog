package org.signalml.app.worker.amplifiers;

import java.util.List;

import org.signalml.app.config.preset.Preset;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import org.signalml.app.worker.processes.OpenBCIModule;

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
         * Channels' default names.
         */
        private List<String> defaultNames;

        /**
         * Driver {@link OpenBCIModule} name.
         */
        private String moduleName;

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
                this.defaultNames = new ArrayList<String>();
                this.moduleName = "";
                this.match = "";
                this.name = "";
                this.protocol = USB;
                this.amplifierNull = 0.0;
        }

        /**
         * Creates and returnes a copy of this object.
         *
         * @return copy of this object
         */
        public AmplifierDefinition copy() {

                AmplifierDefinition retval = new AmplifierDefinition();

                String newModuleName = "" + moduleName;
                String newMatch = "" + match;
                String newName = "" + name;
                String newProtocol = "" + protocol;
                Double newAmpNull = new Double(amplifierNull.doubleValue());

                List<Float> newFrequencies = new ArrayList<Float>();
                List<Float> newCalibrationGain = new ArrayList<Float>();
                List<Float> newCalibrationOffset = new ArrayList<Float>();
                List<Integer> newChannelNumbers = new ArrayList<Integer>();
                List<String> newDefaultNames = new ArrayList<String>();

                for (int i = 0; i < availableFrequencies.size(); i++)
                        newFrequencies.add(new Float(availableFrequencies.get(i).floatValue()));

                for (int i = 0; i < getChannelCount(); i++) {
                        newChannelNumbers.add(new Integer(channelNumbers.get(i).intValue()));
                        newCalibrationGain.add(new Float(calibrationGain.get(i).floatValue()));
                        newCalibrationOffset.add(new Float(calibrationOffset.get(i).floatValue()));
                        newDefaultNames.add("" + defaultNames.get(i));
                }

                retval.setAmplifierNull(newAmpNull);
                retval.setAvailableFrequencies(newFrequencies);
                retval.setCalibrationGain(newCalibrationGain);
                retval.setCalibrationOffset(newCalibrationOffset);
                retval.setChannelNumbers(newChannelNumbers);
                retval.setDefaultNames(newDefaultNames);
                retval.setMatch(newMatch);
                retval.setModuleName(newModuleName);
                retval.setName(newName);
                retval.setProtocol(newProtocol);

                return retval;
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

        public List<String> getDefaultNames() {
                return defaultNames;
        }

        public String getModuleName() {
                return moduleName;
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

        public void setModuleName(String moduleName) {
                this.moduleName = moduleName;
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

        public void setDefaultNames(List<String> defaultNames) {
                this.defaultNames = defaultNames;
        }
}