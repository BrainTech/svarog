/* BookEvent.java created 2008-02-23
 *
 */

package org.signalml.domain.book;

import java.util.EventObject;

/** BookEvent
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int channelIndex;
	private int segmentIndex;
	private int atomIndex;

	public BookEvent(MutableBook source) {
		super(source);
	}

	public BookEvent(MutableBook source, int channelIndex, int segmentIndex) {
		super(source);
		this.channelIndex = channelIndex;
		this.segmentIndex = segmentIndex;
	}

	public BookEvent(MutableBook source, int segmentIndex) {
		super(source);
		this.segmentIndex = segmentIndex;
	}

	public BookEvent(MutableBook source, int channelIndex, int segmentIndex, int atomIndex) {
		super(source);
		this.channelIndex = channelIndex;
		this.segmentIndex = segmentIndex;
		this.atomIndex = atomIndex;
	}

	public int getChannelIndex() {
		return channelIndex;
	}

	public int getSegmentIndex() {
		return segmentIndex;
	}

	public int getAtomIndex() {
		return atomIndex;
	}

	public MutableBook getBookSource() {
		return ((MutableBook) source);
	}

}
