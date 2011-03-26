/* AmplifierChannels.java created 2011-03-24
 *
 */

package org.signalml.app.view.opensignal.elements;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Piotr Szachewicz
 */
public class AmplifierChannels {

	private List<AmplifierChannel> channels = new ArrayList<AmplifierChannel>();

	public AmplifierChannels() {
	}

	public AmplifierChannels(List<Integer> channelNumbers, String[] channelLabels) {
		Integer number;
                String label;
		for (int i = 0; i < channelNumbers.size(); i++) {
			number = channelNumbers.get(i);
                        label = channelLabels[i];
			channels.add(new AmplifierChannel(number, label));
		}
	}

	public int getNumberOfChannels() {
		return channels.size();
	}

	public List<AmplifierChannel> getSelectedChannels() {
		List<AmplifierChannel> selectedChannels = new ArrayList<AmplifierChannel>();

		for (AmplifierChannel channel: channels)
			if (channel.isSelected())
				selectedChannels.add(channel);

		return selectedChannels;
	}

	public List<AmplifierChannel> getAllChannels() {
		return channels;
	}

	public AmplifierChannel getChannel(int i) {
		return channels.get(i);
	}

	public void setAllSelected(boolean selected) {
		for (AmplifierChannel channel: channels)
			channel.setSelected(selected);
	}

	public String[] getAllChannelsLabels() {
		return getChannelsLabels(channels);
	}

	public String[] getSelectedChannelsLabels() {
		List<AmplifierChannel> selectedChannels = getSelectedChannels();
		return getChannelsLabels(selectedChannels);
	}

	protected String[] getChannelsLabels(List<AmplifierChannel> ampChannels) {
		String[] labels = new String[ampChannels.size()];

		for (int i = 0; i < ampChannels.size(); i++)
			labels[i] = ampChannels.get(i).getName();

		return labels;
	}

}
