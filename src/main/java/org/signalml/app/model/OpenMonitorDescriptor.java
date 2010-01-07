package org.signalml.app.model;

import multiplexer.jmx.client.JmxClient;

import org.signalml.domain.signal.SignalType;

/** 
 * @author Mariusz Podsiadło 
 */
public class OpenMonitorDescriptor {

	private SignalType type;
	
	private String multiplexerAddress;
	private int multiplexerPort = -1;
	private JmxClient jmxClient;
    
    private Float samplingFrequency;
    private Integer channelCount = 1;
    private Float calibrationGain;
    private Float calibrationOffset;
    
    private String[] channelLabels;
    
    private Float pageSize;

	public OpenMonitorDescriptor() {
		// XXX currently all signals are treated as EEG - there is no way to change this in the GUI
		type = SignalType.EEG_10_20;
		channelCount = 1;
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

    public SignalType getType() {
		return type;
	}

    public void setType(SignalType type) {
		this.type = type;
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

    public void setChannelCount(Integer channelCount) {
        this.channelCount = channelCount;
    }

    public Float getCalibrationGain() {
        return calibrationGain;
    }

    public void setCalibrationGain(Float calibrationGain) {
        this.calibrationGain = calibrationGain;
    }

    public Float getCalibrationOffset() {
        return calibrationOffset;
    }

    public void setCalibrationOffset(Float calibrationOffset) {
        this.calibrationOffset = calibrationOffset;
    }

    public String[] getChannelLabels() {
        return channelLabels;
    }

    public void setChannelLabels(String[] channelLabels) {
        this.channelLabels = channelLabels;
    }

    public Float getPageSize() {
        return pageSize;
    }

    public void setPageSize(Float pageSize) {
        this.pageSize = pageSize;
    }

}
