/* MontageSampleFilterListener.java created 2008-02-01
 *
 */

package org.signalml.domain.montage;

import java.util.EventListener;

/**
 * This interface represents an event listener associated with
 * adding/removing/changing {@link MontageSampleFilter filters} in
 * a {@link Montage montage}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageSampleFilterListener extends EventListener {

	/**
	 * Invoked when a {@link MontageSampleFilter filter} is added to
	 * a {@link Montage montage}.
	 * @param ev an event object describing a change
	 */
	public void filterAdded(MontageSampleFilterEvent ev);

	/**
	 * Invoked when a {@link MontageSampleFilter filter} in
	 * a {@link Montage montage} is changed.
	 * @param ev an event object describing a change
	 */
	public void filterChanged(MontageSampleFilterEvent ev);

	/**
	 * Invoked when a {@link MontageSampleFilter filter} is removed from
	 * a {@link Montage montage}.
	 * @param ev an event object describing a change
	 */
	public void filterRemoved(MontageSampleFilterEvent ev);

	/**
	 * Invoked when {@link MontageSampleFilter filters} exclusions are
	 * changed.
	 * @param ev an event object describing a change
	 */
	public void filterExclusionChanged(MontageSampleFilterEvent ev);

	/**
	 * Invoked when all {@link MontageSampleFilter filters} are changed.
	 * @param ev an event object describing a change
	 */
	public void filtersChanged(MontageSampleFilterEvent ev);

}
