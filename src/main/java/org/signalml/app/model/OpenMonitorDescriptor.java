package org.signalml.app.model;

import multiplexer.jmx.client.JmxClient;
import org.signalml.app.worker.amplifiers.AmplifierDefinition;

import org.signalml.domain.signal.SignalType;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;

/**
 * @author Mariusz Podsiadło
 */
public class OpenMonitorDescriptor {

	private SignalType type;

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
	private Double pageSize;

	private Float samplingFrequency;
	private float[] calibrationGain;
	private float[] calibrationOffset;
	private Float minimumValue;
	private Float maximumValue;

	/**
	 * An integer value representing amplifier`s channel value for non-connected channel.
	 */
	private double amplifierNull;

	private RawSignalSampleType sampleType;
	private RawSignalByteOrder byteOrder;

	/**
	 * This {@link MonitorRecordingDescriptor} represents the parameters of
	 * the recording which can be performed on this monitor.
	 */
	private MonitorRecordingDescriptor monitorRecordingDescriptor;

	public OpenMonitorDescriptor() {
		// XXX currently all signals are treated as EEG - there is no way to change this in the GUI
		type = SignalType.EEG_10_20;
		channelCount = 0;
		sampleType = RawSignalSampleType.DOUBLE;
		byteOrder = RawSignalByteOrder.LITTLE_ENDIAN;

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

	public SignalType getType() {
		return type;
	}

	public void setType(SignalType type) {
		this.type = type;
	}

	public Float getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency( Float samplingFrequency) {
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
		float [] selectedChannelsCalibrationGain = new float[getSelectedChannelList().length];
		int j = 0;

		for(int i: getSelectedChannelsIndecies()) {
			selectedChannelsCalibrationGain[j] = calibrationGain[i];
			j++;
		}

		return selectedChannelsCalibrationGain;
	}

	public double[] getGain() {
		double[] result = new double[calibrationGain.length];
		for (int i=0; i<calibrationGain.length; i++)
			result[i] = (double) calibrationGain[i];
		return result;
	}

	public void setCalibrationGain( float[] calibrationGain) {
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
		float [] selectedChannelsCalibrationOffset = new float[getSelectedChannelList().length];
		int j = 0;

		for(int i: getSelectedChannelsIndecies()) {
			selectedChannelsCalibrationOffset[j] = calibrationOffset[i];
			j++;
		}

		return selectedChannelsCalibrationOffset;
	}

	public double[] getOffset() {
		double[] result = new double[calibrationOffset.length];
		for (int i=0; i<calibrationOffset.length; i++)
			result[i] = (double) calibrationOffset[i];
		return result;
	}

	public void setCalibrationOffset( float[] calibrationOffset) {
		this.calibrationOffset = calibrationOffset;
	}

	public Float getMinimumValue() {
		return minimumValue;
	}

	public void setMinimumValue(Float minimumValue) {
		this.minimumValue = minimumValue;
	}

	/**
	 * Returns an integer value representing amplifier`s channel value for non-connected channel
	 * @return an integer value representing amplifier`s channel value for non-connected channel
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
		String [] selectedChannelLabels = new String[getSelectedChannelList().length];
		int j = 0;

		for(int i: getSelectedChannelsIndecies()) {
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

	private int findLabelIndex( Object label) throws Exception {
		if (label == null)
			throw new NullPointerException();
		for (int i=0; i<channelLabels.length; i++)
			if (label.equals( channelLabels[i]))
				return i;
		throw new Exception( "Bad argument value: " + label);
	}

	public void setSelectedChannelList(Object[] selectedChannelList) throws Exception {
		this.selectedChannelList = selectedChannelList;
		selectedChannelsIndecies = new int[selectedChannelList.length];
		for (int i=0; i<selectedChannelList.length; i++) {
			int n = findLabelIndex( selectedChannelList[i]);
			selectedChannelsIndecies[i] = n;
		}
	}

	public int[] getSelectedChannelsIndecies() {
		return selectedChannelsIndecies;
	}

	public Double getPageSize() {
		return pageSize;
	}

	public void setPageSize( Double pageSize) {
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

                        selectedChannelsIndecies[i] = i;
                        channelLabels[i] = Integer.toBinaryString(i);
                        selectedChannelList[i] = channelLabels[i];
                }                
        }

}
