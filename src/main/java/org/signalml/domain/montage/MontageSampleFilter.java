/* MontageSampleFilter.java created 2008-02-01
 *
 */

package org.signalml.domain.montage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.signalml.domain.montage.filter.SampleFilterDefinition;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MontageSampleFilter
 * Class representing sample filter for montage
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("filter")
public class MontageSampleFilter {

        /**
         * Definition of a filter. Contains most parameters of the filter
         */
	private SampleFilterDefinition definition;

        /**
         * Tells if filter is enabled
         */
	private boolean enabled = true;

        /**
         * list of channels excluded from filter
         */
	private ArrayList<MontageChannel> excludedChannels = new ArrayList<MontageChannel>();

        /**
         * Constructor. Creates an empty filter (without definition)
         */
	protected MontageSampleFilter() {
	}

        /**
         * Constructor. Creates filter based on given definition
         * @param definition definition of a filter
         */
	public MontageSampleFilter(SampleFilterDefinition definition) {
		this.definition = definition;
	}

        /**
         * Copy constructor. Creates new filter (in another montage) based on given.
         * @param filter filter to be copied
         * @param montageChannels channels of a montage in which created filter will be located
         * @param filterMontageChannels channels of a montage in which filter that is being copied is located
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
         *
         * @return definition of a filter
         */
	public SampleFilterDefinition getDefinition() {
		return definition;
	}

        /**
         * Sets definition of a filter to a given value
         * @param definition value to be set
         */
	public void setDefinition(SampleFilterDefinition definition) {
		this.definition = definition;
	}

        /**
         * Clears all exclusions. After this operation no channel is excluded
         * @return true if there were any exclusion to be cleared, false otherwise
         */
	public boolean clearExclusion() {
		boolean done = !excludedChannels.isEmpty();
		excludedChannels.clear();
		return done;
	}

        /**
         *
         * @return number of channels excluded
         */
	public int getExcludedChannelCount() {
		return excludedChannels.size();
	}

        /**
         *
         * @param index index in excludedChannels array
         * @return MontageChannel of a given index in excludedChannels array
         */
	public MontageChannel getExcludedChannelAt(int index) {
		return excludedChannels.get(index);
	}

        /**
         * Returns iterator for collection of excluded channels
         * @return iterator for collection of excluded channels
         */
	public Iterator<MontageChannel> getExcludedChannelIterator() {
		return excludedChannels.iterator();
	}

        /**
         * Checks if given channel is excluded from filter
         * @param channel channel to be checked
         * @return true if channel is excluded, false otherwise
         */
	public boolean isChannelExcluded(MontageChannel channel) {
		return excludedChannels.contains(channel);
	}

        /**
         * Adds given channels to excluded channels
         * @param channel channel to be excluded
         * @return true
         */
	public boolean addExcludedChannel(MontageChannel channel) {
		return excludedChannels.add(channel);
	}

        /**
         * Removes given channel from excluded channels
         * @param channel channel to be removed from excluded
         * @return true if this list contained the specified element, false otherwise
         */
	public boolean removeExcludedChannel(MontageChannel channel) {
		return excludedChannels.remove(channel);
	}

        /**
         * Removes given channel with a given index on excludedChannels list from excluded channels
         * @param index index of a channel to be removed from excluded
         * @return removed element
         */
	public MontageChannel removeExcludedChannel(int index) {
		return excludedChannels.remove(index);
	}

        /**
         * Returns array of indexes (in a given list) of excluded channels
         * @param montageChannels list of montage channels from which indexes will be used
         * @return array of indexes (in a given list) of excluded channels
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
         *
         * @return true if filter is enabled, false otherwise
         */
	public boolean isEnabled() {
		return enabled;
	}

        /**
         * Set if filter should be enabled
         * @param enabled true if filter should be enabled, false otherwise
         */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
