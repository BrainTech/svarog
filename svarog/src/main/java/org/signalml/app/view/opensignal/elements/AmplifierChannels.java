/* AmplifierChannels.java created 2011-03-24
 *
 */

package org.signalml.app.view.opensignal.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a list of AmplifierChannels for the use of the ChannelSelectTable.
 *
 * @author Piotr Szachewicz
 */
public class AmplifierChannels {

	/**
	 * A list containing amplifier channels.
	 */
	private List<AmplifierChannel> channels = new ArrayList<AmplifierChannel>();

	/**
	 * Constructor.
	 */
	public AmplifierChannels() {
	}

	/**
	 * Constructor. Creates a new AmplifierChannels represented by the given
	 * channel numbers and channel labels.
	 * @param channelNumbers the channel numbers of the channels which will
	 * be contained in this AmplifierChannels
	 * @param channelLabels the channel labels of the channels which will be
	 * contained in this AmplifierChannels
	 */
	public AmplifierChannels(List<Integer> channelNumbers, String[] channelLabels) {
		Integer number;
                String label;
		for (int i = 0; i < channelNumbers.size(); i++) {
			number = channelNumbers.get(i);
                        label = channelLabels[i];
			channels.add(new AmplifierChannel(number, label));
		}
	}

	/**
	 * Returns the number of channels stored in this AmplfierChannels.
	 * @return the number of channels stored in this AmplfierChannels
	 */
	public int getNumberOfChannels() {
		return channels.size();
	}

	/**
	 * Returns the selected channels from this channels list.
	 * @return the selected channels from this channels list
	 */
	public List<AmplifierChannel> getSelectedChannels() {
		List<AmplifierChannel> selectedChannels = new ArrayList<AmplifierChannel>();

		for (AmplifierChannel channel: channels)
			if (channel.isSelected())
				selectedChannels.add(channel);

		return selectedChannels;
	}

	/**
	 * Returns channels stored in this AmplifierChannels.
	 * @return channels stored in this AmplifierChannels
	 */
	public List<AmplifierChannel> getAllChannels() {
		return channels;
	}

	/**
	 * Returns i-th channel on the channel list.
	 * @param i the index of channel on the channel list
	 * @return the i-th channel
	 */
	public AmplifierChannel getChannel(int i) {
		return channels.get(i);
	}

	/**
	 * Sets all channels in the selected/unselected state.
	 * @param selected the new state for all channels
	 */
	public void setAllSelected(boolean selected) {
		for (AmplifierChannel channel: channels)
			channel.setSelected(selected);
	}

	/**
	 * Returns the labels of all channels on the list.
	 * @return the labels of all channels on the list
	 */
	public String[] getAllChannelsLabels() {
		return getChannelsLabels(channels);
	}

	/**
	 * Returns the labels of the selected channels on the list.
	 * @return the labels of the selected channels on the list
	 */
	public String[] getSelectedChannelsLabels() {
		List<AmplifierChannel> selectedChannels = getSelectedChannels();
		return getChannelsLabels(selectedChannels);
	}

	/**
	 * Returns an array of channels labels for the given list of channels.
	 * @param ampChannels the channels for which an array of channel labels
	 * will be found
	 * @return an array of labels for channels in the given list
	 */
	protected String[] getChannelsLabels(List<AmplifierChannel> ampChannels) {
		String[] labels = new String[ampChannels.size()];

		for (int i = 0; i < ampChannels.size(); i++)
			labels[i] = ampChannels.get(i).getLabel();

		return labels;
	}

}
