package org.signalml.app.model.document.opensignal.elements;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * An amplifier channel description for the select ChannelsSelectTable in the
 * AmplfierSignalSourcePanel.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias(value="channel")
public class AmplifierChannel {

	public static String DRIVER_SAW_CHANNEL_NAME = "Driver_Saw";
	public static String AMPLIFIER_SAW_CHANNEL_NAME = "Saw";
	public static String TRIGGER_CHANNEL_NAME = "trig";
	public static String ONOFF_CHANNEL_NAME = "onoff";
	public static String BATTERY_CHANNEL_NAME = "bat";

	public static final Set<String> SPECIAL_CHANNEL_NAMES = new HashSet<String>(Arrays.asList(
     					new String[] {
						DRIVER_SAW_CHANNEL_NAME,
						AMPLIFIER_SAW_CHANNEL_NAME,
						TRIGGER_CHANNEL_NAME,
						ONOFF_CHANNEL_NAME,
						BATTERY_CHANNEL_NAME
		}));

	/**
	 * Determines whether this channel is selected to be shown or not.
	 */
	private Boolean selected = Boolean.TRUE;

	/**
	 * The number of the channel.
	 */
	private int number;

	/**
	 * The label representing this channel.
	 */
	private String label;

	private String originalName;

	private float calibrationGain;
	private float calibrationOffset;
	private double idle;

	public AmplifierChannel() {
	}

	public AmplifierChannel(AmplifierChannel channel) {
		this.selected = channel.selected;
		this.number = channel.number;
		this.label = channel.label;
		this.calibrationGain = channel.calibrationGain;
		this.calibrationOffset = channel.calibrationOffset;
		this.originalName = channel.originalName;
		this.idle = channel.idle;
	}

	/**
	 * Constructor.
	 * @param number the channel number
	 * @param channelName the channel name
	 */
	public AmplifierChannel(int number, String channelName) {
		this.number = number;
		this.label = channelName;
	}

	/**
	 * Returns the channel label.
	 * @return the channel label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the channel label.
	 * @param label the new channel label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the number of this channel.
	 * @return the number of this channel
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Returns whether this channel is selected to be monitored or not.
	 * @return true if this channel is selected, false otherwise
	 */
	public Boolean isSelected() {
		return selected;
	}

	/**
	 * Sets this channel to be selected/unselected.
	 * @param selected the selecton status of this channel
	 */
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public float getCalibrationGain() {
		return calibrationGain;
	}

	public void setCalibrationGain(float calibrationGain) {
		this.calibrationGain = calibrationGain;
	}

	public float getCalibrationOffset() {
		return calibrationOffset;
	}

	public void setCalibrationOffset(float calibrationOffset) {
		this.calibrationOffset = calibrationOffset;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public String getOriginalName() {
		return originalName;
	}

	public double getIdle() {
		return idle;
	}

	public void setIdle(double idle) {
		this.idle = idle;
	}

}
