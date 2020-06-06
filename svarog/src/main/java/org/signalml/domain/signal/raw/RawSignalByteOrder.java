/* RawSignalByteOrder.java created 2008-01-27
 *
 */

package org.signalml.domain.signal.raw;

import java.nio.ByteOrder;
import org.springframework.context.MessageSourceResolvable;

/**
 * This class represents the order of bytes in the file with the raw signal
 * (little of big endian).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum RawSignalByteOrder implements MessageSourceResolvable {

	/**
	 * little endian byte {@link ByteOrder order}
	 */
	LITTLE_ENDIAN(ByteOrder.LITTLE_ENDIAN),
	/**
	 * big endian byte {@link ByteOrder order}
	 */
	BIG_ENDIAN(ByteOrder.BIG_ENDIAN)

	;

	/**
	 * the actual {@link ByteOrder order} of bytes
	 */
	private ByteOrder byteOrder;

	/**
	 * Constructor. Creates the order of bytes for the file with the
	 * raw signal using the given {@link ByteOrder order} of bytes
	 * @param byteOrder the order of bytes
	 */
	private RawSignalByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	/**
	 * Returns the {@link ByteOrder order} of bytes.
	 * @return the order of bytes
	 */
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
