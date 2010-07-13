/* MontageAdapter.java created 2007-11-24
 *
 */

package org.signalml.domain.montage;

/**
 * Class representing listeners associated with source montages, montages and sample filters
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageAdapter implements SourceMontageListener, MontageListener, MontageSampleFilterListener {

        /**
         * Invoked when a source channel is added to a montage
         * @param ev an event object describing a change
         */
	@Override
	public void sourceMontageChannelAdded(SourceMontageEvent ev) {
	}

        /**
         * Invoked when a source channel is changed
         * @param ev an event object describing a change
         */
	@Override
	public void sourceMontageChannelChanged(SourceMontageEvent ev) {
	}

        /**
         * Invoked when a source channel is removed from a montage
         * @param ev an event object describing a change
         */
	@Override
	public void sourceMontageChannelRemoved(SourceMontageEvent ev) {
	}

        /**
         * Invoked when montage channels are added to a montage
         * @param ev an event object describing a change
         */
	@Override
	public void montageChannelsAdded(MontageEvent ev) {
	}

        /**
         * Invoked when montage channels are changed
         * @param ev an event object describing a change
         */
	@Override
	public void montageChannelsChanged(MontageEvent ev) {
	}

        /**
         * Invoked when montage channels are removed from a montage
         * @param ev an event object describing a change
         */
	@Override
	public void montageChannelsRemoved(MontageEvent ev) {
	}

        /**
         * Invoked when references of montage channels are changed
         * @param ev an event object describing a change
         */
	@Override
	public void montageReferenceChanged(MontageEvent ev) {
	}

        /**
         * Invoked when structure of a montage is changed
         * @param ev an event object describing a change
         */
	@Override
	public void montageStructureChanged(MontageEvent ev) {
	}

        /**
         * Invoked when a filter is added to a montage
         * @param ev an event object describing a change
         */
	@Override
	public void filterAdded(MontageSampleFilterEvent ev) {
	}

        /**
         * Invoked when a sample filter in a montage is changed
         * @param ev an event object describing a change
         */
	@Override
	public void filterChanged(MontageSampleFilterEvent ev) {
	}

        /**
         * Invoked when filters exclusions are changed
         * @param ev an event object describing a change
         */
	@Override
	public void filterExclusionChanged(MontageSampleFilterEvent ev) {
	}

        /**
         * Invoked when a filter is removed from a montage
         * @param ev an event object describing a change
         */
	@Override
	public void filterRemoved(MontageSampleFilterEvent ev) {
	}

        /**
         * Invoked when all filters are changed
         * @param ev an event object describing a change
         */
	@Override
	public void filtersChanged(MontageSampleFilterEvent ev) {
	}


}
