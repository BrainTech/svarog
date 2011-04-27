/* MontageSampleFilterEvent.java created 2008-02-01
 *
 */

package org.signalml.domain.montage;

import java.util.EventObject;

/**
 * This class represents an event associated with change, addition or removal
 * of a {@link MontageSampleFilter sample filter} from a {@link Montage montage}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageSampleFilterEvent extends EventObject {

	private static final long serialVersionUID = 1L;

        /**
         * indexes of {@link MontageSampleFilter filters} that were
         * changed/added/removed
         */
	private int[] indices;

        /**
         * Constructor. Creates an event associated with a given object.
         * @param source a {@link Montage montage} on which the Event initially
         * occurred
         */
	public MontageSampleFilterEvent(Object source) {
		super(source);
	}

        /**
         * Constructor. Creates an event associated with a given
         * {@link Montage montage} and a list of filters indexes.
         * @param source a montage on which the Event initially occurred.
         * @param indices an array with indexes of
         * {@link MontageSampleFilter filters} that were changed/added/removed
         */
	public MontageSampleFilterEvent(Object source, int[] indices) {
		super(source);
		this.indices = indices;
	}

        /**
         * Returns an array with indexes of {@link MontageSampleFilter filters}
         * that were changed/added/removed
         * @return an array with indexes of filters that were
         * changed/added/removed
         */
	public int[] getIndices() {
		return indices;
	}

}
