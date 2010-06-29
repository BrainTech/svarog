/* RawSignalByteOrder.java created 2008-01-27
 *
 */

package org.signalml.domain.signal.raw;

import java.nio.ByteOrder;

import org.springframework.context.MessageSourceResolvable;

/** RawSignalByteOrder
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum RawSignalByteOrder implements MessageSourceResolvable {

	LITTLE_ENDIAN(ByteOrder.LITTLE_ENDIAN),
	BIG_ENDIAN(ByteOrder.BIG_ENDIAN)

	;

	private ByteOrder byteOrder;

	private RawSignalByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	public ByteOrder getByteOrder() {
		return byteOrder;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "byteOrder." + name() };
	}

	@Override
	public String getDefaultMessage() {
		return name();
	}

}
