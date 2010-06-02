package org.signalml.app.model;

import multiplexer.jmx.client.JmxClient;

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
	
    private Integer channelCount = 1;
    private int[] amplifierChannels;
    private String[] channelLabels;
    private Object[] selectedChannelList;
    private int[] selectedChannelsIndecies;
    private Double pageSize;

    private Float samplingFrequency;
    private float[] calibrationGain;
    private float[] calibrationOffset;
    private Float minimumValue;
    private Float maximumValue;

    private String fileName;
    private RawSignalSampleType sampleType;
    private RawSignalByteOrder byteOrder;

	public OpenMonitorDescriptor() {
		// XXX currently all signals are treated as EEG - there is no way to change this in the GUI
		type = SignalType.EEG_10_20;
		channelCount = 1;
        sampleType = RawSignalSampleType.DOUBLE;
        byteOrder = RawSignalByteOrder.LITTLE_ENDIAN;
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

    public void setChannelCount(Integer channelCount) {
        this.channelCount = channelCount;
    }

    public float[] getCalibrationGain() {
        return calibrationGain;
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

    public Float getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(Float maximumValue) {
        this.maximumValue = maximumValue;
    }

    public String[] getChannelLabels() {
        return channelLabels;
    }

    public void setChannelLabels(String[] channelLabels) {
        this.channelLabels = channelLabels;
    }

    public int[] getAmplifierChannels() {
        return amplifierChannels;
    }

    public void setAmplifierChannels( int[] channelNumbers) {
        this.amplifierChannels = channelNumbers;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

}
