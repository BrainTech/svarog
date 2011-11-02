/**
 *
 */
package org.signalml.method;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.signalml.domain.montage.system.ChannelFunction;

/**
 * This is abstract class which should be extended by classes acting as data
 * to be processed by methods for SignalML.
 *
 * @author Oskar Kapala &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 *
 */
public class AbstractData implements Serializable {

	private static final long serialVersionUID = 1L;

        /**
         * Constructs new empy Data.
         */
	public AbstractData() {
		keyChannelMap = new HashMap<String, Integer>();
		eegChannels = new ArrayList<Integer>();
		channelMap = new HashMap<String, Integer>();
	}

	private static final ChannelFunction[] keyChannels = ChannelFunction.values();
		/*new ChannelFunction[] {
	        EegChannel.ECG,
	        EegChannel.F7,
	        EegChannel.F8,
	        EegChannel.T3,
	        EegChannel.T4,
	        EegChannel.FP1,
	        EegChannel.FP2,
	        EegChannel.EOGL,
	        EegChannel.EOGP,
	        EegChannel.C3,
	        EegChannel.C4,
	        EegChannel.F3,
	        EegChannel.F4,
	        EegChannel.EMG,
	        EegChannel.A1,
	        EegChannel.A2
	};*/

        /**
         * Unmodifiable Set of EEG channels.
         */
	public static final Set<ChannelFunction> keyChannelSet = getKeyChannelSet();

	private static Set<ChannelFunction> getKeyChannelSet() {
		HashSet<ChannelFunction> channelSet = new HashSet<ChannelFunction>();
		for (int i=0; i<keyChannels.length; i++) {
			channelSet.add(keyChannels[i]);
		}
		return Collections.unmodifiableSet(channelSet);
	}

	private Map<String,Integer> keyChannelMap;
	private ArrayList<Integer> eegChannels;

	private Map<String,Integer> channelMap;

	private int[][] excludedChannels;

        /**
         * Returns map of channels.
         * @return map of channels
         */
	public Map<String, Integer> getKeyChannelMap() {
		return keyChannelMap;
	}

        /**
         * Sets map of channels.
         * @param keyChannelMap map to be set as map of channels
         */
	public void setKeyChannelMap(Map<String, Integer> keyChannelMap) {
		this.keyChannelMap = keyChannelMap;
	}

        /**
         * Returns list of EEG channels.
         * @return list of EEG channels
         */
	public ArrayList<Integer> getEegChannels() {
		return eegChannels;
	}

        /**
         * Sets EEG channels.
         * @param eegChannels list to be set as EEG channels
         */
	public void setEegChannels(ArrayList<Integer> eegChannels) {
		this.eegChannels = eegChannels;
	}

        /**
         * Returns map of channels.
         * @return map of channels
         */
	public Map<String, Integer> getChannelMap() {
		return channelMap;
	}

        /**
         * Sets map of channels.
         * @param channelMap map to be set as map of channels
         */
	public void setChannelMap(Map<String, Integer> channelMap) {
		this.channelMap = channelMap;
	}

        /**
         * Returns array of excluded channels.
         * @return array of excluded channels
         */
	public int[][] getExcludedChannels() {
		return excludedChannels;
	}

        /**
         * Sets array of excluded channels.
         * @param excludedChannels array to be set as excluded channels
         */
	public void setExcludedChannels(int[][] excludedChannels) {
		this.excludedChannels = excludedChannels;
	}

}
