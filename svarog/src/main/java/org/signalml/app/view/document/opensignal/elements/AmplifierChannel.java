/* AmplifierChannel.java created 2011-03-23
 *
 */

package org.signalml.app.view.document.opensignal.elements;

/**
 * An amplifier channel description for the select ChannelsSelectTable in the
 * AmplfierSignalSourcePanel.
 *
 * @author Piotr Szachewicz
 */
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
	void setSelected(Boolean selected) {
		this.selected = selected;
	}

}
