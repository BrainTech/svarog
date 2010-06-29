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

import org.signalml.domain.montage.eeg.EegChannel;

/**
 * @author Oskar Kapala &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 *
 */
public class AbstractData implements Serializable {

	private static final long serialVersionUID = 1L;

	public AbstractData() {
		keyChannelMap = new HashMap<String, Integer>();
		eegChannels = new ArrayList<Integer>();
		channelMap = new HashMap<String, Integer>();
	}

	private static final EegChannel[] keyChannels = new EegChannel[] {
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
	};

	public static final Set<EegChannel> keyChannelSet = getKeyChannelSet();

	private static Set<EegChannel> getKeyChannelSet() {
		HashSet<EegChannel> channelSet = new HashSet<EegChannel>();
		for (int i=0; i<keyChannels.length; i++) {
			channelSet.add(keyChannels[i]);
		}
		return Collections.unmodifiableSet(channelSet);
	}

	private Map<String,Integer> keyChannelMap;
	private ArrayList<Integer> eegChannels;

	private Map<String,Integer> channelMap;

	private int[][] excludedChannels;

	public Map<String, Integer> getKeyChannelMap() {
		return keyChannelMap;
	}

	public void setKeyChannelMap(Map<String, Integer> keyChannelMap) {
		this.keyChannelMap = keyChannelMap;
	}

	public ArrayList<Integer> getEegChannels() {
		return eegChannels;
	}

	public void setEegChannels(ArrayList<Integer> eegChannels) {
		this.eegChannels = eegChannels;
	}

	public Map<String, Integer> getChannelMap() {
		return channelMap;
	}

	public void setChannelMap(Map<String, Integer> channelMap) {
		this.channelMap = channelMap;
	}

	public int[][] getExcludedChannels() {
		return excludedChannels;
	}

	public void setExcludedChannels(int[][] excludedChannels) {
		this.excludedChannels = excludedChannels;
	}

}
