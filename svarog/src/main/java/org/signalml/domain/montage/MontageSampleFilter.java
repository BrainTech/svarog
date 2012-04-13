/* MontageSampleFilter.java created 2008-02-01
 *
 */

package org.signalml.domain.montage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.signalml.domain.montage.filter.SampleFilterDefinition;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class contains a description of a sample filter for a {@link Montage montage}.
 * This description includes the {@link SampleFilterDefinition definition} of a filter,
 * an indicator whether it is enabled or not and a list of
 * {@link MontageChannel montage channels} that are excluded from
 * this filter.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("filter")
public class MontageSampleFilter {

	/**
	 * Definition of a filter. Contains most parameters of this filter.
	 */
	private SampleFilterDefinition definition;

	/**
	 * tells if the filter is enabled
	 */
	private boolean enabled = true;

	/**
	 * the list of channels excluded from this filter
	 */
	private ArrayList<MontageChannel> excludedChannels = new ArrayList<MontageChannel>();

	/**
	 * Constructor. Creates an empty filter (without definition).
	 */
	protected MontageSampleFilter() {
	}

	/**
	 * Constructor. Creates a filter based on a given definition.
	 * @param definition a definition of a filter
	 */
	public MontageSampleFilter(SampleFilterDefinition definition) {
		this.definition = definition;
	}

	/**
	 * Copy constructor. Creates a new filter (in different
	 * {@link Montage montage}) based on given.
	 * @param filter a filter to be copied
	 * @param montageChannels {@link MontageChannel channels} of a montage
	 * in which a created filter will be located
	 * @param filterMontageChannels channels of a montage in which the filter
	 * that is being copied is located
	 */
	public MontageSampleFilter(MontageSampleFilter filter, ArrayList<MontageChannel> montageChannels, ArrayList<MontageChannel> filterMontageChannels) {
		this();

		this.definition = filter.definition.duplicate();
		this.enabled = filter.enabled;

		MontageChannel filterChannel;
		MontageChannel channel;
		int index;
		int size = montageChannels.size();

		Iterator<MontageChannel> it = filter.excludedChannels.iterator();
		while (it.hasNext()) {

			filterChannel = it.next();
			index = filterMontageChannels.indexOf(filterChannel);
			if (index >= 0 && index < size) {

				channel = montageChannels.get(index);
				if (channel != null) {
					excludedChannels.add(channel);
				}

			}

		}

	}

	/**
	 * Returns the {@link SampleFilterDefinition definition} of this filter.
	 * @return the definition of this filter
	 */
	public SampleFilterDefinition getDefinition() {
		return definition;
	}

	/**
	 * Sets the {@link SampleFilterDefinition definition} of this filter
	 * to a given value.
	 * @param definition the value to be set
	 */
	public void setDefinition(SampleFilterDefinition definition) {
		this.definition = definition;
	}

	/**
	 * Clears all exclusions. After this operation no channel is excluded.
	 * @return true if there were any exclusion to be cleared, false
	 * otherwise
	 */
	public boolean clearExclusion() {
		boolean done = !excludedChannels.isEmpty();
		excludedChannels.clear();
		return done;
	}

	/**
	 * Returns the number of channels that are excluded.
	 * @return the number of channels that are excluded
	 */
	public int getExcludedChannelCount() {
		return excludedChannels.size();
	}

	/**
	 * Returns the {@link MontageChannel montage channel} of a given index
	 * in an {@link #excludedChannels} array.
	 * @param index an index in an excludedChannels array
	 * @return a montage channel of a given index in the
	 * excludedChannels array
	 */
	public MontageChannel getExcludedChannelAt(int index) {
		return excludedChannels.get(index);
	}

	/**
	 * Returns an iterator for the collection of excluded channels.
	 * @return an iterator for the collection of excluded channels
	 */
	public Iterator<MontageChannel> getExcludedChannelIterator() {
		return excludedChannels.iterator();
	}

	/**
	 * Checks if the given channel is excluded from this filter.
	 * @param channel the channel to be checked
	 * @return true if the channel is excluded, false otherwise
	 */
	public boolean isChannelExcluded(MontageChannel channel) {
		return excludedChannels.contains(channel);
	}

	/**
	 * Adds a given {@link MontageChannel channel} to the collection of
	 * excluded channels.
	 * @param channel a channel to be excluded
	 * @return true
	 */
	public boolean addExcludedChannel(MontageChannel channel) {
		return excludedChannels.add(channel);
	}

	/**
	 * Removes a given {@link MontageChannel channel} from excluded channels.
	 * @param channel a channel to be removed from excluded
	 * @return true if this list contained the specified element,
	 * false otherwise
	 */
	public boolean removeExcludedChannel(MontageChannel channel) {
		return excludedChannels.remove(channel);
	}

	/**
	 * Removes a given {@link MontageChannel channel} with a given index
	 * in the {@link #excludedChannels} list from excluded channels.
	 * @param index an index of a channel to be removed from excluded
	 * @return a removed channel
	 */
	public MontageChannel removeExcludedChannel(int index) {
		return excludedChannels.remove(index);
	}

	/**
	 * Returns an array of indexes (in the given list) of excluded
	 * {@link MontageChannel channels}.
	 * @param montageChannels a list of montage channels from which indexes
	 * will be used
	 * @return an array of indexes (in a given list) of excluded channels
	 */
	public boolean[] getExclusionArray(ArrayList<MontageChannel> montageChannels) {

		boolean[] result = new boolean[montageChannels.size()];

		Arrays.fill(result, false);

		Iterator<MontageChannel> it = excludedChannels.iterator();
		int index;
		while (it.hasNext()) {

			index = montageChannels.indexOf(it.next());
			if (index >= 0) {
				result[index] = true;
			}

		}

		return result;

	}

	/**
	 * Returns if this filter is enabled.
	 * @return true if the filter is enabled, false otherwise
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Set if this filter should be enabled.
	 * @param enabled true if this filter should be enabled, false otherwise
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
