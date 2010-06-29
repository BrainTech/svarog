/* MontageSampleFilterEvent.java created 2008-02-01
 *
 */

package org.signalml.domain.montage;

import java.util.EventObject;

/** MontageSampleFilterEvent
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageSampleFilterEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int[] indices;

	public MontageSampleFilterEvent(Object source) {
		super(source);
	}

	public MontageSampleFilterEvent(Object source, int[] indices) {
		super(source);
		this.indices = indices;
	}

	public int[] getIndices() {
		return indices;
	}

}
