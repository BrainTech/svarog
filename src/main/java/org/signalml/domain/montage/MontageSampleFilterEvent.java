/* MontageSampleFilterEvent.java created 2008-02-01
 *
 */

package org.signalml.domain.montage;

import java.util.EventObject;

/** MontageSampleFilterEvent
 * Class representing event associated with change, addition of removal of a sample filter from a montage
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageSampleFilterEvent extends EventObject {

	private static final long serialVersionUID = 1L;

        /**
         * Indexes of filters that were changed/added/removed
         */
	private int[] indices;

        /**
         * Constructor. Creates event associated with a given object.
         * @param source montage on which the Event initially occurred.
         */
	public MontageSampleFilterEvent(Object source) {
		super(source);
	}

        /**
         * Constructor. Creates event associated with a given montage and list of filters indexes.
         * @param source montage on which the Event initially occurred.
         * @param indices array with indexes of filters that were changed/added/removed
         */
	public MontageSampleFilterEvent(Object source, int[] indices) {
		super(source);
		this.indices = indices;
	}

        /**
         *
         * @return array with indexes of filters that were changed/added/removed
         */
	public int[] getIndices() {
		return indices;
	}

}
