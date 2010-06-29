/* SignalMLCodecManagerEvent.java created 2008-01-08
 *
 */

package org.signalml.codec;

import java.util.EventObject;

/** SignalMLCodecManagerEvent
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLCodecManagerEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private SignalMLCodec codec;
	private int index;

	public SignalMLCodecManagerEvent(Object source) {
		super(source);
	}

	public SignalMLCodecManagerEvent(Object source, SignalMLCodec codec, int index) {
		super(source);
		this.codec = codec;
		this.index = index;
	}

	public SignalMLCodec getCodec() {
		return codec;
	}

	public int getIndex() {
		return index;
	}

}
