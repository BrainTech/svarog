/* AmplifierChannel.java created 2011-03-23
 *
 */

package org.signalml.app.view.document.opensignal.elements;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * An amplifier channel description for the select ChannelsSelectTable in the
 * AmplfierSignalSourcePanel.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias(value="channel")
public class AmplifierChannel {

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
	
	private float calibrationGain;
	private float calibrationOffset;

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

}
