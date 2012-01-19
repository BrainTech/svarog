/* ChannelSpaceSelection.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.space;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.plugin.export.signal.SignalSelection;

/**
 * This class represents the (sub)set of channels.
 * It holds the indexes of these channels in the
 * {@link MultichannelSampleSource source}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSpace {

        /**
         * the set of indexes of selected channels
         */
	private LinkedHashSet<Integer> channels;

        /**
         * Constructor. Creates an empty (sub)set.
         */
	public ChannelSpace() {
		channels = new LinkedHashSet<Integer>();
	}

        /**
         * Constructor. Creates the set with the given channels.
         * @param array an array with the indexes (in the
         * {@link MultichannelSampleSource source}) of selected channels
         */
	public ChannelSpace(int[] array) {
		this();
		replaceChannels(array);
	}

        /**
         * Clears this set of channels and puts there provided channels.
         * @param array an array with the indexes (in the
         * {@link MultichannelSampleSource source}) of selected channels
         */
	public void replaceChannels(int[] array) {
		channels.clear();
		for (int i=0; i<array.length; i++) {
			channels.add(array[i]);
		}
	}

        /**
         * Adds the channel to this set.
         * @param channel the index (in the
         * {@link MultichannelSampleSource source}) of the channel
         */
	public void addChannel(int channel) {
		channels.add(channel);
	}

        /**
         * Removes the channel from this set.
         * @param channel the index (in the
         * {@link MultichannelSampleSource source}) of the channel
         */
	public void removeChannel(int channel) {
		channels.remove(new Integer(channel));
	}

        /**
         * Clears this set.
         */
	public void clear() {
		channels.clear();
	}

        /**
         * Returns the number of channels in this set.
         * @return the number of channels in this set
         */
	public int size() {
		return channels.size();
	}

        /**
         * Checks if the given channel is in this set.
         * @param channel the index (in the
         * {@link MultichannelSampleSource source}) of the channel
         * @return true if the given channel is in this set, false otherwise
         */
	public boolean isChannelSelected(int channel) {
		return channels.contains(channel);
	}

        /**
         * Creates an array with the channels from this set.
         * @return an array with the channels from this set
         */
	public int[] getSelectedChannels() {
		int size = channels.size();
		int[] array = new int[size];
		int cnt = 0;
		Iterator<Integer> it = channels.iterator();
		while (it.hasNext()) {
			array[cnt] = it.next();
			cnt++;
		}
		Arrays.sort(array);
		return array;
	}
        //TODO there is a function to create the array from collection
        //<code>channels.toArray</code>

        /**
         * Returns an array saying if channels of indexes lower then
         * <code>channelCount</code> are in the set.
         * <code>arr[i]<code> - true if channel of index i is in the set,
         * false otherwise
         * @param channelCount the index of the last channel in the created
         * array
         * @return an array saying if channels are in the set
         */
	public boolean[] getChannelSelection(int channelCount) {

		boolean[] selection = new boolean[channelCount];

		for (int i=0; i<channelCount; i++) {
			if (channels.contains(i)) {
				selection[i] = true;
			}
		}

		return selection;

	}

	/**
	 * Returns if all channels in the channel space are selected
	 * @return true if all channels are selected
	 */
	public boolean areAllChannelsSelected() {
		if (getSelectedChannels()[0] == SignalSelection.CHANNEL_NULL)
			return true;
		return false;
	}

}
