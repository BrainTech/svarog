package org.signalml.app.model;

import org.signalml.app.worker.amplifiers.AmplifierDefinition;

/**
 * OpenMonitorDescriptor suited for amplifier connection.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierConnectionOpenMonitorDescriptor extends OpenMonitorDescriptor {

        public void setSelectedChannelsIndecies(int[] selectedChannelsIndecies) {
                this.selectedChannelsIndecies = selectedChannelsIndecies;
        }

        @Override
        public void setSelectedChannelList(Object[] selectedChannelList) throws Exception {
                this.selectedChannelList = selectedChannelList;
        }

        /**
         * Fills this object from an {@link AmplifierDefinition} object.
         *
         * @param definition the definition
         */
        public void fillFromAnAmplifierDefinition(AmplifierDefinition definition) {

                channelCount = definition.getChannelCount();

                samplingFrequency = definition.getAvailableFrequencies().get(0);

                channelLabels = new String[channelCount];
                selectedChannelList = new String[channelCount];
                selectedChannelsIndecies = new int[channelCount];
                calibrationGain = new float[definition.getChannelCount()];
                calibrationOffset = new float[definition.getChannelCount()];

                for (int i = 0; i < definition.getChannelCount(); i++) {

                        calibrationGain[i] = definition.getCalibrationGain().get(i);
                        calibrationOffset[i] = definition.getCalibrationOffset().get(i);
                        selectedChannelsIndecies[i] = definition.getChannelNumbers().get(i);

                        channelLabels[i] = Integer.toBinaryString(i);
                        selectedChannelList[i] = channelLabels[i];
                }
        }
}
