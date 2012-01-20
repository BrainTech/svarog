package org.signalml.app.model.document.opensignal;

import multiplexer.jmx.client.JmxClient;


import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.app.view.document.opensignal.SignalSource;
import org.signalml.domain.montage.system.EegSystem;

import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.tag.StyledTagSet;

/**
 * @author Mariusz Podsiadło
 */
public class OpenMonitorDescriptor {

        private String multiplexerAddress;
        private int multiplexerPort = -1;
        private JmxClient jmxClient;
        private JmxClient tagClient;
        private boolean metadataReceived = false;
        private String metadataInfo;
        private Integer channelCount;
        private String[] channelLabels;
        private Object[] selectedChannelList;
        private int[] selectedChannelsIndecies;
        private float pageSize;
        private Float samplingFrequency;
        private float[] calibrationGain;
        private float[] calibrationOffset;
        private Float minimumValue;
        private Float maximumValue;
        private Float backupFrequency;
	/**
	 * An integer value representing amplifier's channel value for non-connected channel.
	 */
        private double amplifierNull;
        private RawSignalSampleType sampleType;
        private RawSignalByteOrder byteOrder;
        /**
         * This {@link MonitorRecordingDescriptor} represents the parameters of
         * the recording which can be performed on this monitor.
         */
        private MonitorRecordingDescriptor monitorRecordingDescriptor;
        /**
         * The signal source - openBCI or amplifier.
         */
        private SignalSource signalSource;

	/**
	 * Styles from this {@link StyledTagSet} are used for the new monitor tags.
	 */
	private StyledTagSet tagStyles;
	/**
	 * The {@link EegSystem} used for this monitor signal.
	 */
	private EegSystem eegSystem;

        public OpenMonitorDescriptor() {
                channelCount = 0;
                sampleType = RawSignalSampleType.DOUBLE;
                byteOrder = RawSignalByteOrder.LITTLE_ENDIAN;
		samplingFrequency = 128.0F;
		pageSize = 20.0F;
                backupFrequency = 10.0F;

                monitorRecordingDescriptor = new MonitorRecordingDescriptor();
        }

        public String getMultiplexerAddress() {
                return multiplexerAddress;
        }

        public void setMultiplexerAddress(String multiplexerAddress) {
                this.multiplexerAddress = multiplexerAddress;
        }

        public int getMultiplexerPort() {
                return multiplexerPort;
        }

        public void setMultiplexerPort(int multiplexerPort) {
                this.multiplexerPort = multiplexerPort;
        }

        public JmxClient getJmxClient() {
                return jmxClient;
        }

        public void setJmxClient(JmxClient jmxClient) {
                this.jmxClient = jmxClient;
        }

        public JmxClient getTagClient() {
                return tagClient;
        }

        public void setTagClient(JmxClient tagClient) {
                this.tagClient = tagClient;
        }

        public boolean isMetadataReceived() {
                return metadataReceived;
        }

        public void setMetadataReceived(boolean metadataReceived) {
                this.metadataReceived = metadataReceived;
        }

        public String getMetadataInfo() {
                return metadataInfo;
        }

        public void setMetadataInfo(String metadataInfo) {
                this.metadataInfo = metadataInfo;
        }

        public Float getSamplingFrequency() {
                return samplingFrequency;
        }

        public void setSamplingFrequency(Float samplingFrequency) {
                this.samplingFrequency = samplingFrequency;
        }

        public Integer getChannelCount() {
                return channelCount;
        }

        /**
         * Returns the number of channels to be monitored (selected in the
         * Open Monitor dialog).
         *
         * @return how many channels are to be monitored.
         */
        public Integer getSelectedChannelsCount() {
                return selectedChannelList.length;
        }

        public void setChannelCount(Integer channelCount) {
                this.channelCount = channelCount;
        }

        public float[] getCalibrationGain() {
                return calibrationGain;
        }

        /**
         * Returns the values of calibration gain for the channels selected in the
         * Open Monitor dialog.
         *
         * @return calibration gain for the selected channels.
         */
        public float[] getSelectedChannelsCalibrationGain() {
                float[] selectedChannelsCalibrationGain = new float[getSelectedChannelList().length];
                int j = 0;

                for (int i : getSelectedChannelsIndecies()) {
                        selectedChannelsCalibrationGain[j] = calibrationGain[i];
                        j++;
                }

                return selectedChannelsCalibrationGain;
        }

        public double[] getGain() {
                double[] result = new double[calibrationGain.length];
                for (int i = 0; i < calibrationGain.length; i++) {
                        result[i] = (double) calibrationGain[i];
                }
                return result;
        }

        public void setCalibrationGain(float[] calibrationGain) {
                this.calibrationGain = calibrationGain;
        }

        public float[] getCalibrationOffset() {
                return calibrationOffset;
        }

        /**
         * Returns the values of calibration offset for the channels selected in the
         * Open Monitor dialog.
         *
         * @return calibration offset for the selected channels.
         */
        public float[] getSelectedChannelsCalibrationOffset() {
                float[] selectedChannelsCalibrationOffset = new float[getSelectedChannelList().length];
                int j = 0;

                for (int i : getSelectedChannelsIndecies()) {
                        selectedChannelsCalibrationOffset[j] = calibrationOffset[i];
                        j++;
                }

                return selectedChannelsCalibrationOffset;
        }

        public double[] getOffset() {
                double[] result = new double[calibrationOffset.length];
                for (int i = 0; i < calibrationOffset.length; i++) {
                        result[i] = (double) calibrationOffset[i];
                }
                return result;
        }

        public void setCalibrationOffset(float[] calibrationOffset) {
                this.calibrationOffset = calibrationOffset;
        }

        public Float getMinimumValue() {
                return minimumValue;
        }

        public void setMinimumValue(Float minimumValue) {
                this.minimumValue = minimumValue;
        }

	/**
	 * Returns an integer value representing amplifier's channel value for non-connected channel
	 * @return an integer value representing amplifier's channel value for non-connected channel
	 */
        public double getAmplifierNull() {
                return this.amplifierNull;
        }

        public void setAmplifierNull(double ampNull) {
                this.amplifierNull = ampNull;
        }

        public Float getMaximumValue() {
                return maximumValue;
        }

        public void setMaximumValue(Float maximumValue) {
                this.maximumValue = maximumValue;
        }

        public String[] getChannelLabels() {
                return channelLabels;
        }

        /**
         * Returns the labels for the channels selected to be monitored in the
         * Open Monitor dialog.
         *
         * @return labels of the selected channels.
         */
        public String[] getSelectedChannelsLabels() {
                String[] selectedChannelLabels = new String[getSelectedChannelList().length];
                int j = 0;

                for (int i : getSelectedChannelsIndecies()) {
                        selectedChannelLabels[j] = channelLabels[i];
                        j++;
                }

                return selectedChannelLabels;
        }

        public void setChannelLabels(String[] channelLabels) {
                this.channelLabels = channelLabels;
        }

        public Object[] getSelectedChannelList() {
                return selectedChannelList;
        }

        private int findLabelIndex(Object label) throws Exception {
                if (label == null) {
                        throw new NullPointerException();
                }
                for (int i = 0; i < channelLabels.length; i++) {
                        if (label.equals(channelLabels[i])) {
                                return i;
                        }
                }
                throw new Exception("Bad argument value: " + label);
        }

        public void setSelectedChannelList(Object[] selectedChannelList) throws Exception {
                this.selectedChannelList = selectedChannelList;
                selectedChannelsIndecies = new int[selectedChannelList.length];
                for (int i = 0; i < selectedChannelList.length; i++) {
                        int n = findLabelIndex(selectedChannelList[i]);
                        selectedChannelsIndecies[i] = n;
                }
        }

        public int[] getSelectedChannelsIndecies() {
                return selectedChannelsIndecies;
        }

        public float getPageSize() {
                return pageSize;
        }

        public void setPageSize(float pageSize) {
                this.pageSize = pageSize;
        }

        public RawSignalSampleType getSampleType() {
                return sampleType;
        }

        public void setSampleType(RawSignalSampleType sampleType) {
                this.sampleType = sampleType;
        }

        public RawSignalByteOrder getByteOrder() {
                return byteOrder;
        }

        public void setByteOrder(RawSignalByteOrder byteOrder) {
                this.byteOrder = byteOrder;
        }

        /**
         * Sets the parameters for this monitor recording.
         * @param monitorRecordingDescriptor an object describing the parameters
         * used to record this monitor.
         */
        public void setMonitorRecordingDescriptor(MonitorRecordingDescriptor monitorRecordingDescriptor) {
                this.monitorRecordingDescriptor = monitorRecordingDescriptor;
        }

        /**
         * Returns the parameters of the recording which can be performed on this
         * monitor.
         * @return the parameters decribing a recording which can be made
         * on this monitor.
         */
        public MonitorRecordingDescriptor getMonitorRecordingDescriptor() {
                return monitorRecordingDescriptor;
        }

        public SignalSource getSignalSource() {
                return signalSource;
        }

        public void setSignalSource(SignalSource signalSource) {
                this.signalSource = signalSource;
        }

        public Float getBackupFrequency() {
                return backupFrequency;
        }

        public void setBackupFrequency(Float backupFrequency) {
                this.backupFrequency = backupFrequency;
        }

	/**
	 * Sets the tag styles that should be used by the new monitor.
	 * @param tagStyles tag styles that should be used
	 */
	public void setTagStyles(StyledTagSet tagStyles) {
		this.tagStyles = tagStyles;
	}

	/**
	 * Returns the tags styles that should be used by the new monitor.
	 * @return tag styles that should be used
	 */
	public StyledTagSet getTagStyles() {
		return tagStyles;
	}

	/**
	 * Returns the EEG system which should be used by this monitor signal
	 * @return EEG system to be used
	 */
	public EegSystem getEegSystem() {
		return eegSystem;
	}

	/**
	 * Sets the EEG system which should be used by this monitor signal
	 * @param eegSystem the EEG system to be used
	 */
	public void setEegSystem(EegSystem eegSystem) {
		this.eegSystem = eegSystem;
	}

}
