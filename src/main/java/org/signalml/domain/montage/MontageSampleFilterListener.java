/* MontageSampleFilterListener.java created 2008-02-01
 *
 */

package org.signalml.domain.montage;

import java.util.EventListener;

/**
 * Listener interface for adding/removing/changing
 * {@link MontageSampleFilter filters} in a {@link Montage montage}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageSampleFilterListener extends EventListener {

        /**
         * Invoked when a filter is added to a montage
         * @param ev an event object describing a change
         */
	public void filterAdded(MontageSampleFilterEvent ev);

        /**
         * Invoked when a sample filter in a montage is changed
         * @param ev an event object describing a change
         */
	public void filterChanged(MontageSampleFilterEvent ev);

        /**
         * Invoked when a filter is removed from a montage
         * @param ev an event object describing a change
         */
	public void filterRemoved(MontageSampleFilterEvent ev);

        /**
         * Invoked when filters exclusions are changed
         * @param ev an event object describing a change
         */
	public void filterExclusionChanged(MontageSampleFilterEvent ev);

        /**
         * Invoked when all filters are changed
         * @param ev an event object describing a change
         */
	public void filtersChanged(MontageSampleFilterEvent ev);

}
